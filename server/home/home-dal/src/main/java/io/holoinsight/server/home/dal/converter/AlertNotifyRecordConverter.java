/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AlertNotifyRecord;
import io.holoinsight.server.home.dal.transformer.MapJsonMapper;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface AlertNotifyRecordConverter {
  AlertNotifyRecordDTO doToDTO(AlertNotifyRecord alertNotifyRecord);

  AlertNotifyRecord dtoToDO(AlertNotifyRecordDTO target);

  List<AlertNotifyRecordDTO> dosToDTOs(List<AlertNotifyRecord> records);
}
