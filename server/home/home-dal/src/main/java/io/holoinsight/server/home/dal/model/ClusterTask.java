/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterTask.java, v 0.1 2022年03月17日 7:30 下午 jinsong.yjs Exp $
 */
@Data
public class ClusterTask {

  @TableId(type = IdType.AUTO)
  public Long id;
  public Long period;

  public String taskId;

  public String clusterIp;

  public String status;

  public String context;

  public String result;

  public Date gmtCreate;

  public Date gmtModified;
}
