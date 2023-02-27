/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaDataDictValue.java, v 0.1 2022年03月21日 8:17 下午 jinsong.yjs Exp $
 */
@Data
@TableName("metadata_dictvalue")
public class MetaDataDictValue {

  @TableId(type = IdType.AUTO)
  public Long id;

  public String type;

  public String dictKey;

  public String dictValue;

  public String dictValueType;

  public String dictDesc;

  public Integer version;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
