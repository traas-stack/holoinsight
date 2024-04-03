/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.AlertmanagerWebhook;
import io.holoinsight.server.common.dao.entity.dto.AlertmanagerWebhookDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlertmanagerWebhookConverter {

  AlertmanagerWebhookDTO doToDTO(AlertmanagerWebhook model);

  AlertmanagerWebhook dtoToDO(AlertmanagerWebhookDTO dto);

  List<AlertmanagerWebhookDTO> dosToDTOs(Iterable<AlertmanagerWebhook> models);

}
