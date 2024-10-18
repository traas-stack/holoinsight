/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.AlarmHistory;
import io.holoinsight.server.common.dao.entity.dto.AlarmHistoryDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmHistoryService.java, v 0.1 2022年04月08日 2:51 下午 jinsong.yjs Exp $
 */
public interface AlarmHistoryService extends IService<AlarmHistory> {

  AlarmHistoryDTO queryById(Long id, String tenant, String workspace);

  MonitorPageResult<AlarmHistoryDTO> getListByPage(MonitorPageRequest<AlarmHistoryDTO> pageRequest,
      List<String> uniqueIds);

  Boolean deleteById(Long id);

  List<AlarmHistory> queryByTime(long from, long to);
}
