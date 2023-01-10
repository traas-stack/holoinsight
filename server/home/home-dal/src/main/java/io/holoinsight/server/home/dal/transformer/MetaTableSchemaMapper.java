/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.meta.MetaTableCol;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableSchemaMapper.java, v 0.1 2022年03月22日 3:32 下午 jinsong.yjs Exp $
 */
@Component
public class MetaTableSchemaMapper {
  public static String asString(List<MetaTableCol> schema) {
    return J.toJson(schema);
  }

  public static List<MetaTableCol> asObj(String schema) {
    Type t = new TypeToken<List<MetaTableCol>>() {}.getType();
    return J.fromJson(schema, t);
  }
}
