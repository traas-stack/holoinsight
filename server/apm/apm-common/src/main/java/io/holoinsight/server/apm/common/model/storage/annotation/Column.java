/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.storage.annotation;

import java.lang.annotation.*;

/**
 * @author jiwliu
 * @version : Column.java, v 0.1 2022年10月11日 19:18 xiangwanpeng Exp $
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
  String name();

}
