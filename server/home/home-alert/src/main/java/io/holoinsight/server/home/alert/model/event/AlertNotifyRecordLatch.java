/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author limengyang
 * @date 2023/7/27 15:28
 * @DESCRIPTION
 */
@Data
public class AlertNotifyRecordLatch {

  private List<AlertNotifyRecordDTO> alertNotifyRecordDTOList = new ArrayList<>();

  public void add(AlertNotifyRecordDTO alertNotifyRecord) {
    if (alertNotifyRecord == null) {
      return;
    }
    if (this.alertNotifyRecordDTOList == null) {
      this.alertNotifyRecordDTOList = new ArrayList<>();
    }
    this.alertNotifyRecordDTOList.add(alertNotifyRecord);
  }

  public int size() {
    if (!CollectionUtils.isEmpty(alertNotifyRecordDTOList)) {
      return alertNotifyRecordDTOList.size();
    }
    return 0;
  }
}
