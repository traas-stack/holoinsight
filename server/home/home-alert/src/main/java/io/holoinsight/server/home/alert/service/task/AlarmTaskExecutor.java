/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

/**
 * @author wangsiyuan
 * @date 2022/2/28 5:36 下午
 */
public interface AlarmTaskExecutor<T> {

  public void process(T t);

}
