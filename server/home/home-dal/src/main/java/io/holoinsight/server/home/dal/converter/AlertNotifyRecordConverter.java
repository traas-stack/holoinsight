/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AlertNotifyRecord;
import io.holoinsight.server.home.dal.transformer.AlertNotifyErrorCodeMapper;
import io.holoinsight.server.home.dal.transformer.AlertNotifyStageMapper;
import io.holoinsight.server.home.dal.transformer.AlertNotifyUserMapper;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AlertNotifyUserMapper.class,
    AlertNotifyErrorCodeMapper.class, AlertNotifyStageMapper.class})
public interface AlertNotifyRecordConverter {
  @Mapping(target = "notifyStage", source = "extra")
  AlertNotifyRecordDTO doToDTO(AlertNotifyRecord alertNotifyRecord);

  @Mapping(target = "extra", source = "notifyStage")
  AlertNotifyRecord dtoToDO(AlertNotifyRecordDTO target);

  @Mapping(target = "notifyStage", source = "extra")
  List<AlertNotifyRecordDTO> dosToDTOs(List<AlertNotifyRecord> records);
}
