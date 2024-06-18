/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.dao.entity.IntegrationProduct;
import io.holoinsight.server.common.dao.entity.dto.IntegrationProductDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IntegrationProductService extends IService<IntegrationProduct> {

  IntegrationProductDTO findById(Long id);

  IntegrationProductDTO findByName(String name);

  List<IntegrationProductDTO> findByMap(Map<String, Object> columnMap);

  IntegrationProductDTO create(IntegrationProductDTO IntegrationProductDTO);

  void deleteById(Long id);

  IntegrationProductDTO updateByRequest(IntegrationProductDTO IntegrationProductDTO);

  MonitorPageResult<IntegrationProductDTO> getListByPage(
      MonitorPageRequest<IntegrationProductDTO> IntegrationProductDTORequest);

  List<IntegrationProductDTO> getListByKeyword(String keyword, String tenant);

  List<IntegrationProductDTO> getListByNameLike(String name, String tenant);

  List<IntegrationProductDTO> queryByRows();

  List<IntegrationProductDTO> queryNames();

}
