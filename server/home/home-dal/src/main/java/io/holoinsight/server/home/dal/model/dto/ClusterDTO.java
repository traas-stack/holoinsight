/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterDTO.java, v 0.1 2022年03月17日 6:08 下午 jinsong.yjs Exp $
 */
@Data
public class ClusterDTO {
  public Long id;
  public String ip;
  public String hostname;
  public Long lastHeartBeatTime;
  public Date gmtModified;
  public String role;
  public Boolean manualClose;
}
