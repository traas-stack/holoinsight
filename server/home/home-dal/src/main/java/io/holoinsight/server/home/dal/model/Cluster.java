/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: Cluster.java, v 0.1 2022年03月17日 5:41 下午 jinsong.yjs Exp $
 */
@Data

public class Cluster {
  @TableId(type = IdType.AUTO)
  public Long id;
  public String ip;

  public String hostname;

  public String role;

  @TableField(value = "`last_heartbeat_time`")
  public Long lastHeartBeatTime;

  public Date gmtModified;

  public Date gmtCreate;

  public Integer manualClose;
}
