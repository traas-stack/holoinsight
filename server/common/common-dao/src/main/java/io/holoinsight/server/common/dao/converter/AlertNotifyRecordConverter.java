/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.AlertNotifyRecord;
import io.holoinsight.server.common.dao.transformer.AlertNotifyErrorCodeMapper;
import io.holoinsight.server.common.dao.transformer.AlertNotifyStageMapper;
import io.holoinsight.server.common.dao.transformer.AlertNotifyUserMapper;
import io.holoinsight.server.common.dao.entity.dto.AlertNotifyRecordDTO;
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
