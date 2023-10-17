/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.CompletenessConfig;
import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import io.holoinsight.server.agg.v1.core.dict.Dict;
import io.holoinsight.server.agg.v1.executor.output.MergedCompleteness;
import io.holoinsight.server.agg.v1.executor.state.AggWindowState;
import io.holoinsight.server.agg.v1.pb.AggProtos;

/**
 * <p>
 * created at 2023/10/8
 *
 * @author xzchaoo
 */
final class CompletenessUtils {
  private CompletenessUtils() {}

  public static void processCompletenessInfo(AggWindowState w,
      AggProtos.AggTaskValue aggTaskValue) {
    String refCollectKey = Dict.get(aggTaskValue.getExtensionOrDefault("refCollectKey", null));
    String refTargetKey = Dict.get(aggTaskValue.getExtensionOrDefault("refTargetKey", null));
    boolean ok = "true".equals(aggTaskValue.getExtensionOrDefault("ok", ""));
    if (refCollectKey == null || refTargetKey == null) {
      return;
    }

    CompletenessConfig completenessConfig = w.getAggTask().getInner().getFrom().getCompleteness();

    WindowCompleteness wc = w.getWindowCompleteness();
    TableCompleteness tc = wc.getTables().get(refCollectKey);
    if (tc == null) {
      return;
    }
    String targetKey = StringUtils.removeStart(refTargetKey, "dim2:");
    Map<String, Object> target = wc.getAllTargets().get(targetKey);
    if (target == null) {
      return;
    }
    FixedSizeTags reused = wc.getReusedTags();
    completenessConfig.getGroupBy().fillTagValuesFromObject(reused.getValues(), target);

    TableCompleteness.Group detail = tc.getGroups().get(reused);
    if (detail == null) {
      return;
    }

    detail.getPending().remove(targetKey);
    if (!ok) {
      detail.getError().put(targetKey, target);
    }
  }

  static MergedCompleteness buildMergedCompleteness(AggWindowState w) {
    CompletenessConfig completenessConfig = w.getAggTask().getInner().getFrom().getCompleteness();
    if (completenessConfig.getMode() == CompletenessConfig.Mode.NONE) {
      return new MergedCompleteness();
    }
    List<String> keepTargetKeys = completenessConfig.getKeepTargetKeys();

    MergedCompleteness mc = new MergedCompleteness();
    Map<FixedSizeTags, MergedCompleteness.GroupCompleteness> groups = new HashMap<>();

    WindowCompleteness wc = w.getWindowCompleteness();

    for (TableCompleteness tc0 : wc.getTables().values()) {
      for (TableCompleteness.Group detail : tc0.getGroups().values()) {
        MergedCompleteness.GroupCompleteness gc = groups.get(detail.getTags());
        if (gc == null) {
          gc = new MergedCompleteness.GroupCompleteness();
          gc.setTags(detail.getTags().asMap());
          groups.put(detail.getTags(), gc);
        }

        MergedCompleteness.TableCompleteness tc = new MergedCompleteness.TableCompleteness();

        if (detail.getError() != null) {
          tc.setError(detail.getError().size());
          for (Map.Entry<String, Map<String, Object>> e3 : detail.getError().entrySet()) {
            Map<String, Object> smallTarget = Utils.copyMapWithKeys(e3.getValue(), keepTargetKeys);
            tc.addError(new MergedCompleteness.Target(e3.getKey(), smallTarget));
            if (tc.getErrorTargets().size() >= MergedCompleteness.MAX_FAILURE_RECORD_COUNT) {
              break;
            }
          }
        }

        tc.setMissing(detail.getPending().size());
        for (Map.Entry<String, Map<String, Object>> e3 : detail.getPending().entrySet()) {
          Map<String, Object> smallTarget = Utils.copyMapWithKeys(e3.getValue(), keepTargetKeys);
          tc.addMissing(new MergedCompleteness.Target(e3.getKey(), smallTarget));
          if (tc.getMissingTargets().size() >= MergedCompleteness.MAX_FAILURE_RECORD_COUNT) {
            break;
          }
        }

        tc.setTotal(detail.getTotal());
        tc.setOk(tc.getTotal() - tc.getMissing() - tc.getError());
        gc.getTables().put(tc0.getTable(), tc);

        mc.setOk(mc.getOk() + tc.getOk());
        mc.setTotal(mc.getTotal() + tc.getTotal());
      }
    }

    mc.getDetails().addAll(groups.values());

    return mc;
  }

  static void processCompletenessInfoInData(AggWindowState w, DataAccessor da) {
    CompletenessConfig completenessConfig = w.getAggTask().getInner().getFrom().getCompleteness();
    if (completenessConfig.getMode() != CompletenessConfig.Mode.DATA) {
      return;
    }
    WindowCompleteness wc = w.getWindowCompleteness();
    String targetKey = da.getTag(completenessConfig.getTargetKey());
    if (targetKey == null) {
      return;
    }
    Map<String, Object> target = wc.getAllTargets().get(targetKey);
    if (target == null) {
      return;
    }
    TableCompleteness tc = wc.getTables().get("-");
    if (tc == null) {
      return;
    }

    if (!tc.getProcessed().add(targetKey)) {
      return;
    }

    FixedSizeTags reused = wc.getReusedTags();
    completenessConfig.getGroupBy().fillTagValuesFromObject(reused.getValues(), target);
    TableCompleteness.Group detail = tc.getGroups().get(reused);
    if (detail == null) {
      return;
    }

    detail.getPending().remove(targetKey);
  }

}
