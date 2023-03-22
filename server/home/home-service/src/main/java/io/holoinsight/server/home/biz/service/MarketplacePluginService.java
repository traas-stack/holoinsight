/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.MarketplacePlugin;
import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplacePluginService.java, v 0.1 2022年10月13日 上午11:38 jinsong.yjs Exp $
 */
public interface MarketplacePluginService extends IService<MarketplacePlugin> {

  MarketplacePluginDTO queryById(Long id, String tenant, String workspace);

  List<MarketplacePluginDTO> queryByTenant(String tenant, String workspace);

  List<MarketplacePluginDTO> findByMap(Map<String, Object> columnMap);

  MarketplacePluginDTO create(MarketplacePluginDTO integrationPluginDTO);

  void deleteById(Long id);

  MarketplacePluginDTO updateByRequest(MarketplacePluginDTO integrationPluginDTO);

  MonitorPageResult<MarketplacePluginDTO> getListByPage(
      MonitorPageRequest<MarketplacePluginDTO> integrationPluginDTORequest);

  List<MarketplacePluginDTO> getListByKeyword(String keyword, String tenant, String workspace);

  List<MarketplacePluginDTO> getListByNameLike(String name, String tenant, String workspace);

}
