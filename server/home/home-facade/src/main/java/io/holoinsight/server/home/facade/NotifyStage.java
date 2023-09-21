/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.List;

/**
 * @author limengyang
 * @date 2023/7/19 17:35
 * @DESCRIPTION
 */
@Data
public class NotifyStage {

  private String stage;

  private List<Steps> steps;

}
