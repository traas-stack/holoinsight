/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.AlarmGroup;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmGroupService.java, v 0.1 2022年04月08日 2:51 下午 jinsong.yjs Exp $
 */
public interface AlertGroupService extends IService<AlarmGroup> {

  Long save(AlarmGroupDTO alarmGroupDTO);

  Boolean updateById(AlarmGroupDTO alarmGroupDTO);

  AlarmGroupDTO queryById(Long id, String tenant);

  AlarmGroupDTO queryById(Long id, String tenant, String workspace);

  List<AlarmGroupDTO> getListByUserLike(String userId, String tenant);

  MonitorPageResult<AlarmGroupDTO> getListByPage(MonitorPageRequest<AlarmGroupDTO> pageRequest);

}
