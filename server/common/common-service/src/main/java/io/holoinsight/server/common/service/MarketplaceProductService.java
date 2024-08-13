/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.dao.entity.MarketplaceProduct;
import io.holoinsight.server.common.dao.entity.dto.MarketplaceProductDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplaceProductService.java, v 0.1 2022年10月13日 上午11:35 jinsong.yjs Exp $
 */
public interface MarketplaceProductService extends IService<MarketplaceProduct> {

  MarketplaceProductDTO findById(Long id);

  List<MarketplaceProductDTO> findByMap(Map<String, Object> columnMap);

  MarketplaceProductDTO create(MarketplaceProductDTO MarketplaceProductDTO);

  void deleteById(Long id);

  MarketplaceProductDTO updateByRequest(MarketplaceProductDTO MarketplaceProductDTO);

  MonitorPageResult<MarketplaceProductDTO> getListByPage(
      MonitorPageRequest<MarketplaceProductDTO> MarketplaceProductDTORequest);

  List<MarketplaceProductDTO> getListByKeyword(String keyword, String tenant);

  List<MarketplaceProductDTO> getListByNameLike(String name, String tenant);
}
