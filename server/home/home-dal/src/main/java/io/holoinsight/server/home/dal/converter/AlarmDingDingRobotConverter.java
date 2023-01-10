/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AlarmDingDingRobot;
import io.holoinsight.server.home.dal.model.dto.AlarmDingDingRobotDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:40 下午
 */
@Mapper(componentModel = "spring")
public interface AlarmDingDingRobotConverter {

    AlarmDingDingRobotDTO doToDTO(AlarmDingDingRobot alarmDingDingRobot);

    AlarmDingDingRobot dtoToDO(AlarmDingDingRobotDTO alarmDingDingRobotDTO);

    List<AlarmDingDingRobotDTO> dosToDTOs(Iterable<AlarmDingDingRobot> alarmDingDingRobots);
}
