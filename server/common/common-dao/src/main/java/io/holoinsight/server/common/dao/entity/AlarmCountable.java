/*
 * Ant Group Copyright (c) 2004-2024 All Rights Reserved.
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
