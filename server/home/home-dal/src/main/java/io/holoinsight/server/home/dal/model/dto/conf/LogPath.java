/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: Log.java, v 0.1 2022年03月14日 8:20 下午 jinsong.yjs Exp $
 */
@Data
public class LogPath implements Serializable {
  private static final long serialVersionUID = 5655374551833224541L;

  /**
   * path/glob/regexp/format
   */
  public String type;

  /**
   * type == path; 绝对路径 type == glob; 表达式 /common-error*.log type == format;
   * /{time:yyyy}/{time:MM}/{time:dd}/common-error.log
   */
  public String path;

  /**
   * type = regexp, 填写父目录，/home/admin/logs
   */
  public String dir;

  /// **
  // * type == regexp,， 填写正则表达式，匹配全路径
  // */
  // public String regexp;

}
