/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import com.alibaba.fastjson.JSON;

import lombok.Data;

/**
 * <p>
 * created at 2023/10/24
 *
 * @author xzchaoo
 */
@Data
public class WindowStat {
  private int input;
  private int discardKeyLimit;
  private int error;
  private int filterWhere;
  private int discardMetaUnmatched;
  private int matchMultiMeta;

  public void incInput() {
    input++;
  }

  public void incDiscardKeyLimit() {
    discardKeyLimit++;
  }

  public void incError() {
    error++;
  }

  public void incFilterWhere() {
    filterWhere++;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

  public void incDiscardMetaUnmatched() {
    discardMetaUnmatched++;
  }

  public void incMatchMultiMeta() {
    matchMultiMeta++;
  }
}
