/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.MarketplacePluginService;
import io.holoinsight.server.home.common.util.EventBusHolder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
  public MarketplacePluginDTO queryById(Long id, String tenant) {
    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    MarketplacePlugin model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }

    return marketplacePluginConverter.doToDTO(model);
  }

  @Override
  public List<MarketplacePluginDTO> queryByTenant(String tenant) {
    QueryWrapper<MarketplacePlugin> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("name", "product", "json").eq("tenant", tenant);
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
    MarketplacePluginDTO customPluginDTOId = marketplacePluginConverter.doToDTO(model);
    EventBusHolder.post(customPluginDTOId);
    return customPluginDTOId;
  }

  @Override
  public void deleteById(Long id) {
    MarketplacePlugin MarketplacePlugin = getById(id);
    if (null == MarketplacePlugin) {
      return;
    }
    removeById(id);
    EventBusHolder.post(MarketplacePlugin);
  }

  @Override
  public MarketplacePluginDTO updateByRequest(MarketplacePluginDTO MarketplacePluginDTO) {
    MarketplacePluginDTO.setGmtModified(new Date());
    MarketplacePlugin model = marketplacePluginConverter.dtoToDO(MarketplacePluginDTO);
    saveOrUpdate(model);
    MarketplacePluginDTO save = marketplacePluginConverter.doToDTO(model);
    EventBusHolder.post(save);
    return save;
  }

  @Override
  public MonitorPageResult<MarketplacePluginDTO> getListByPage(
      MonitorPageRequest<MarketplacePluginDTO> MarketplacePluginDTORequest) {
    if (MarketplacePluginDTORequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();

    MarketplacePluginDTO MarketplacePluginDTO = MarketplacePluginDTORequest.getTarget();

    if (null != MarketplacePluginDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", MarketplacePluginDTO.getGmtCreate());
    }
    if (null != MarketplacePluginDTO.getGmtModified()) {
      wrapper.le("gmt_modified", MarketplacePluginDTO.getGmtCreate());
    }

    if (StringUtil.isNotBlank(MarketplacePluginDTO.getCreator())) {
      wrapper.eq("creator", MarketplacePluginDTO.getCreator().trim());
    }

    if (StringUtil.isNotBlank(MarketplacePluginDTO.getModifier())) {
      wrapper.eq("modifier", MarketplacePluginDTO.getModifier().trim());
    }

    if (null != MarketplacePluginDTO.getId()) {
      wrapper.eq("id", MarketplacePluginDTO.getId());
    }

    if (StringUtil.isNotBlank(MarketplacePluginDTO.getName())) {
      wrapper.like("name", MarketplacePluginDTO.getName().trim());
    }


    if (null != MarketplacePluginDTO.getStatus()) {
      wrapper.eq("status", MarketplacePluginDTO.getStatus());
    }

    if (null != MarketplacePluginDTO.getTenant()) {
      wrapper.eq("tenant", MarketplacePluginDTO.getTenant());
    }
    wrapper.select(MarketplacePlugin.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<MarketplacePlugin> page = new Page<>(MarketplacePluginDTORequest.getPageNum(),
        MarketplacePluginDTORequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<MarketplacePluginDTO> customPluginDTOs = new MonitorPageResult<>();

    customPluginDTOs.setItems(marketplacePluginConverter.dosToDTOs(page.getRecords()));
    customPluginDTOs.setPageNum(MarketplacePluginDTORequest.getPageNum());
    customPluginDTOs.setPageSize(MarketplacePluginDTORequest.getPageSize());
    customPluginDTOs.setTotalCount(page.getTotal());
    customPluginDTOs.setTotalPage(page.getPages());

    return customPluginDTOs;
  }

  @Override
  public List<MarketplacePluginDTO> getListByKeyword(String keyword, String tenant) {
    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    wrapper.like("id", keyword).or().like("name", keyword);
    Page<MarketplacePlugin> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return marketplacePluginConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<MarketplacePluginDTO> getListByNameLike(String name, String tenant) {
    QueryWrapper<MarketplacePlugin> wrapper = new QueryWrapper<>();
    wrapper.select().like("name", name);
    List<MarketplacePlugin> customPlugins = baseMapper.selectList(wrapper);
    return marketplacePluginConverter.dosToDTOs(customPlugins);
  }
}
