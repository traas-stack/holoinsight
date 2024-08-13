/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * created at 2023/11/27
 *
 * @author xzchaoo
 */
@Data
public class Header {
  private List<String> tagKeys;
  private List<String> fieldKeys;

  public Header() {}

  public Header(List<String> tagKeys, List<String> fieldKeys) {
    this.tagKeys = tagKeys;
    this.fieldKeys = fieldKeys;
  }
}
