/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.IntegrationPlugin;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;

import java.util.List;
import java.util.Map;

public interface IntegrationPluginService extends IService<IntegrationPlugin> {

  IntegrationPluginDTO queryById(Long id, String tenant, String workspace);

  IntegrationPluginDTO queryByName(String name, String tenant, String workspace);

  List<IntegrationPluginDTO> queryByTenant(String tenant);

  List<IntegrationPluginDTO> queryByTenant(String tenant, String workspace);

  List<IntegrationPluginDTO> findByMap(Map<String, Object> columnMap);

  IntegrationPluginDTO create(IntegrationPluginDTO integrationPluginDTO);

  void deleteById(Long id);

  IntegrationPluginDTO updateByRequest(IntegrationPluginDTO integrationPluginDTO);

  MonitorPageResult<IntegrationPluginDTO> getListByPage(
      MonitorPageRequest<IntegrationPluginDTO> integrationPluginDTORequest);

  List<IntegrationPluginDTO> getListByKeyword(String keyword, String tenant, String workspace);

  List<IntegrationPluginDTO> getListByNameLike(String name, String tenant, String workspace);

}
