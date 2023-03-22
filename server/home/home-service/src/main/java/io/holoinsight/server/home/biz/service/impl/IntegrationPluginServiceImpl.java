/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.plugin.PluginRepository;
import io.holoinsight.server.home.biz.service.IntegrationPluginService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.IntegrationPluginConverter;
import io.holoinsight.server.home.dal.mapper.IntegrationPluginMapper;
import io.holoinsight.server.home.dal.model.IntegrationPlugin;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version 1.0: IntegrationPluginServiceImpl.java, v 0.1 2022年06月08日 7:32 下午 xiangwanpeng Exp $
 */
@Service
public class IntegrationPluginServiceImpl extends
    ServiceImpl<IntegrationPluginMapper, IntegrationPlugin> implements IntegrationPluginService {

  @Resource
  private IntegrationPluginConverter integrationPluginConverter;
  @Autowired
  private PluginRepository pluginRepository;

  @Override
  public IntegrationPluginDTO queryById(Long id, String tenant, String workspace) {

    QueryWrapper<IntegrationPlugin> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    IntegrationPlugin model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }
    return integrationPluginConverter.doToDTO(model);
  }

  @Override
  public IntegrationPluginDTO queryByName(String name, String tenant, String workspace) {
    QueryWrapper<IntegrationPlugin> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("product", name);
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    IntegrationPlugin model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }
    return integrationPluginConverter.doToDTO(model);
  }

  @Override
  public List<IntegrationPluginDTO> queryByTenant(String tenant) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("tenant", tenant);

    return integrationPluginConverter.dosToDTOs(listByMap(columnMap));
  }

  @Override
  public List<IntegrationPluginDTO> queryByTenant(String tenant, String workspace) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      columnMap.put("workspace", workspace);
    }

    return integrationPluginConverter.dosToDTOs(listByMap(columnMap));
  }

  @Override
  public List<IntegrationPluginDTO> findByMap(Map<String, Object> columnMap) {
    List<IntegrationPlugin> models = listByMap(columnMap);
    return integrationPluginConverter.dosToDTOs(models);
  }

  @Override
  public IntegrationPluginDTO create(IntegrationPluginDTO integrationPluginDTO) {
    integrationPluginDTO.setGmtCreate(new Date());
    integrationPluginDTO.setGmtModified(new Date());


    IntegrationPlugin model = integrationPluginConverter.dtoToDO(integrationPluginDTO);
    save(model);
    IntegrationPluginDTO integrationPlugin = integrationPluginConverter.doToDTO(model);

    EventBusHolder.post(integrationPlugin);


    return integrationPlugin;
  }

  public static boolean isClassicPlugin(String product) {
    if (StringUtils.isEmpty(product)) {
      return false;
    }
    return product.equals("JVM") || product.equals("MySQL") || product.equals("AlibabaCloud")
        || product.equals("SpringBoot");
  }

  /**
   * 1. 如果是数据源插件，要检查修改是不是要同步修改采集配置 1.1 如果是创建，要插入 gaea 配置表 1.2 如果是版本升级，要对比修改后的 json (不包含)有没有变化 1.3
   * 如果是采集范围变化变化，要插入 gaea 配置表
   * 
   * @param newIntegrationPlugin
   * @param originalRecord
   * @param pluginRepository
   */
  public static boolean checkActionType(IntegrationPluginDTO newIntegrationPlugin,
      IntegrationPluginDTO originalRecord, PluginRepository pluginRepository) {
    // 是否数据源插件
    boolean isDataSourcePlugin = pluginRepository.isDataSourcePlugin(newIntegrationPlugin.type);
    boolean isCreate = originalRecord == null;
    boolean needUpsertGaea = false;
    if (isDataSourcePlugin) {
      if (isCreate) {
        needUpsertGaea = true;
      } else {
        // 是否插件升级
        boolean shouldUpgrade = newIntegrationPlugin.checkVersion(originalRecord);
        // 是否变更了采集配置
        boolean shouldChangeJson = newIntegrationPlugin.checkJsonChange(originalRecord);
        if (shouldUpgrade || shouldChangeJson) {
          needUpsertGaea = true;
        }
      }
      newIntegrationPlugin.name = newIntegrationPlugin.tenant + '_' + newIntegrationPlugin.type;
    }

    return needUpsertGaea;
  }

  @Override
  public void deleteById(Long id) {
    IntegrationPlugin integrationPlugin = getById(id);
    if (null == integrationPlugin) {
      return;
    }
    integrationPlugin.setStatus(false);
    removeById(id);
    IntegrationPluginDTO deleted = integrationPluginConverter.doToDTO(integrationPlugin);
    EventBusHolder.post(deleted);
  }

  @Override
  public IntegrationPluginDTO updateByRequest(IntegrationPluginDTO integrationPluginDTO) {
    integrationPluginDTO.setGmtModified(new Date());
    IntegrationPluginDTO originalRecord = queryById(integrationPluginDTO.getId(),
        integrationPluginDTO.getTenant(), integrationPluginDTO.getWorkspace());

    boolean needUpsertGaea = isClassicPlugin(integrationPluginDTO.getProduct())
        || checkActionType(integrationPluginDTO, originalRecord, this.pluginRepository);
    IntegrationPlugin model = integrationPluginConverter.dtoToDO(integrationPluginDTO);
    saveOrUpdate(model);
    IntegrationPluginDTO integrationPlugin = integrationPluginConverter.doToDTO(model);
    if (needUpsertGaea) {
      EventBusHolder.post(integrationPlugin);
    }

    return integrationPlugin;
  }

  @Override
  public MonitorPageResult<IntegrationPluginDTO> getListByPage(
      MonitorPageRequest<IntegrationPluginDTO> integrationPluginDTORequest) {
    if (integrationPluginDTORequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<IntegrationPlugin> wrapper = new QueryWrapper<>();

    IntegrationPluginDTO integrationPluginDTO = integrationPluginDTORequest.getTarget();

    if (null != integrationPluginDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", integrationPluginDTO.getGmtCreate());
    }
    if (null != integrationPluginDTO.getGmtModified()) {
      wrapper.le("gmt_modified", integrationPluginDTO.getGmtCreate());
    }

    if (StringUtils.isNotBlank(integrationPluginDTO.getCreator())) {
      wrapper.eq("creator", integrationPluginDTO.getCreator().trim());
    }

    if (StringUtils.isNotBlank(integrationPluginDTO.getModifier())) {
      wrapper.eq("modifier", integrationPluginDTO.getModifier().trim());
    }

    if (null != integrationPluginDTO.getId()) {
      wrapper.eq("id", integrationPluginDTO.getId());
    }

    if (StringUtils.isNotBlank(integrationPluginDTO.getTenant())) {
      wrapper.eq("tenant", integrationPluginDTO.getTenant().trim());
    }

    if (StringUtils.isNotBlank(integrationPluginDTO.getWorkspace())) {
      wrapper.eq("workspace", integrationPluginDTO.getWorkspace().trim());
    }

    if (StringUtils.isNotBlank(integrationPluginDTO.getName())) {
      wrapper.like("name", integrationPluginDTO.getName().trim());
    }

    if (null != integrationPluginDTO.getType()) {
      wrapper.eq("type", integrationPluginDTO.getType());
    }

    if (null != integrationPluginDTO.getStatus()) {
      wrapper.eq("status", integrationPluginDTO.getStatus());
    }
    wrapper.select(IntegrationPlugin.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<IntegrationPlugin> page = new Page<>(integrationPluginDTORequest.getPageNum(),
        integrationPluginDTORequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<IntegrationPluginDTO> customPluginDTOs = new MonitorPageResult<>();

    customPluginDTOs.setItems(integrationPluginConverter.dosToDTOs(page.getRecords()));
    customPluginDTOs.setPageNum(integrationPluginDTORequest.getPageNum());
    customPluginDTOs.setPageSize(integrationPluginDTORequest.getPageSize());
    customPluginDTOs.setTotalCount(page.getTotal());
    customPluginDTOs.setTotalPage(page.getPages());

    return customPluginDTOs;
  }

  @Override
  public List<IntegrationPluginDTO> getListByKeyword(String keyword, String tenant,
      String workspace) {
    QueryWrapper<IntegrationPlugin> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }

    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.like("id", keyword).or().like("name", keyword);
    Page<IntegrationPlugin> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return integrationPluginConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<IntegrationPluginDTO> getListByNameLike(String name, String tenant,
      String workspace) {
    QueryWrapper<IntegrationPlugin> wrapper = new QueryWrapper<>();
    wrapper.select().like("name", name);
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }

    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    List<IntegrationPlugin> customPlugins = baseMapper.selectList(wrapper);
    return integrationPluginConverter.dosToDTOs(customPlugins);
  }
}
