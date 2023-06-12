/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.home.dal.model.AlarmMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginDTO.java, v 0.1 2022年03月14日 8:04 下午 jinsong.yjs Exp $
 */
@Data
public class CustomPluginDTO {
  public Long id;

  public String tenant;

  public String workspace;

  public Long parentFolderId;

  public String name;

  public String pluginType;

  public CustomPluginStatus status;

  public CustomPluginPeriodType periodType;

  public CustomPluginConf conf;

  public List<AlarmMetric> alarmMetrics;

  public String sampleLog;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
