/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.dao.entity.AlertmanagerWebhook;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AlertmanagerWebhookService extends IService<AlertmanagerWebhook> {

  MonitorPageResult<AlertmanagerWebhook> getListByPage(
      MonitorPageRequest<AlertmanagerWebhook> request);

  AlertmanagerWebhook queryById(Long id, String tenant);

}
