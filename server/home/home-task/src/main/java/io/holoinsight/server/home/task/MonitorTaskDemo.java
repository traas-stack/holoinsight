/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.home.common.model.TaskEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorTaskDemo.java, v 0.1 2022年03月17日 8:02 下午 jinsong.yjs Exp $
 */
@Service
@TaskHandler(TaskEnum.TASK_DEMO)
public class MonitorTaskDemo extends AbstractMonitorTask {

  public MonitorTaskDemo() {
    super(1, 2, TaskEnum.TASK_DEMO);
  }

  @Override
  public List<MonitorTaskJob> buildJobs(long period) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean needRun() {
    return true;
  }

}
