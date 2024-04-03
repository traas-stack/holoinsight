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
 * @version 1.0: UserFavorite.java, v 0.1 2022年03月21日 2:30 下午 jinsong.yjs Exp $
 */
@Data
@TableName("user_favorite")
public class UserFavorite {

  @TableId(type = IdType.AUTO)
  public Long id;

  public String userLoginName;

  public String relateId;

  public String type;

  public String name;

  public String url;

  public String tenant;

  public String workspace;

  public Date gmtCreate;

  public Date gmtModified;
}
