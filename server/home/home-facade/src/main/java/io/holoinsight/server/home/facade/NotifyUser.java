/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

/**
 * @author limengyang
 * @date 2023/7/20 15:55
 * @DESCRIPTION
 */
@Data
public class NotifyUser {

  private String notifyChannel;

  private String user;
}
