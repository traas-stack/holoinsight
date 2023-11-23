/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
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
}
