/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.PositionBizRule;
import io.holoinsight.server.home.dal.model.dto.PositionBizRuleDTO;
import org.mapstruct.Mapper;

/**
 * 错误码定位业务规则转换接口
 */
@Mapper(componentModel = "spring")
public interface PositionBizRuleConverter {
  /**
   * do转dto
   *
   * @param positionBizRule
   * @return
   */
  PositionBizRuleDTO convertToDTO(PositionBizRule positionBizRule);

  /**
   * dto转do
   *
   * @param positionBizRuleDTO
   * @return
   */
  PositionBizRule convertToDO(PositionBizRuleDTO positionBizRuleDTO);
}
