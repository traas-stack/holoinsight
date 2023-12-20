/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

/**
 * @author limengyang
 * @date 2023/7/19 17:39
 * @DESCRIPTION
 */
@Data
public class NotifyErrorMsg {

  private String notifyType;

  private String errMsg;

  private String address;

  private String notifyUser;
}
