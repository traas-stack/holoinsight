/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.MarketplaceProductService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.MarketplaceProductConverter;
import io.holoinsight.server.home.dal.mapper.MarketplaceProductMapper;
import io.holoinsight.server.home.dal.model.MarketplaceProduct;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplaceProductServiceImpl.java, v 0.1 2022年10月13日 上午11:36 jinsong.yjs Exp $
 */
@Service
public class MarketplaceProductServiceImpl extends
    ServiceImpl<MarketplaceProductMapper, MarketplaceProduct> implements MarketplaceProductService {

  @Autowired
  private MarketplaceProductConverter marketplaceProductConverter;


  @Override
  public MarketplaceProductDTO findById(Long id) {
    MarketplaceProduct model = getById(id);
    if (model == null) {
      return null;
    }
    return marketplaceProductConverter.doToDTO(model);
  }

  @Override
  public List<MarketplaceProductDTO> findByMap(Map<String, Object> columnMap) {
    List<MarketplaceProduct> models = listByMap(columnMap);

    return marketplaceProductConverter.dosToDTOs(models);
  }

  @Override
  public MarketplaceProductDTO create(MarketplaceProductDTO MarketplaceProductDTO) {
    MarketplaceProductDTO.setGmtCreate(new Date());
    MarketplaceProductDTO.setGmtModified(new Date());
    MarketplaceProduct model = marketplaceProductConverter.dtoToDO(MarketplaceProductDTO);
    save(model);
    return marketplaceProductConverter.doToDTO(model);
  }

  @Override
  public void deleteById(Long id) {
    MarketplaceProduct MarketplaceProduct = getById(id);
    if (null == MarketplaceProduct) {
      return;
    }
    removeById(id);
  }

  @Override
  public MarketplaceProductDTO updateByRequest(MarketplaceProductDTO MarketplaceProductDTO) {
    MarketplaceProductDTO.setGmtModified(new Date());
    MarketplaceProduct model = marketplaceProductConverter.dtoToDO(MarketplaceProductDTO);
    saveOrUpdate(model);
    return marketplaceProductConverter.doToDTO(model);
  }

  @Override
  public MonitorPageResult<MarketplaceProductDTO> getListByPage(
      MonitorPageRequest<MarketplaceProductDTO> MarketplaceProductDTORequest) {
    if (MarketplaceProductDTORequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<MarketplaceProduct> wrapper = new QueryWrapper<>();

    MarketplaceProductDTO MarketplaceProductDTO = MarketplaceProductDTORequest.getTarget();

    if (null != MarketplaceProductDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", MarketplaceProductDTO.getGmtCreate());
    }
    if (null != MarketplaceProductDTO.getGmtModified()) {
      wrapper.le("gmt_modified", MarketplaceProductDTO.getGmtCreate());
    }

    if (StringUtil.isNotBlank(MarketplaceProductDTO.getCreator())) {
      wrapper.eq("creator", MarketplaceProductDTO.getCreator().trim());
    }

    if (StringUtil.isNotBlank(MarketplaceProductDTO.getModifier())) {
      wrapper.eq("modifier", MarketplaceProductDTO.getModifier().trim());
    }

    if (null != MarketplaceProductDTO.getId()) {
      wrapper.eq("id", MarketplaceProductDTO.getId());
    }

    if (StringUtil.isNotBlank(MarketplaceProductDTO.getName())) {
      wrapper.like("name", MarketplaceProductDTO.getName().trim());
    }


    if (null != MarketplaceProductDTO.getStatus()) {
      wrapper.eq("status", MarketplaceProductDTO.getStatus());
    }
    wrapper.select(MarketplaceProduct.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<MarketplaceProduct> page = new Page<>(MarketplaceProductDTORequest.getPageNum(),
        MarketplaceProductDTORequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<MarketplaceProductDTO> dtos = new MonitorPageResult<>();

    dtos.setItems(marketplaceProductConverter.dosToDTOs(page.getRecords()));
    dtos.setPageNum(MarketplaceProductDTORequest.getPageNum());
    dtos.setPageSize(MarketplaceProductDTORequest.getPageSize());
    dtos.setTotalCount(page.getTotal());
    dtos.setTotalPage(page.getPages());

    return dtos;
  }

  @Override
  public List<MarketplaceProductDTO> getListByKeyword(String keyword, String tenant) {
    QueryWrapper<MarketplaceProduct> wrapper = new QueryWrapper<>();
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    wrapper.like("id", keyword).or().like("name", keyword);
    Page<MarketplaceProduct> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return marketplaceProductConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<MarketplaceProductDTO> getListByNameLike(String name, String tenant) {
    QueryWrapper<MarketplaceProduct> wrapper = new QueryWrapper<>();
    wrapper.select().like("name", name);
    List<MarketplaceProduct> customPlugins = baseMapper.selectList(wrapper);
    return marketplaceProductConverter.dosToDTOs(customPlugins);
  }
}
