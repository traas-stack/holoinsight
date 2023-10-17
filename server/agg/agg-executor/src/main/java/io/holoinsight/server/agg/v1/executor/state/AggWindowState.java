/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import io.holoinsight.server.agg.v1.core.conf.GroupBy;
import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import io.holoinsight.server.agg.v1.core.dict.Dict;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import io.holoinsight.server.agg.v1.executor.executor.Group;
import io.holoinsight.server.agg.v1.executor.executor.WindowCompleteness;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * agg window state
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Data
@Slf4j
public class AggWindowState {

  /**
   * agg window timestamp
   */
  private long timestamp;
  private transient FixedSizeTags reused;
  private Map<FixedSizeTags, Group> groupMap = new HashMap<>();
  private WindowCompleteness windowCompleteness = new WindowCompleteness();
  private int input;
  private XAggTask aggTask;

  public Group getOrCreateGroup(DataAccessor da, BiConsumer<AggWindowState, Group> callback) {
    FixedSizeTags groupKey = buildGroupKey(da);

    Group g = groupMap.get(groupKey);
    if (g == null) {
      // check key limit
      if (groupMap.size() >= aggTask.getInner().getGroupBy().getKeyLimit()) {
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

  private FixedSizeTags buildGroupKey(DataAccessor da) {
    if (!aggTask.getInner().hasGroupBy()) {
      return FixedSizeTags.EMPTY;
    }

    GroupBy groupBys = aggTask.getInner().getGroupBy();

    FixedSizeTags reused = this.internalGetReusedGroupKey();
    groupBys.fillTagValuesFromDataAccessor(reused.getValues(), da);

    return reused;
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

  @Deprecated
  public void addInput() {
    input++;
  }

  private static FixedSizeTags cloneTags(FixedSizeTags gk) {
    FixedSizeTags cloned = gk.clone();
    Dict.reuse(cloned.getValues());
    return cloned;
  }

}
