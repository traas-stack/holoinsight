/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.transformer.ListJsonMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.AlertHistoryExtra;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:40 下午
 */
@Mapper(componentModel = "spring")
public interface AlarmHistoryConverter {
  @Mappings({@Mapping(source = "app", target = "app", qualifiedByName = "appMap2"),
      @Mapping(source = "triggerContent", target = "triggerContent",
          qualifiedByName = "triggerContent2"),
      @Mapping(source = "extra", target = "extra", qualifiedByName = "extra2"),})
  AlarmHistoryDTO doToDTO(AlarmHistory tenant);

  @Mappings({@Mapping(source = "app", target = "app", qualifiedByName = "appMap1"),
      @Mapping(source = "triggerContent", target = "triggerContent",
          qualifiedByName = "triggerContent1"),
      @Mapping(source = "extra", target = "extra", qualifiedByName = "extra1"),})
  AlarmHistory dtoToDO(AlarmHistoryDTO tenantDTO);

  List<AlarmHistoryDTO> dosToDTOs(Iterable<AlarmHistory> tenants);

  @Named("appMap1")
  default String map(List<String> app) {
    if (CollectionUtils.isEmpty(app)) {
      return null;
    }
    return "," + String.join(",", app) + ",";
  }

  @Named("appMap2")
  default List<String> map(String app) {
    if (StringUtils.isBlank(app)) {
      return Collections.emptyList();
    }
    return Arrays.asList(app.substring(1, app.length() - 1).split(","));
  }

  @Named("triggerContent1")
  default String triggerContent(List<String> triggerContent) {
    return ListJsonMapper.asString(triggerContent);
  }

  @Named("triggerContent2")
  default List<String> triggerContent(String triggerContent) {
    return ListJsonMapper.asObj(triggerContent);
  }

  @Named("extra1")
  default String extra(AlertHistoryExtra value) {
    if (value == null) {
      return null;
    }
    return J.toJson(value);
  }

  @Named("extra2")
  default AlertHistoryExtra extra(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return J.fromJson(value, AlertHistoryExtra.class);
  }
}
