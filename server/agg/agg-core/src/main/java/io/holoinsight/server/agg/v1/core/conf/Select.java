/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.List;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class Select {
  private List<SelectItem> items;
}
