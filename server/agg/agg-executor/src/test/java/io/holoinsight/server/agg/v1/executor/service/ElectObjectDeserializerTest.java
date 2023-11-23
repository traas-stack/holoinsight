/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.service;

import io.holoinsight.server.agg.v1.core.conf.SelectItem;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * <p>
 * created at 2023/10/7
 *
 * @author xzchaoo
 */
public class ElectObjectDeserializerTest {
  @Test
  public void test() {
    SelectItem si = JSON.parseObject("{\"elect\":\"a.b\"}", SelectItem.class);
    System.out.println(si.getElect());
    si = JSON.parseObject("{\"elect\":{\"metric\":\"a\",\"field\":\"b\"}}", SelectItem.class);
    System.out.println(si.getElect());
  }
}
