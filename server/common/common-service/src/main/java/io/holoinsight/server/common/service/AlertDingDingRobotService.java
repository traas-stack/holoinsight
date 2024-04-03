/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.AlarmDingDingRobot;
import io.holoinsight.server.common.dao.entity.dto.AlarmDingDingRobotDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;


/**
 * @author wangsiyuan
 * @date 2022/4/7 2:03 下午
 */
public interface AlertDingDingRobotService extends IService<AlarmDingDingRobot> {

  Long save(AlarmDingDingRobotDTO alarmDingDingRobotDTO);

  Boolean updateById(AlarmDingDingRobotDTO alarmDingDingRobotDTO);

  AlarmDingDingRobotDTO queryById(Long id, String tenant);

  MonitorPageResult<AlarmDingDingRobotDTO> getListByPage(
      MonitorPageRequest<AlarmDingDingRobotDTO> pageRequest);

}
