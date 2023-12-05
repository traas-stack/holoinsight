/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmGroup.java, v 0.1 2022年04月08日 3:10 下午 jinsong.yjs Exp $
 */
@Data
public class AlarmGroup {

  @TableId(type = IdType.AUTO)
  public Long id;
  public String tenant;

  public String workspace;

  public String groupName;

  public String groupInfo;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  public String emailAddress;

  public String ddWebhook;

  public String dyvmsPhone;

  public String smsPhone;

  /**
   * 环境类型
   */
  @Column(name = "env_type")
  private String envType;
}
