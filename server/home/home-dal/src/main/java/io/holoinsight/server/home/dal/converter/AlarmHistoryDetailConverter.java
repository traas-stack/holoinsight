/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.dal.transformer.ListJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:40 下午
 */
@Mapper(componentModel = "spring", uses = {ListJsonMapper.class})
public interface AlarmHistoryDetailConverter {

  AlarmHistoryDetailDTO doToDTO(AlarmHistoryDetail alarmHistoryDetail);

  AlarmHistoryDetail dtoToDO(AlarmHistoryDetailDTO alarmHistoryDetailDTO);

  List<AlarmHistoryDetailDTO> dosToDTOs(Iterable<AlarmHistoryDetail> alarmHistoryDetails);
}
