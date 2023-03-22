/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface CustomPluginService extends IService<CustomPlugin> {

  CustomPluginDTO queryById(Long id, String tenant, String workspace);

  List<CustomPluginDTO> findByMap(Map<String, Object> columnMap);

  List<CustomPluginDTO> findByIds(List<String> ids);

  CustomPluginDTO create(CustomPluginDTO customPluginDTO);

  void deleteById(Long id);

  CustomPluginDTO updateByRequest(CustomPluginDTO customPluginDTO);

  MonitorPageResult<CustomPluginDTO> getListByPage(
      MonitorPageRequest<CustomPluginDTO> customPluginDTORequest);

  List<CustomPluginDTO> getListByKeyword(String keyword, String tenant, String workspace);

  List<CustomPluginDTO> getListByNameLike(String name, String tenant, String workspace);

  Boolean updateById(CustomPluginDTO customPluginDTO);

}
