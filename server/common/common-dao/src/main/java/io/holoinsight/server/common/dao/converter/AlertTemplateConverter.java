/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.dto.NotificationTemplate;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import io.holoinsight.server.common.dao.entity.AlertTemplate;
import io.holoinsight.server.common.dao.entity.dto.AlertTemplateDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author masaimu
 * @version 2024-01-22 17:03:00
 */
@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface AlertTemplateConverter {

  AlertTemplateDTO doToDTO(AlertTemplate template);

  AlertTemplate dtoToDO(AlertTemplateDTO templateDTO);

  List<AlertTemplateDTO> dosToDTOs(Iterable<AlertTemplate> templates);

  default String map(NotificationTemplate value) {
    if (value == null) {
      return null;
    }
    return J.toJson(value);
  }

  default NotificationTemplate map(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return J.fromJson(value, NotificationTemplate.class);
  }
}
