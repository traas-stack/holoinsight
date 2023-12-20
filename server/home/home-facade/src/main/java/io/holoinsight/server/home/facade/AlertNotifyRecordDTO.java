/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author limengyang
 * @date 2023/7/17 9:32
 */
@Data
public class AlertNotifyRecordDTO {
  /**
   * id
   */
  private Long id;

  /**
   * traceId
   */
  private String traceId;

  /**
   * gmtCreate
   */
  private Date gmtCreate;

  private Date gmtModified;

  /**
   * alert history detail id
   */
  private Long historyDetailId;

  /**
   * alert history id
   */
  private Long historyId;

  /**
   * alert exception time
   */
  private Date notifyErrorTime;

  /**
   * alert success
   */
  private Byte isSuccess;

  /**
   * notify channel
   */
  private String notifyChannel;

  /**
   * notify target detail
   */
  private List<NotifyUser> notifyUser;

  /**
   * exception node
   */
  private List<NotifyErrorMsg> notifyErrorNode;

  /**
   * tenant
   */
  private String tenant;

  /**
   * alert compute detail
   */
  private List<NotifyStage> notifyStage;

  /**
   * env type
   */
  private String envType;

  private String workspace;

  private String uniqueId;

  private String ruleName;

  private String triggerResult;

  private Boolean isRecord;
}
