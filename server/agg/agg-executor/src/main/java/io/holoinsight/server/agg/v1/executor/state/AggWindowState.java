/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import io.holoinsight.server.agg.v1.core.conf.GroupBy;
import io.holoinsight.server.agg.v1.core.conf.GroupByItem;
import io.holoinsight.server.agg.v1.core.conf.JoinMeta;
import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import io.holoinsight.server.agg.v1.core.dict.Dict;
import io.holoinsight.server.agg.v1.executor.executor.AggMetaService;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import io.holoinsight.server.agg.v1.executor.executor.Group;
import io.holoinsight.server.agg.v1.executor.executor.WindowCompleteness;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import io.holoinsight.server.agg.v1.executor.output.WindowStat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * agg window state
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Getter
@Slf4j
public class AggWindowState {

  /**
   * Last preview emit time
   */
  @Setter
  transient long lastPreviewEmitTime;

  /**
   * This agg window has new input since last preview emit.
   */
  @Setter
  transient boolean previewDirty;
  /**
   * agg window timestamp
   */
  @Setter
  private long timestamp;
  /**
   * reused FixedSizeTags instance for {@link #groupMap}
   */
  @Getter(AccessLevel.NONE)
  private transient FixedSizeTags reused;
  /**
   * Groups
   */
  private Map<FixedSizeTags, Group> groupMap = new HashMap<>();
  /**
   * Window completeness info
   */
  @Setter
  private WindowCompleteness windowCompleteness = new WindowCompleteness();
  /**
   * window statistics
   */
  @Setter
  private WindowStat stat = new WindowStat();
  /**
   * Agg task related to this agg window state.
   */
  @Setter
  private XAggTask aggTask;

  /**
   * Get or create group
   * 
   * @param da
   * @param callback
   * @return null if group count exceeds limit
   */
  public Group getOrCreateGroup(DataAccessor da, AggMetaService aggMetaService,
      BiConsumer<AggWindowState, Group> callback) {
    FixedSizeTags groupKey = buildGroupKey(da, aggMetaService);
    if (groupKey == null) {
      if (stat.getDiscardMetaUnmatched() == 0) {
        log.warn("[agg] [{}] discard unmatch", aggTask.getInner().getAggId());
      }
      stat.incDiscardMetaUnmatched();
      return null;
    }

    Group g = groupMap.get(groupKey);
    if (g == null) {
      // check key limit
      if (groupMap.size() >= aggTask.getInner().getGroupBy().getKeyLimit()) {
        if (stat.getDiscardKeyLimit() == 0) {
          log.error("[agg] [{}] group key limit exceeded", aggTask.getInner().getAggId());
        }
        stat.incDiscardKeyLimit();
        return null;
      }

      FixedSizeTags clonedKey = cloneTags(groupKey);
      g = new Group();
      g.setTags(clonedKey);
      g.initGroupFields(aggTask.getSelect());
      groupMap.put(clonedKey, g);

      if (callback != null) {
        callback.accept(this, g);
      }
    }
    return g;
  }

  public boolean maybeCreateGroup(FixedSizeTags groupKey,
      BiConsumer<AggWindowState, Group> callback) {

    Group g = groupMap.get(groupKey);
    if (g != null) {
      return true;
    }

    // check key limit
    if (groupMap.size() >= aggTask.getInner().getGroupBy().getKeyLimit()) {
      return false;
    }

    g = new Group();
    g.setTags(groupKey);
    g.initGroupFields(aggTask.getSelect());
    groupMap.put(groupKey, g);
    if (callback != null) {
      callback.accept(this, g);
    }
    return true;
  }

  private FixedSizeTags buildGroupKey(DataAccessor da, AggMetaService aggMetaService) {
    if (!aggTask.getInner().hasGroupBy()) {
      return FixedSizeTags.EMPTY;
    }

    GroupBy groupBy = aggTask.getInner().getGroupBy();

    FixedSizeTags reused = this.internalGetReusedGroupKey();
    if (!fillTagValuesFromDataAccessor(groupBy, reused.getValues(), da, aggMetaService)) {
      return null;
    }

    return reused;
  }

  private boolean fillTagValuesFromDataAccessor(GroupBy gb, String[] values, DataAccessor da,
      AggMetaService aggMetaService) {
    List<GroupByItem> items = gb.getItems();
    Preconditions.checkArgument(values.length == items.size());
    for (int i = 0; i < items.size(); i++) {
      GroupByItem gbi = items.get(i);
      String value;

      JoinMeta joinMeta = gbi.getJoinMeta();
      if (joinMeta != null) {
        Map<String, Object> condition = buildCondition(da, joinMeta);
        List<Map<String, Object>> metas = aggMetaService.find(joinMeta.getMetaTable(), condition);
        if (metas.isEmpty()) {
          if (joinMeta.isDiscardIfUnmatch()) {
            return false;
          }
          value = gbi.getDefaultValue();
        } else {
          if (metas.size() > 1) {
            if (stat.getMatchMultiMeta() == 0) {
              stat.incMatchMultiMeta();
              log.warn("[agg] [{}] {} match more than 1 meta", //
                  this.aggTask.getInner().getAggId(), gb);
            }
          }
          value = gbi.getAsStringFromObject(metas.get(0));
        }
      } else {
        value = gbi.get(da);
      }
      values[i] = value;
    }
    return true;
  }

  public void reuseStrings() {
    groupMap.values().forEach(Group::reuseStrings);
  }

  public void add(Group x) {
    groupMap.put(x.getTags(), x);
  }

  public FixedSizeTags internalGetReusedGroupKey() {
    if (reused == null) {
      String[] keys = aggTask.getInner().getGroupBy().getGroupTagKeys();
      reused = new FixedSizeTags(keys);
    }
    reused.clearCache();
    return reused;
  }

  @NotNull
  private static Map<String, Object> buildCondition(DataAccessor da, JoinMeta joinMeta) {
    Map<String, JoinMeta.Value> condConf = joinMeta.getCondition();
    Map<String, Object> condition = Maps.newHashMapWithExpectedSize(condConf.size());
    for (Map.Entry<String, JoinMeta.Value> e : condConf.entrySet()) {
      // type==tag
      if ("tag".equals(e.getValue().getType())) {
        String tag = da.getTagOrDefault(e.getValue().getValue(), "-");
        condition.put(e.getKey(), tag);
      } else {
        // type==const
        condition.put(e.getKey(), e.getValue().getValue());
      }
    }
    return condition;
  }

  private static FixedSizeTags cloneTags(FixedSizeTags tags) {
    FixedSizeTags cloned = tags.clone();
    Dict.reuse(cloned.getValues());
    return cloned;
  }
}
