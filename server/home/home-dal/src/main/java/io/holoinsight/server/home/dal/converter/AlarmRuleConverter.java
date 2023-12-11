/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.AlertRuleExtra;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:40 下午
 */
@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface AlarmRuleConverter {

  AlarmRuleDTO doToDTO(AlarmRule tenant);

  AlarmRule dtoToDO(AlarmRuleDTO tenantDTO);

  List<AlarmRuleDTO> dosToDTOs(Iterable<AlarmRule> tenants);

  default String map(AlertRuleExtra value) {
    if (value == null) {
      return null;
    }
    return J.toJson(value);
  }

  default AlertRuleExtra map(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return J.fromJson(value, AlertRuleExtra.class);
  }
}
