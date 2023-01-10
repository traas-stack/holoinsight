/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task;

import io.holoinsight.server.home.task.TaskEnum.TaskType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jsy1001de
 * @version : TaskHandler.java, v 0.1 2022年03月17日 8:01 下午 jinsong.yjs Exp $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskHandler {
  TaskEnum value();

  TaskType type() default TaskType.TASK;
}
