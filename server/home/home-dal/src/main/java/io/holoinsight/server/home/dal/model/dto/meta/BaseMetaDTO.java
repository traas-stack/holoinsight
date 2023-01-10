/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.meta;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: BaseMetaDTO.java, v 0.1 2022年03月16日 10:44 上午 jinsong.yjs Exp $
 */
@Data
public class BaseMetaDTO implements Serializable {
  private static final long serialVersionUID = 7465376185453994081L;

  /**
   * 主键ID，和default_pk 保持一致
   */
  public String id;

  /**
   * 租户ID, 避免字段冲突，使用 _
   */
  public String _tenant;

  /**
   * 来源
   */
  public String _source;

  /**
   * 状态
   */
  public String _status;
  /**
   * 创建时间
   */
  public Date _gmtCreate;
  /**
   * 修改时间
   */
  public Date _gmtModified;
  /**
   * 删除时间
   */
  public Date _gmtDeleted;
  /**
   * 是否删除
   */
  public Boolean _deleted;

  /**
   * 唯一键
   */
  public String _uk;

  /**
   * label 列表
   */
  public Map<String, Object> labelMap;

}
