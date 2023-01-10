/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.ClusterTask;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterTaskService.java, v 0.1 2022年03月17日 7:34 下午 jinsong.yjs Exp $
 */
public interface ClusterTaskService extends IService<ClusterTask> {

  void batchInsert(List<ClusterTask> cts);

  void insert(ClusterTask ct);

  // 获取我的undone任务, 并标注为doing
  List<ClusterTask> getMyTask(long period);

  // 完成了我的任务，标注为done
  void doneTask(ClusterTask task, Boolean success, String res);

}
