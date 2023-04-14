/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.home.common.model.TaskEnum;
import io.holoinsight.server.home.common.model.TaskEnum.TaskType;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jsy1001de
 * @version : TaskHandler.java, v 0.1 2022-03-17 20:01 jinsong.yjs Exp $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskHandler {
  TaskEnum value() default TaskEnum.UNKNOWN_TASK;

  String code() default StringUtils.EMPTY;

  TaskType type() default TaskType.TASK;
}
