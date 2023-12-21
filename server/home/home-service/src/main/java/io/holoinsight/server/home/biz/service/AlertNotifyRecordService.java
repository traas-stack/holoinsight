/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.AlertNotifyRecord;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;

public interface AlertNotifyRecordService extends IService<AlertNotifyRecord> {
  AlertNotifyRecordDTO queryByHistoryDetailId(Long historyDetailId, String tenant,
      String workspace);

  MonitorPageResult<AlertNotifyRecordDTO> getListByPage(
      MonitorPageRequest<AlertNotifyRecordDTO> pageRequest);

  void insert(AlertNotifyRecordDTO alertNotifyRecordDTO);
}
