/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.AggTaskValueTypes;
import io.holoinsight.server.agg.v1.core.conf.CompletenessConfig;
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

  /**
   * AggTaskExecutor should ignore data whose aligned ts < ignoredMinWatermark
   */
  long ignoredMinWatermark;

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
      AsyncOutput output) {
    this.state = Objects.requireNonNull(state);
    this.completenessService = Objects.requireNonNull(completenessService);
    this.output = Objects.requireNonNull(output);
  }

  /**
   * process agg task value
   *
   * @param latestAggTask latest agg task
   * @param aggTaskValue
   */
  public void process(XAggTask latestAggTask, AggProtos.AggTaskValue aggTaskValue) {
    switch (aggTaskValue.getType()) {
      case AggTaskValueTypes.COMPLETENESS_INFO:
        processCompletenessInfo(latestAggTask, aggTaskValue);
        break;
      default:
        processData(latestAggTask, aggTaskValue);
        processData2(latestAggTask, aggTaskValue);
        break;
    }
  }

  private void processData2(XAggTask latestAggTask, AggProtos.AggTaskValue aggTaskValue) {
    if (!aggTaskValue.hasDataTable()) {
      return;
    }

    long now = System.currentTimeMillis();
    AggWindowState lastWindowState = null;
    AggProtos.Table table = aggTaskValue.getDataTable();

    TableRowDataAccessor.Meta meta = new TableRowDataAccessor.Meta(table.getHeader());
    TableRowDataAccessor da = new TableRowDataAccessor();
    for (AggProtos.Table.Row row : table.getRowList()) {
      long alignedDataTs =
          Utils.align(row.getTimestamp(), latestAggTask.getInner().getWindow().getInterval());

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

      CompletenessUtils.processCompletenessInfoInData(w, da);

      // decide which group to use
      Group g = w.getOrCreateGroup(da, this::onGroupCreate);
      if (g == null) {
        w.getStat().incDiscardKeyLimit();
        log.error("[agg] [{}] group key limit exceeded", key());
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
    long now = System.currentTimeMillis();
    AggWindowState lastWindowState = null;
    InDataNodeDataAccessor da = new InDataNodeDataAccessor();
    for (AggProtos.InDataNode in : aggTaskValue.getInDataNodesList()) {

      long alignedDataTs =
          Utils.align(in.getTimestamp(), latestAggTask.getInner().getWindow().getInterval());

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

      CompletenessUtils.processCompletenessInfoInData(w, da);

      // decide which group to use
      Group g = w.getOrCreateGroup(da, this::onGroupCreate);
      if (g == null) {
        w.getStat().incDiscardKeyLimit();
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
  }

  private void onAggWindowCreate(AggWindowState w) {
    AggTaskKey key = key();

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
    state.setWatermark(watermark);

    Iterator<AggWindowState> iter = state.getAggWindowStateIter();
    while (iter.hasNext()) {
      AggWindowState w = iter.next();
      if (w.getTimestamp() < watermark) {
        try {
          doOutput(watermark, w);
        } catch (Exception e) {
          log.error("[agg] [{}] output error", key(), e);
        }
        iter.remove();
      }
    }
  }

  private void doOutput(long watermark, AggWindowState w) {
    XAggTask aggTask = w.getAggTask();

    MergedCompleteness mc = CompletenessUtils.buildMergedCompleteness(w);

    log.info("[agg] [{}] emit wm=[{}] ts=[{}] complete=[{}/{}] stat={} groups=[{}]", //
        key(), //
        Utils.formatTimeShort(watermark), //
        Utils.formatTimeShort(w.getTimestamp()), //
        mc.ok, //
        mc.total, //
        w.getStat(), //
        w.getGroupMap().size()); //

    Map<Integer, XOutput.Batch> batches = new HashMap<>();
    XOutput.WindowInfo windowInfo = new XOutput.WindowInfo(w.getTimestamp(), mc, w.getStat());

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
      output.write(batch);
    }
  }

  AggTaskKey key() {
    return state.getKey();
  }

}
