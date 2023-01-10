/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.AlertmanagerWebhook;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AlertmanagerWebhookService extends IService<AlertmanagerWebhook> {

  MonitorPageResult<AlertmanagerWebhook> getListByPage(
      MonitorPageRequest<AlertmanagerWebhook> request);

  AlertmanagerWebhook queryById(Long id, String tenant);

}
