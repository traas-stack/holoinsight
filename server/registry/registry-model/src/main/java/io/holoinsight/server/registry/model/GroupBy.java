/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class GroupBy {
  private int maxKeys;
  private List<Group> groups;

  @ToString
  @Getter
  @Setter
  public static class Group {
    private String name;
    private Elect elect;
  }
}
