/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.hash.Hashing;

import io.holoinsight.server.agg.v1.core.conf.AggFunc;
import io.holoinsight.server.agg.v1.core.conf.SelectItem;
import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import io.holoinsight.server.agg.v1.core.data.LogSamples;
import io.holoinsight.server.agg.v1.executor.state.LogSamplesState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Slf4j
@Data
public class GroupField {
  private transient AggFunc agg;
  private int input;
  private int count;
  private double value;
  private LogSamplesState logSamples;
  private TopnState topn;
  private HllState hll;

  public GroupField() {}

  public void add(DataAccessor da, SelectItem si) {
    input++;

    double f64;
    switch (agg.getTypeInt()) {
      case AggFunc.TYPE_SUM:
      case AggFunc.TYPE_AVG:
        count++;
        value += da.getFloat64Field();
        break;
      case AggFunc.TYPE_MIN:
        f64 = da.getFloat64Field();
        if (count == 0 || f64 < value) {
          value = f64;
        }
        count++;
        break;
      case AggFunc.TYPE_MAX:
        f64 = da.getFloat64Field();
        if (count == 0 || f64 > value) {
          value = f64;
        }
        count++;
        break;
      case AggFunc.TYPE_COUNT:
        count++;
        break;
      case AggFunc.TYPE_AVG_MERGE:
        count += da.getCount();
        value += da.getCount() * da.getFloat64Field();
        break;
      case AggFunc.TYPE_LOGSAMPLES_MERGE: {
        if (logSamples != null && logSamples.isFull()) {
          return;
        }
        LogSamples ls = JSON.parseObject(da.getStringField(), LogSamples.class);
        if (logSamples == null) {
          logSamples = new LogSamplesState();
          logSamples.setMaxCount(ls.getMaxCount());

          if (agg.getLogSamplesMerge() != null) {
            logSamples.setStrategy(agg.getLogSamplesMerge().getStrategy());
          }
        }
        logSamples.add(ls.getSamples());
        break;
      }
      case AggFunc.TYPE_TOPN: {
        if (topn == null) {
          AggFunc.TopnParams params = agg.getTopn();
          if (params == null) {
            return;
          }
          topn = new TopnState(params);
        }

        topn.add(da);
        break;
      }
      case AggFunc.TYPE_HLL:
        if (hll == null) {
          hll = new HllState();
        }
        String v = da.getTag(si.getElect().getField());
        if (v != null) {
          int x =
              Hashing.murmur3_32().newHasher().putString(v, StandardCharsets.UTF_8).hash().asInt();
          hll.hll.addRaw(x);
        }
        break;
    }
  }

  @JSONField(serialize = false)
  public Object getFinalValue() {
    switch (agg.getTypeInt()) {
      case AggFunc.TYPE_AVG:
      case AggFunc.TYPE_AVG_MERGE:
        return count > 0 ? value / count : 0;
      case AggFunc.TYPE_COUNT:
        return count;
      case AggFunc.TYPE_LOGSAMPLES_MERGE:
        if (logSamples == null) {
          return null;
        }
        LogSamples ls = new LogSamples();
        ls.setMaxCount(logSamples.getMaxCount());
        ls.setSamples(new ArrayList<>());

        if (logSamples.getAccumulated() != null) {
          ls.getSamples().addAll(logSamples.getAccumulated());
        }
        if (logSamples.getPending() != null) {
          ls.getSamples().addAll(logSamples.getPending());
        }
        return ls;
      case AggFunc.TYPE_TOPN: {
        if (topn != null) {
          return topn.getFinalValue();
        }
        return null;
      }
      case AggFunc.TYPE_MAX:
      case AggFunc.TYPE_MIN:
      case AggFunc.TYPE_SUM:
        return value;
      case AggFunc.TYPE_HLL:
        if (hll == null) {
          return 0;
        }
        return hll.hll.cardinality();
      default:
        throw new IllegalStateException("unsupported");
    }
  }

}
