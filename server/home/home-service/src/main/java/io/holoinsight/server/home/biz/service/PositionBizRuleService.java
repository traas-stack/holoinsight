/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.PositionBizRule;
import io.holoinsight.server.home.dal.model.dto.PositionBizRuleDTO;
import io.holoinsight.server.home.dal.model.dto.PositionBizRuleVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * bizops错误码业务规则相关接口
 */
public interface PositionBizRuleService extends IService<PositionBizRule> {
  /**
   * 添加错误码规则
   *
   * @param positionBizRuleDTO
   * @return
   */
  Long createRule(PositionBizRuleDTO positionBizRuleDTO);

  /**
   * 更新错误码规则
   *
   * @param positionBizRuleDTO
   * @return
   */
  Boolean updateRuleByAppAndInterfaceName(PositionBizRuleDTO positionBizRuleDTO);

  /**
   * 删除错误码规则
   *
   * @param positionBizRuleDTO
   * @return
   */
  Boolean deleteRuleByAppAndInterfaceName(PositionBizRuleDTO positionBizRuleDTO);

  /**
   * 查询错误码规则
   *
   * @param positionBizRuleDTO
   * @return
   */
  PositionBizRuleVO queryRuleByAppAndInterfaceName(PositionBizRuleDTO positionBizRuleDTO);

}
