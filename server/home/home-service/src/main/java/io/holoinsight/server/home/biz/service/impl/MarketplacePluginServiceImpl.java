/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.MarketplacePluginService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.MarketplacePluginConverter;
import io.holoinsight.server.home.dal.mapper.MarketplacePluginMapper;
import io.holoinsight.server.home.dal.model.MarketplacePlugin;
import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplacePluginServiceImpl.java, v 0.1 2022年10月13日 上午11:39 jinsong.yjs Exp $
 */
@Service
public class MarketplacePluginServiceImpl extends
    ServiceImpl<MarketplacePluginMapper, MarketplacePlugin> implements MarketplacePluginService {

  @Autowired
  private MarketplacePluginConverter marketplacePluginConverter;

  @Override
  public MarketplacePluginDTO queryById(Long id, String tenant, String workspace) {
    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    MarketplacePlugin model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }

    return marketplacePluginConverter.doToDTO(model);
  }

  @Override
  public List<MarketplacePluginDTO> queryByTenant(String tenant, String workspace) {
    QueryWrapper<MarketplacePlugin> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("name", "product", "json").eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      queryWrapper.eq("workspace", workspace);
    }
    List<MarketplacePlugin> models = baseMapper.selectList(queryWrapper);
    return marketplacePluginConverter.dosToDTOs(models);
  }

  @Override
  public List<MarketplacePluginDTO> findByMap(Map<String, Object> columnMap) {
    List<MarketplacePlugin> models = listByMap(columnMap);

    return marketplacePluginConverter.dosToDTOs(models);
  }

  @Override
  public MarketplacePluginDTO create(MarketplacePluginDTO MarketplacePluginDTO) {
    MarketplacePluginDTO.setGmtCreate(new Date());
    MarketplacePluginDTO.setGmtModified(new Date());
    MarketplacePlugin model = marketplacePluginConverter.dtoToDO(MarketplacePluginDTO);
    save(model);
    return marketplacePluginConverter.doToDTO(model);
  }

  @Override
  public void deleteById(Long id) {
    MarketplacePlugin MarketplacePlugin = getById(id);
    if (null == MarketplacePlugin) {
      return;
    }
    removeById(id);
  }

  @Override
  public MarketplacePluginDTO updateByRequest(MarketplacePluginDTO MarketplacePluginDTO) {
    MarketplacePluginDTO.setGmtModified(new Date());
    MarketplacePlugin model = marketplacePluginConverter.dtoToDO(MarketplacePluginDTO);
    saveOrUpdate(model);
    return marketplacePluginConverter.doToDTO(model);
  }

  @Override
  public MonitorPageResult<MarketplacePluginDTO> getListByPage(
      MonitorPageRequest<MarketplacePluginDTO> marketplacePluginDTORequest) {
    if (marketplacePluginDTORequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();

    MarketplacePluginDTO marketplacePluginDTO = marketplacePluginDTORequest.getTarget();

    if (null != marketplacePluginDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", marketplacePluginDTO.getGmtCreate());
    }
    if (null != marketplacePluginDTO.getGmtModified()) {
      wrapper.le("gmt_modified", marketplacePluginDTO.getGmtCreate());
    }

    if (StringUtil.isNotBlank(marketplacePluginDTO.getCreator())) {
      wrapper.eq("creator", marketplacePluginDTO.getCreator().trim());
    }

    if (StringUtil.isNotBlank(marketplacePluginDTO.getModifier())) {
      wrapper.eq("modifier", marketplacePluginDTO.getModifier().trim());
    }

    if (null != marketplacePluginDTO.getId()) {
      wrapper.eq("id", marketplacePluginDTO.getId());
    }

    if (StringUtil.isNotBlank(marketplacePluginDTO.getName())) {
      wrapper.like("name", marketplacePluginDTO.getName().trim());
    }


    if (null != marketplacePluginDTO.getStatus()) {
      wrapper.eq("status", marketplacePluginDTO.getStatus());
    }

    if (null != marketplacePluginDTO.getTenant()) {
      wrapper.eq("tenant", marketplacePluginDTO.getTenant());
    }

    if (StringUtil.isNotBlank(marketplacePluginDTORequest.getSortBy())
        && StringUtil.isNotBlank(marketplacePluginDTORequest.getSortRule())) {
      if (marketplacePluginDTORequest.getSortRule().toLowerCase(Locale.ROOT).equals("desc")) {
        wrapper.orderByDesc(marketplacePluginDTORequest.getSortBy());
      } else {
        wrapper.orderByAsc(marketplacePluginDTORequest.getSortBy());
      }
    } else {
      wrapper.orderByDesc("gmt_modified");
    }

    if (StringUtils.isNotBlank(marketplacePluginDTO.getWorkspace())) {
      wrapper.eq("workspace", marketplacePluginDTO.getWorkspace());
    }
    wrapper.select(MarketplacePlugin.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<MarketplacePlugin> page = new Page<>(marketplacePluginDTORequest.getPageNum(),
        marketplacePluginDTORequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<MarketplacePluginDTO> customPluginDTOs = new MonitorPageResult<>();

    customPluginDTOs.setItems(marketplacePluginConverter.dosToDTOs(page.getRecords()));
    customPluginDTOs.setPageNum(marketplacePluginDTORequest.getPageNum());
    customPluginDTOs.setPageSize(marketplacePluginDTORequest.getPageSize());
    customPluginDTOs.setTotalCount(page.getTotal());
    customPluginDTOs.setTotalPage(page.getPages());

    return customPluginDTOs;
  }

  @Override
  public List<MarketplacePluginDTO> getListByKeyword(String keyword, String tenant,
      String workspace) {
    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.like("id", keyword).or().like("name", keyword);
    Page<MarketplacePlugin> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return marketplacePluginConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<MarketplacePluginDTO> getListByNameLike(String name, String tenant,
      String workspace) {
    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();
    wrapper.select().like("name", name);
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    List<MarketplacePlugin> customPlugins = baseMapper.selectList(wrapper);
    return marketplacePluginConverter.dosToDTOs(customPlugins);
  }
}
