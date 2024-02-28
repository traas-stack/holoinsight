/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.conf.AggTaskValueTypes;
import io.holoinsight.server.agg.v1.core.conf.AggTaskVersion;
import io.holoinsight.server.agg.v1.core.conf.CompletenessConfig;
import io.holoinsight.server.agg.v1.core.conf.FillZero;
import io.holoinsight.server.agg.v1.core.conf.From;
import io.holoinsight.server.agg.v1.core.conf.FromConfigs;
import io.holoinsight.server.agg.v1.core.conf.GroupBy;
import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.InDataNodeDataAccessor;
import io.holoinsight.server.agg.v1.core.data.TableRowDataAccessor;
import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.ExpectedCompleteness;
import io.holoinsight.server.agg.v1.executor.output.AsyncOutput;
import io.holoinsight.server.agg.v1.executor.output.MergedCompleteness;
import io.holoinsight.server.agg.v1.executor.output.XOutput;
import io.holoinsight.server.agg.v1.executor.state.AggTaskState;
import io.holoinsight.server.agg.v1.executor.state.AggWindowState;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import io.holoinsight.server.common.ProtoJsonUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Slf4j
@Getter
public class AggTaskExecutor {
  /**
   * Intermediate state of aggregation tasks
   */
  private final AggTaskState state;
  private final CompletenessService completenessService;
  private final AsyncOutput output;
  private final AggMetaService aggMetaService;

  /**
   * AggTaskExecutor should ignore data whose aligned ts < ignoredMinWatermark
   */
  long ignoredMinWatermark;

  /**
   * This agg task executor is recovered from persistent state with offset = restoredOffset
   */
  long restoredOffset = -1;

  /**
   * deletion mark
   */
  @Getter(AccessLevel.NONE)
  transient boolean toBeDeleted;

  /**
   * last used agg task
   */
  @Nullable
  @Getter(AccessLevel.NONE)
  transient XAggTask lastUsedAggTask;

  public AggTaskExecutor(AggTaskState state, CompletenessService completenessService,
      AsyncOutput output, AggMetaService aggMetaService) {
    this.state = Objects.requireNonNull(state);
    this.completenessService = Objects.requireNonNull(completenessService);
    this.output = Objects.requireNonNull(output);
    this.aggMetaService = Objects.requireNonNull(aggMetaService);
  }

  /**
   * process agg task value
   *
   * @param latestAggTask latest agg task
   * @param aggTaskValue
   */
  public void process(XAggTask latestAggTask, AggProtos.AggTaskValue aggTaskValue) {
    boolean debug = latestAggTask.getInner().getExtension().isDebug();
    if (debug) {
      log.info("[agg] [debug] [{}] input={}", key(), ProtoJsonUtils.toJson(aggTaskValue));
    }


    switch (aggTaskValue.getType()) {
      case AggTaskValueTypes.COMPLETENESS_INFO:
        processCompletenessInfo(latestAggTask, aggTaskValue);
        break;
      default:
        if (aggTaskValue.hasDataTable()) {
          processData2(latestAggTask, aggTaskValue);
        } else {
          try {
            processData(latestAggTask, aggTaskValue);
          } catch (Exception e) {
            log.error("[agg] [{}] error", key(), e);
          }
        }
        break;
    }
  }

  private void processData2(XAggTask latestAggTask, AggProtos.AggTaskValue aggTaskValue) {
    long now = System.currentTimeMillis();
    AggWindowState lastWindowState = null;
    AggProtos.Table table = aggTaskValue.getDataTable();

    TableRowDataAccessor.Meta meta = new TableRowDataAccessor.Meta(table.getHeader());
    TableRowDataAccessor da = new TableRowDataAccessor();
    for (AggProtos.Table.Row row : table.getRowList()) {
      long alignedDataTs = Utils.align(row.getTimestamp(), latestAggTask.getInner().getWindow());

      if (alignedDataTs > now) {
        log.error("[agg] [{}] invalid data {}]", key(), row);
        continue;
      }

      if (alignedDataTs < ignoredMinWatermark) {
        continue;
      }

      boolean late = alignedDataTs < state.getWatermark();
      if (late) {
        log.error("[agg] [{}] data late, ts=[{}] wm=[{}]", key(), alignedDataTs,
            state.getWatermark());
        continue;
      }

      state.updateMaxEventTimestamp(row.getTimestamp());

      da.replace(meta, row);

      // decide which time window to use
      // 99% case: all InDataNodes have same timestamp
      AggWindowState w;
      if (lastWindowState != null && lastWindowState.getTimestamp() == alignedDataTs) {
        w = lastWindowState;
      } else {
        w = state.getAggWindowState(alignedDataTs);
      }
      if (w != null) {
        lastWindowState = w;
        // use aggTask of the existing time window
        if (!w.getAggTask().getWhere().test(da)) {
          continue;
        }
      } else {
        // use latest aggTask
        if (!latestAggTask.getWhere().test(da)) {
          continue;
        }
      }
      if (w == null) {
        w = state.getOrCreateAggWindowState(alignedDataTs, latestAggTask, this::onAggWindowCreate);
        lastWindowState = w;
      }

      w.getStat().incInput();
      w.setPreviewDirty(true);

      CompletenessUtils.processCompletenessInfoInData(w, da);

      // decide which group to use
      Group g = w.getOrCreateGroup(da, aggMetaService, this::onGroupCreate);
      if (g == null) {
        continue;
      }

      // do agg
      try {
        g.agg(w.getAggTask(), aggTaskValue, da);
      } catch (Exception e) {
        log.error("[agg] [{}] agg error {}", key(), da, e);
      }

    }
  }

  private void processData(XAggTask latestAggTask, AggProtos.AggTaskValue aggTaskValue) {
    boolean debug = latestAggTask.getInner().getExtension().isDebug();
    if (debug) {
      log.info("[agg] [debug] [{}] input={}", key(), ProtoJsonUtils.toJson(aggTaskValue));
    }

    long now = System.currentTimeMillis();
    AggWindowState lastWindowState = null;
    InDataNodeDataAccessor da = new InDataNodeDataAccessor();
    for (AggProtos.InDataNode in : aggTaskValue.getInDataNodesList()) {
      long alignedDataTs = Utils.align(in.getTimestamp(), latestAggTask.getInner().getWindow());

      if (alignedDataTs > now) {
        log.error("[agg] [{}] invalid data {}]", key(), in);
        continue;
      }

      if (alignedDataTs < ignoredMinWatermark) {
        continue;
      }

      boolean late = alignedDataTs < state.getWatermark();
      if (late) {
        log.error("[agg] [{}] data late, ts=[{}] wm=[{}]", key(), alignedDataTs,
            state.getWatermark());
        continue;
      }

      state.updateMaxEventTimestamp(in.getTimestamp());

      // decide which time window to use
      // 99% case: all InDataNodes have same timestamp
      da.replace(in);
      AggWindowState w;
      if (lastWindowState != null && lastWindowState.getTimestamp() == alignedDataTs) {
        w = lastWindowState;
      } else {
        w = state.getAggWindowState(alignedDataTs);
      }
      if (w != null) {
        lastWindowState = w;
        // use aggTask of the existing time window
        if (!w.getAggTask().getWhere().test(da)) {
          w.getStat().incFilterWhere();
          continue;
        }
      } else {
        // use latest aggTask
        if (!latestAggTask.getWhere().test(da)) {
          // w.getStat().incFilterWhere();
          continue;
        }
      }

      if (w == null) {
        w = state.getOrCreateAggWindowState(alignedDataTs, latestAggTask, this::onAggWindowCreate);
        lastWindowState = w;
      }

      w.getStat().incInput();
      w.setPreviewDirty(true);

      CompletenessUtils.processCompletenessInfoInData(w, da);

      // decide which group to use
      Group g = w.getOrCreateGroup(da, aggMetaService, this::onGroupCreate);
      if (g == null) {
        continue;
      }

      // do agg
      try {
        g.agg(w.getAggTask(), aggTaskValue, da);
      } catch (Exception e) {
        w.getStat().incError();
        log.error("[agg] [{}] agg error {}", key(), in, e);
      }
    }
  }

  private void processCompletenessInfo(XAggTask latestAggTask,
      AggProtos.AggTaskValue aggTaskValue) {
    long now = System.currentTimeMillis();
    long ts = aggTaskValue.getTimestamp();
    if (ts > now) {
      log.error("[agg] [{}] invalid data {}]", key(), aggTaskValue);
      return;
    }

    if (ts < ignoredMinWatermark) {
      return;
    }

    boolean late = ts < state.getWatermark();
    if (late) {
      log.error("[agg] [{}] data late, ts=[{}] wm=[{}]", key(), ts, state.getWatermark());
      return;
    }

    state.updateMaxEventTimestamp(ts);

    AggWindowState w = state.getOrCreateAggWindowState(ts, latestAggTask, this::onAggWindowCreate);

    CompletenessUtils.processCompletenessInfo(w, aggTaskValue);

    w.setPreviewDirty(true);
  }

  private void onAggWindowCreate(AggWindowState w) {
    AggTaskKey key = key();
    log.info("[agg] [{}] window created ts=[{}] aggVersion=[{}]", //
        key, Utils.formatTimeShort(w.getTimestamp()), w.getAggTask().getInner().getVersion());

    From from = w.getAggTask().getInner().getFrom();
    CompletenessConfig completenessConfig = from.getCompleteness();
    if (completenessConfig.getMode() == CompletenessConfig.Mode.NONE) {
      return;
    }

    List<String> tables;
    boolean dataMode = completenessConfig.getMode() == CompletenessConfig.Mode.DATA;
    if (dataMode) {
      String table = completenessConfig.getDimTable();
      if (table.contains("%s")) {
        table = String.format(table, key.getTenant());
      }
      tables = Collections.singletonList(table);
    } else {
      FromConfigs fc = from.getConfigs();
      if (fc == null) {
        return;
      }
      tables = new ArrayList<>(fc.getTableNames());
    }

    WindowCompleteness wc = w.getWindowCompleteness();
    GroupBy cgb = completenessConfig.getGroupBy();
    FixedSizeTags reused = new FixedSizeTags(cgb.getGroupTagKeys());
    wc.setReused(reused);

    for (String table : tables) {
      ExpectedCompleteness ec;
      if (dataMode) {
        ec = completenessService.getByDimTable(table);
      } else {
        ec = completenessService.getByCollectTableName(table);
      }

      if (ec == null) {
        log.error("ExpectedCompleteness is nil, table=[{}]", table);
        continue;
      }

      if (dataMode) {
        table = "-";
      }
      TableCompleteness tc = new TableCompleteness(table);

      for (Map<String, Object> target : ec.getExpectedTargets()) {
        String targetKey;
        if (dataMode) {
          targetKey = (String) target.get(completenessConfig.getTargetKey());
        } else {
          targetKey = (String) target.get("_uk");
        }
        if (targetKey == null) {
          continue;
        }
        wc.getAllTargets().put(targetKey, target);

        cgb.fillTagValuesFromObject(reused.getValues(), target);
        reused.clearCache();

        TableCompleteness.Group detail = tc.getGroups().get(reused);
        if (detail == null) {
          FixedSizeTags clone = reused.clone();
          detail = new TableCompleteness.Group(clone);
          tc.getGroups().put(clone, detail);
        }
        detail.getPending().put(targetKey, target);
      }
      for (TableCompleteness.Group detail : tc.getGroups().values()) {
        detail.setTotal(detail.getPending().size());
      }

      wc.getTables().put(table, tc);
    }
  }

  private void onGroupCreate(AggWindowState w, Group g) {}

  void maybeEmit(long watermark) {
    // If the current instance is restored from the persistent state, when it is in the
    // recalculation phase, watermark <= state.watermark will occur, and we need to ignore such
    // situations.
    if (watermark <= state.getWatermark()) {
      return;
    }

    maybeFillZero(watermark);

    maybeEmit0(watermark);
  }

  private void maybeEmit0(long watermark) {
    state.setWatermark(watermark);
    Iterator<AggWindowState> iter = state.getAggWindowStateIter();
    long now = System.currentTimeMillis();

    while (iter.hasNext()) {
      AggWindowState w = iter.next();
      AggTask inner = w.getAggTask().getInner();

      boolean complete = w.getTimestamp() < watermark;

      boolean preview = !complete && inner.getOutput().isPreview() && w.isPreviewDirty()
          && w.getLastPreviewEmitTime() < now - inner.getWindow().getPreviewEmitInterval();

      if (complete || preview) {
        w.setPreviewDirty(false);
        w.setLastPreviewEmitTime(now);

        try {
          doOutput(watermark, w, preview);
        } catch (Exception e) {
          log.error("[agg] [{}] output error", key(), e);
        }

        if (complete) {
          iter.remove();
        }
      }
    }
  }

  private void maybeFillZero(long watermark) {
    FillZero fillZero = lastUsedAggTask.getInner().getFillZero();

    if (!fillZero.isEnabled() || state.getWatermark() <= 0) {
      return;
    }

    AggTaskVersion historyTagsVersion = state.getHistoryTagsVersion();
    Map<FixedSizeTags, Long> historyTags = state.getHistoryTags();

    long interval = lastUsedAggTask.getInner().getWindow().getInterval();
    long lastEmitTimestamp = Math.max( //
        // This value may be small if the state is restored from a very old state.
        Utils.align(state.getWatermark() - 1, lastUsedAggTask.getInner().getWindow()),
        // So we restrict it to be within the last 60 periods of the watermark
        Utils.align(watermark - 1, lastUsedAggTask.getInner().getWindow()) - 60 * interval);

    for (long ts = lastEmitTimestamp + interval; ts < watermark; ts += interval) {
      AggWindowState w = state.getAggWindowState(ts);

      if (w == null) {
        w = state.getOrCreateAggWindowState(ts, lastUsedAggTask, null);
        log.info("[agg] [{}] create empty window [{}]", key(), Utils.formatTimeShort(ts));
      }

      if (historyTagsVersion.isEmpty()) {
        historyTagsVersion.updateTo(w.getAggTask().getInner());
        historyTags.clear();
      } else if (!historyTagsVersion.hasSameVersion(w.getAggTask().getInner())) {
        // this is the last window
        if (ts + interval >= watermark
            && w.getAggTask().getInner().getVersion() > historyTagsVersion.getVersion()) {
          log.info("[agg] [{}] tags version changed, clear history tags {} {}/{}", key(),
              historyTagsVersion, w.getAggTask().getInner().getId(),
              w.getAggTask().getInner().getVersion());
          historyTagsVersion.updateTo(w.getAggTask().getInner());
          historyTags.clear();
        } else {
          log.info("[agg] [{}] tags version changed, no fill history tags {} {}/{}", //
              key(), historyTagsVersion, w.getAggTask().getInner().getId(),
              w.getAggTask().getInner().getVersion());
          continue;
        }
      }

      log.info("[agg] [{}] ts=[{}] groups={} history={}", //
          key(), Utils.formatTime(ts), w.getGroupMap().size(), historyTags.size());

      if (historyTags.size() > 0) {
        List<FixedSizeTags> backup = new ArrayList<>(w.getGroupMap().keySet());
        for (FixedSizeTags tags : historyTags.keySet()) {
          w.maybeCreateGroup(tags, this::onGroupCreate);
        }
        for (FixedSizeTags tags : backup) {
          historyTags.put(tags, w.getTimestamp());
        }
      } else {
        for (FixedSizeTags tags : w.getGroupMap().keySet()) {
          historyTags.put(tags, w.getTimestamp());
        }
      }
    }

    long expiredTime = watermark - fillZero.getExpireTime();

    Iterator<Map.Entry<FixedSizeTags, Long>> iter = historyTags.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<FixedSizeTags, Long> e = iter.next();
      if (e.getValue() < expiredTime) {
        iter.remove();
        log.info("[agg] [{}] delete expired history tags {}", key(), e.getKey());
      }
    }

    if (historyTags.size() > fillZero.getKeyLimit()) {
      // randomly remove some history tags
      int count = historyTags.size() - fillZero.getKeyLimit();
      Iterator<Map.Entry<FixedSizeTags, Long>> iter2 = historyTags.entrySet().iterator();
      while (!historyTags.isEmpty() && historyTags.size() > fillZero.getKeyLimit()) {
        iter2.next();
        iter2.remove();
      }
      log.info("[agg] [{}] clear {} redundant history tags", key(), count);
    }

  }

  private void doOutput(long watermark, AggWindowState w, boolean preview) {
    XAggTask aggTask = w.getAggTask();

    MergedCompleteness mc = CompletenessUtils.buildMergedCompleteness(w);

    log.info("[agg] [{}] [{}] emit wm=[{}] ts=[{}] complete=[{}/{}] stat={} groups=[{}]", //
        key(), //
        preview ? "preview" : "complete", //
        Utils.formatTimeShort(watermark), //
        Utils.formatTimeShort(w.getTimestamp()), //
        mc.ok, //
        mc.total, //
        w.getStat(), //
        w.getGroupMap().size()); //

    Map<Integer, XOutput.Batch> batches = new HashMap<>();
    XOutput.WindowInfo windowInfo =
        new XOutput.WindowInfo(w.getTimestamp(), mc, w.getStat(), preview);

    for (Group g : w.getGroupMap().values()) {
      Map<String, Object> env = g.getFinalFields1(w.getAggTask());
      if (MapUtils.isEmpty(env)) {
        log.error("[agg] [{}] MapUtils.isEmpty(env)", key());
        continue;
      }

      List<OutputItem> outputItems = aggTask.getInner().getOutput().getItems();
      for (int i = 0; i < outputItems.size(); i++) {
        OutputItem oi = outputItems.get(i);
        Map<String, Object> finalFields = g.getFinalFields(oi, env);
        if (MapUtils.isEmpty(finalFields)) {
          log.error("[agg] [{}] MapUtils.isEmpty(finalFields)", key());
          continue;
        }

        XOutput.Batch batch =
            batches.computeIfAbsent(i, x -> new XOutput.Batch(key(), oi, windowInfo));
        batch.groups.add(new XOutput.Group(g.getTags(), finalFields));
      }
    }

    for (XOutput.Batch batch : batches.values()) {
      // process topn after agg
      OutputItem.Topn topn = batch.oi.getTopn();
      if (topn != null && topn.isEnabled() && topn.getLimit() < batch.groups.size()) {
        String orderBy = topn.getOrderBy();

        Comparator<XOutput.Group> comparator = Comparator.comparingDouble(
            x -> ((Number) x.getFinalFields().getOrDefault(orderBy, 0D)).doubleValue());

        if (!topn.isAsc()) {
          comparator = comparator.reversed();
        }

        batch.groups = batch.groups.stream() //
            .sorted(comparator) //
            .limit(topn.getLimit()) //
            .collect(Collectors.toList()); //
      }

      if (batch.groups.isEmpty()) {
        continue;
      }
      output.write(batch);
    }
  }

  AggTaskKey key() {
    return state.getKey();
  }

}
