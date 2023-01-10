/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data;

/**
 * @author wangsiyuan
 * @date 2022/7/12 11:22 上午
 */
public interface AlarmDataProcessor<T, E> {
  E process(T task);
}
