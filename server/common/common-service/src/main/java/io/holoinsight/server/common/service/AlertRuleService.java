/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.dao.entity.AlarmRule;
import io.holoinsight.server.common.dao.entity.dto.AlarmRuleDTO;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/7 2:03 下午
 */
public interface AlertRuleService extends IService<AlarmRule> {

  Long save(AlarmRuleDTO alarmRuleDTO);

  Boolean updateById(AlarmRuleDTO alarmRuleDTO);

  Boolean deleteById(Long id);

  Long save(AlarmRuleDTO alarmRuleDTO, boolean busPost);

  Boolean updateById(AlarmRuleDTO alarmRuleDTO, boolean busPost);

  Boolean deleteById(Long id, boolean busPost);

  AlarmRuleDTO queryById(Long id, String tenant, String workspace);

  List<AlarmRuleDTO> queryBySourceType(String sourceType, String tenant, String workspace);

  List<AlarmRuleDTO> queryBySourceId(Long sourceId);

  MonitorPageResult<AlarmRuleDTO> getListByPage(MonitorPageRequest<AlarmRuleDTO> pageRequest);

  List<AlarmRuleDTO> getListByKeyword(String keyword, String tenant, String workspace);
}
