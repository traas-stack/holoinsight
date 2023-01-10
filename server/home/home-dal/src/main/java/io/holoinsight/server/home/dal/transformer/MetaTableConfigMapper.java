/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import java.lang.reflect.Type;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.meta.MetaTableConfig;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableConfigMapper.java, v 0.1 2022年03月22日 3:32 下午 jinsong.yjs Exp $
 */
@Component
public class MetaTableConfigMapper {
  public static String asString(MetaTableConfig config) {
    return J.toJson(config);
  }

  public static MetaTableConfig asObj(String config) {
    Type t = new TypeToken<MetaTableConfig>() {}.getType();
    return J.fromJson(config, t);
  }
}
