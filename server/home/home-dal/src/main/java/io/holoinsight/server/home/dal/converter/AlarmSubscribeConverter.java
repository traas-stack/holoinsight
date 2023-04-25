/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;
import io.holoinsight.server.common.dao.transformer.ListJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:40 下午
 */
@Mapper(componentModel = "spring", uses = {ListJsonMapper.class})
public interface AlarmSubscribeConverter {

  AlarmSubscribeInfo doToDTO(AlarmSubscribe alarmSubscribe);

  AlarmSubscribe dtoToDO(AlarmSubscribeInfo alarmSubscribeInfo);

  List<AlarmSubscribeInfo> dosToDTOs(Iterable<AlarmSubscribe> alarmSubscribes);
}
