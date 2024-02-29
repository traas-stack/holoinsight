/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/7 2:03 下午
 */
public interface AlertRuleService extends IService<AlarmRule> {

  Long save(AlarmRuleDTO alarmRuleDTO);

  Boolean updateById(AlarmRuleDTO alarmRuleDTO);

  Boolean deleteById(Long id);

  AlarmRuleDTO queryById(Long id, String tenant, String workspace);

  List<AlarmRuleDTO> queryBySourceType(String sourceType, String tenant, String workspace);

  List<AlarmRuleDTO> queryBySourceId(Long sourceId);

  MonitorPageResult<AlarmRuleDTO> getListByPage(MonitorPageRequest<AlarmRuleDTO> pageRequest);

  List<AlarmRuleDTO> getListByKeyword(String keyword, String tenant, String workspace);
}
