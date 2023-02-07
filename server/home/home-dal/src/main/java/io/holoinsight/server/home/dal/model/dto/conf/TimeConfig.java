/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import io.holoinsight.server.registry.model.Elect;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zanghaibo
 * @time 2023-02-07 4:18 下午
 */
@Data
public class TimeConfig implements Serializable {

  private static final long serialVersionUID = 3326452060883733296L;
  /**
   * TimeZone取值类型: auto/processTime/elect
   */
  public String type = "elect";

  /**
   * 从日志里选出一个字段, 然后按format/layout/timezone对其进行解析
   */
  public Elect elect;

  /**
   * unix: unix秒时间戳 unixMilli: unix毫秒时间戳 golangLayout: golang风格的layout, 此时layout字段生效
   */
  public String format;

  /**
   * golang风格的layout, 请自行搜索相关文档
   */
  public String layout;

  /**
   * parse time 用的时区, 不填时使用Agent的本地时间
   */
  public String timezone;
}
