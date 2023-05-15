/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jinsong.yjs
 * @version 1.0: MetricCrawler.java, v 0.1 2023年05月10日 16:36 jinsong.yjs Exp $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetricCrawler {
  String code() default StringUtils.EMPTY;

}
