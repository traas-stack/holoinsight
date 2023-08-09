/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DashboardService extends IService<Dashboard> {

  Dashboard queryById(Long id, String tenant, String workspace);

  Dashboard queryById(Long id, String tenant);

  List<Dashboard> findByIds(List<String> ids);

  List<Dashboard> getListByKeyword(String keyword, String tenant, String workspace);

  MonitorPageResult<Dashboard> getListByPage(MonitorPageRequest<Dashboard> request);

  Long create(Dashboard dashboard);

}
