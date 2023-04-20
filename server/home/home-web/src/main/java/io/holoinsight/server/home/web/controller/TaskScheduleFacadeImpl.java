/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.dal.mapper.ClusterTaskMapper;
import io.holoinsight.server.home.dal.model.ClusterTask;
import io.holoinsight.server.home.web.common.ManageCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-04-14 15:33:00
 */
@RestController
@RequestMapping("/webapi/task")
@Slf4j
public class TaskScheduleFacadeImpl extends BaseFacade {

  @Resource
  private ClusterTaskMapper clusterTaskMapper;

  @GetMapping("/cluster/{taskId}")
  @ResponseBody
  public JsonResult<List<ClusterTask>> getClusterTasks(@PathVariable("taskId") String taskId) {
    final JsonResult<List<ClusterTask>> result = new JsonResult<>();
    result.setSuccess(true);
    result.setData(new ArrayList<>());
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        Page<ClusterTask> page = new Page<>(1, 10);
        QueryWrapper<ClusterTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        Page<ClusterTask> pageResult = clusterTaskMapper.selectPage(page, queryWrapper);
        if (!CollectionUtils.isEmpty(pageResult.getRecords())) {
          result.setData(pageResult.getRecords());
        }
      }
    });

    return result;
  }

}
