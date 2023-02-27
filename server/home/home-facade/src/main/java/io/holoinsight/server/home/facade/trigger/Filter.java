/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/4/18 7:41 下午
 */
@Data
public class Filter {

  private String type;

  private String name;

  private String value;

}
