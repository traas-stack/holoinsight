/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis;

/**
 * @author xiangwanpeng
 * @version : Mergable.java, v 0.1 2022年12月08日 15:03 xiangwanpeng Exp $
 */
public interface Mergable {
  void merge(Mergable other);
}
