/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import io.holoinsight.server.registry.model.integration.GaeaTask;
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
public class SqlTask extends GaeaTask {
  private Select select;
  private From from;
  private Where where;
  private GroupBy groupBy;
  private Window window;
  private Output output;
  private ExecuteRule executeRule;
}
