/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.AlertNotifyRecord;
import io.holoinsight.server.common.dao.entity.dto.AlertNotifyRecordDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;

public interface AlertNotifyRecordService extends IService<AlertNotifyRecord> {
  AlertNotifyRecordDTO queryByHistoryDetailId(Long historyDetailId, String tenant,
      String workspace);

  MonitorPageResult<AlertNotifyRecordDTO> getListByPage(
      MonitorPageRequest<AlertNotifyRecordDTO> pageRequest);

  void insert(AlertNotifyRecordDTO alertNotifyRecordDTO);
}
