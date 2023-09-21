/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.home.biz.service.ClusterTaskService;
import io.holoinsight.server.home.dal.mapper.ClusterTaskMapper;
import io.holoinsight.server.home.dal.model.ClusterTask;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterTaskServiceImpl.java, v 0.1 2022年10月19日 下午7:17 jinsong.yjs Exp $
 */
@Service
public class ClusterTaskServiceImpl extends ServiceImpl<ClusterTaskMapper, ClusterTask>
    implements ClusterTaskService {

  public static final String TASK_UNDONE = "undone";
  public static final String TASK_DOING = "doing";
  public static final String TASK_DONE = "done";
  public static final String TASK_ERROR = "error";

  // 批量生成任务
  public void batchInsert(List<ClusterTask> cts) {
    for (ClusterTask ct : cts) {
      ct.setGmtCreate(new Date());
      ct.setGmtModified(new Date());
      save(ct);
    }
  }

  public void insert(ClusterTask ct) {
    ct.setGmtCreate(new Date());
    ct.setGmtModified(new Date());
    save(ct);
  }

  // 获取我的undone任务, 并标注为doing
  public List<ClusterTask> getMyTask(long period) {
    long scheduleTime = period - (5L * PeriodType.SECOND.intervalMillis());
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("status", TASK_UNDONE);
    columnMap.put("cluster_ip", AddressUtil.getLocalHostIPV4());
    columnMap.put("period", scheduleTime);

    List<ClusterTask> res = listByMap(columnMap);
    for (ClusterTask ct : res) {
      ct.setStatus(TASK_DOING);
      ct.setGmtModified(new Date());
      updateById(ct);
    }
    return res;
  }

  // 完成了我的任务，标注为done
  public void doneTask(ClusterTask task, Boolean success, String res) {
    task.setGmtModified(new Date());
    if (success) {
      task.setStatus(TASK_DONE);
    } else {
      task.setStatus(TASK_ERROR);
      task.setResult(res);
    }
    updateById(task);
  }
}
