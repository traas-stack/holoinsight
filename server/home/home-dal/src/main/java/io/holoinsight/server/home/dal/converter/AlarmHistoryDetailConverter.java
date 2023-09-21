/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.transformer.ListJsonMapper;
import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.facade.trigger.AlertHistoryDetailExtra;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 21:40
 */
@Mapper(componentModel = "spring")
public interface AlarmHistoryDetailConverter {

  AlarmHistoryDetailDTO doToDTO(AlarmHistoryDetail alarmHistoryDetail);

  AlarmHistoryDetail dtoToDO(AlarmHistoryDetailDTO alarmHistoryDetailDTO);

  List<AlarmHistoryDetailDTO> dosToDTOs(Iterable<AlarmHistoryDetail> alarmHistoryDetails);

  default String map(AlertHistoryDetailExtra value) {
    if (value == null) {
      return null;
    }
    return J.toJson(value);
  }

  default AlertHistoryDetailExtra map(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return J.fromJson(value, AlertHistoryDetailExtra.class);
  }

  @Mapping(source = "app", target = "app")
  default String mapApp(List<String> app) {
    if (CollectionUtils.isEmpty(app)) {
      return null;
    }
    return "," + String.join(",", app) + ",";
  }

  @Mapping(source = "app", target = "app")
  default List<String> mapApp(String app) {
    if (StringUtils.isBlank(app)) {
      return Collections.emptyList();
    }
    return Arrays.asList(app.substring(1, app.length() - 1).split(","));
  }
}
