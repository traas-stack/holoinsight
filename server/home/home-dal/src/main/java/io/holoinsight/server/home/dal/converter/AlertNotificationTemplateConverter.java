/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import io.holoinsight.server.home.dal.model.AlertNotificationTemplate;
import io.holoinsight.server.home.facade.AlertNotificationTemplateDTO;
import io.holoinsight.server.home.facade.NotificationTemplate;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author masaimu
 * @version 2024-01-22 17:03:00
 */
@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface AlertNotificationTemplateConverter {

  AlertNotificationTemplateDTO doToDTO(AlertNotificationTemplate template);

  AlertNotificationTemplate dtoToDO(AlertNotificationTemplateDTO templateDTO);

  List<AlertNotificationTemplateDTO> dosToDTOs(Iterable<AlertNotificationTemplate> templates);

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
