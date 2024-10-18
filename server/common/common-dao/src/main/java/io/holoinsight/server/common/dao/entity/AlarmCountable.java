/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import lombok.Data;

/**
 * 报警计数
 *
 * @author limengyang
 * @version AlarmCountable.java, v 0.1 2024年09月19日 17:38 limengyang
 */
@Data
public class AlarmCountable {
  public Long customPluginId;
  public Long parentFolderId;
  public Long historyId;
}
