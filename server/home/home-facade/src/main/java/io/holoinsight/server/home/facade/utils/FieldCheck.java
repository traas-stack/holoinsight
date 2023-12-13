/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.utils;

import java.lang.reflect.Field;

/**
 * @author masaimu
 * @version 2023-12-06 17:41:00
 */
public interface FieldCheck {

  void customCheckUpdate(Field field, String tenant, String workspace);

  void customCheckCreate(Field field, String tenant, String workspace);

  void customCheckRead(Field field, String tenant, String workspace);
}
