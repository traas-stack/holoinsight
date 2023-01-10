/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.transformer;

import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsStorage;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 *
 * @author xzchaoo
 * @version 1.0: TenantOpsStorageMapper.java, v 0.1 2022年06月21日 3:14 下午 jinsong.yjs Exp $
 */
@Component
public class TenantOpsStorageMapper {
  public static String asString(TenantOpsStorage storage) {
    return J.toJson(storage);
  }

  public static TenantOpsStorage asObj(String storage) {
    Type t = new TypeToken<TenantOpsStorage>() {}.getType();
    return J.fromJson(storage, t);
  }
}
