/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.IntegrationProductConverter;
import io.holoinsight.server.home.dal.mapper.IntegrationProductMapper;
import io.holoinsight.server.home.dal.model.IntegrationProduct;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
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
 * @author xiangwanpeng
 * @version 1.0: IntegrationProductServiceImpl.java, v 0.1 2022年06月08日 7:32 下午 xiangwanpeng Exp $
 */
@Service
public class IntegrationProductServiceImpl extends
    ServiceImpl<IntegrationProductMapper, IntegrationProduct> implements IntegrationProductService {

  @Autowired
  private IntegrationProductConverter IntegrationProductConverter;

  @Override
  public IntegrationProductDTO findById(Long id) {
    IntegrationProduct model = getById(id);
    if (model == null) {
      return null;
    }
    return IntegrationProductConverter.doToDTO(model);
  }

  @Override
  public List<IntegrationProductDTO> findByMap(Map<String, Object> columnMap) {
    List<IntegrationProduct> models = listByMap(columnMap);

    return IntegrationProductConverter.dosToDTOs(models);
  }

  @Override
  public IntegrationProductDTO create(IntegrationProductDTO IntegrationProductDTO) {
    IntegrationProductDTO.setGmtCreate(new Date());
    IntegrationProductDTO.setGmtModified(new Date());
    IntegrationProduct model = IntegrationProductConverter.dtoToDO(IntegrationProductDTO);
    save(model);
    IntegrationProductDTO customPluginDTOId = IntegrationProductConverter.doToDTO(model);
    EventBusHolder.post(customPluginDTOId);
    return customPluginDTOId;
  }

  @Override
  public void deleteById(Long id) {
    IntegrationProduct IntegrationProduct = getById(id);
    if (null == IntegrationProduct) {
      return;
    }
    removeById(id);
    EventBusHolder.post(IntegrationProduct);
  }

  @Override
  public IntegrationProductDTO updateByRequest(IntegrationProductDTO IntegrationProductDTO) {
    IntegrationProductDTO.setGmtModified(new Date());
    IntegrationProduct model = IntegrationProductConverter.dtoToDO(IntegrationProductDTO);
    saveOrUpdate(model);
    IntegrationProductDTO save = IntegrationProductConverter.doToDTO(model);
    EventBusHolder.post(save);
    return save;
  }

  @Override
  public MonitorPageResult<IntegrationProductDTO> getListByPage(
      MonitorPageRequest<IntegrationProductDTO> IntegrationProductDTORequest) {
    if (IntegrationProductDTORequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();

    IntegrationProductDTO IntegrationProductDTO = IntegrationProductDTORequest.getTarget();

    if (null != IntegrationProductDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", IntegrationProductDTO.getGmtCreate());
    }
    if (null != IntegrationProductDTO.getGmtModified()) {
      wrapper.le("gmt_modified", IntegrationProductDTO.getGmtCreate());
    }

    if (StringUtil.isNotBlank(IntegrationProductDTO.getCreator())) {
      wrapper.eq("creator", IntegrationProductDTO.getCreator().trim());
    }

    if (StringUtil.isNotBlank(IntegrationProductDTO.getModifier())) {
      wrapper.eq("modifier", IntegrationProductDTO.getModifier().trim());
    }

    if (null != IntegrationProductDTO.getId()) {
      wrapper.eq("id", IntegrationProductDTO.getId());
    }

    if (StringUtil.isNotBlank(IntegrationProductDTO.getName())) {
      wrapper.like("name", IntegrationProductDTO.getName().trim());
    }


    if (null != IntegrationProductDTO.getStatus()) {
      wrapper.eq("status", IntegrationProductDTO.getStatus());
    }
    wrapper.select(IntegrationProduct.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<IntegrationProduct> page = new Page<>(IntegrationProductDTORequest.getPageNum(),
        IntegrationProductDTORequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<IntegrationProductDTO> customPluginDTOs = new MonitorPageResult<>();

    customPluginDTOs.setItems(IntegrationProductConverter.dosToDTOs(page.getRecords()));
    customPluginDTOs.setPageNum(IntegrationProductDTORequest.getPageNum());
    customPluginDTOs.setPageSize(IntegrationProductDTORequest.getPageSize());
    customPluginDTOs.setTotalCount(page.getTotal());
    customPluginDTOs.setTotalPage(page.getPages());

    return customPluginDTOs;
  }

  @Override
  public List<IntegrationProductDTO> getListByKeyword(String keyword, String tenant) {
    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    wrapper.like("id", keyword).or().like("name", keyword);
    Page<IntegrationProduct> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return IntegrationProductConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<IntegrationProductDTO> getListByNameLike(String name, String tenant) {
    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();
    wrapper.select().like("name", name);
    List<IntegrationProduct> customPlugins = baseMapper.selectList(wrapper);
    return IntegrationProductConverter.dosToDTOs(customPlugins);
  }
}
