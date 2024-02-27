/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.CustomPluginConverter;
import io.holoinsight.server.home.dal.mapper.CustomPluginMapper;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginServiceImpl.java, v 0.1 2022年03月14日 7:32 下午 jinsong.yjs Exp $
 */
@Service
public class CustomPluginServiceImpl extends ServiceImpl<CustomPluginMapper, CustomPlugin>
    implements CustomPluginService {

  @Autowired
  private CustomPluginConverter customPluginConverter;
  @Autowired
  private TenantInitService tenantInitService;

  @Override
  public CustomPluginDTO queryById(Long id, String tenant, String workspace) {
    QueryWrapper<CustomPlugin> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    CustomPlugin model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }
    return doToDTO(model);
  }

  @Override
  public CustomPluginDTO queryById(Long id, String tenant) {
    return queryById(id, tenant, null);
  }

  @Override
  public List<CustomPluginDTO> findByMap(Map<String, Object> columnMap) {
    List<CustomPlugin> models = listByMap(columnMap);

    return dosToDTOs(models);
  }

  @Override
  public List<CustomPluginDTO> findByIds(List<String> ids) {

    QueryWrapper<CustomPlugin> wrapper = new QueryWrapper<>();
    wrapper.in("id", ids);
    List<CustomPlugin> customPlugins = baseMapper.selectList(wrapper);
    return dosToDTOs(customPlugins);
  }

  @Override
  public CustomPluginDTO create(CustomPluginDTO customPluginDTO) {
    customPluginDTO.setGmtCreate(new Date());
    customPluginDTO.setGmtModified(new Date());
    CustomPlugin model = customPluginConverter.dtoToDO(customPluginDTO);
    save(model);
    CustomPluginDTO customPluginDTOId = doToDTO(model);
    EventBusHolder.post(customPluginDTOId);
    return customPluginDTOId;
  }

  @Override
  public void deleteById(Long id) {
    CustomPlugin customPlugin = getById(id);
    if (null == customPlugin) {
      return;
    }
    removeById(id);
    customPlugin.setStatus("OFFLINE");
    EventBusHolder.post(doToDTO(customPlugin));
  }

  @Override
  public CustomPluginDTO updateByRequest(CustomPluginDTO customPluginDTO) {
    customPluginDTO.setGmtModified(new Date());
    CustomPlugin model = customPluginConverter.dtoToDO(customPluginDTO);
    updateById(model);
    CustomPluginDTO save = doToDTO(model);
    EventBusHolder.post(save);
    return save;
  }

  @Override
  public MonitorPageResult<CustomPluginDTO> getListByPage(
      MonitorPageRequest<CustomPluginDTO> customPluginDTORequest) {
    if (customPluginDTORequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<CustomPlugin> wrapper = new QueryWrapper<>();

    CustomPluginDTO customPluginDTO = customPluginDTORequest.getTarget();

    if (null != customPluginDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", customPluginDTO.getGmtCreate());
    }
    if (null != customPluginDTO.getGmtModified()) {
      wrapper.le("gmt_modified", customPluginDTO.getGmtCreate());
    }

    if (StringUtil.isNotBlank(customPluginDTO.getCreator())) {
      wrapper.eq("creator", customPluginDTO.getCreator().trim());
    }

    if (StringUtil.isNotBlank(customPluginDTO.getModifier())) {
      wrapper.eq("modifier", customPluginDTO.getModifier().trim());
    }

    if (null != customPluginDTO.getId()) {
      wrapper.eq("id", customPluginDTO.getId());
    }

    if (StringUtil.isNotBlank(customPluginDTO.getTenant())) {
      wrapper.eq("tenant", customPluginDTO.getTenant().trim());
    }

    if (StringUtils.isNotBlank(customPluginDTO.getWorkspace())) {
      wrapper.eq("workspace", customPluginDTO.getWorkspace());
    }

    if (StringUtil.isNotBlank(customPluginDTO.getName())) {
      wrapper.like("name", customPluginDTO.getName().trim());
    }

    if (null != customPluginDTO.getPeriodType()) {
      wrapper.eq("period_type", customPluginDTO.getPeriodType().name());
    }

    if (StringUtil.isNotBlank(customPluginDTO.getPluginType())) {
      wrapper.eq("plugin_type", customPluginDTO.getPluginType().trim());
    }

    if (null != customPluginDTO.getParentFolderId()) {
      wrapper.eq("parent_folder_id", customPluginDTO.getParentFolderId());
    }

    wrapper.orderByDesc("id");

    wrapper.select(CustomPlugin.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<CustomPlugin> page =
        new Page<>(customPluginDTORequest.getPageNum(), customPluginDTORequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<CustomPluginDTO> customPluginDTOs = new MonitorPageResult<>();

    customPluginDTOs.setItems(dosToDTOs(page.getRecords()));
    customPluginDTOs.setPageNum(customPluginDTORequest.getPageNum());
    customPluginDTOs.setPageSize(customPluginDTORequest.getPageSize());
    customPluginDTOs.setTotalCount(page.getTotal());
    customPluginDTOs.setTotalPage(page.getPages());

    return customPluginDTOs;
  }

  @Override
  public List<CustomPluginDTO> getListByKeyword(String keyword, String tenant, String workspace) {
    QueryWrapper<CustomPlugin> wrapper = new QueryWrapper<>();
    if (StringUtil.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }

    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.and(wa -> wa.like("id", keyword).or().like("name", keyword));
    wrapper.last("LIMIT 10");
    return dosToDTOs(baseMapper.selectList(wrapper));
  }

  @Override
  public List<CustomPluginDTO> getListByNameLike(String name, String tenant, String workspace) {
    QueryWrapper<CustomPlugin> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);

    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }

    wrapper.select().like("name", name);
    List<CustomPlugin> customPlugins = baseMapper.selectList(wrapper);
    return dosToDTOs(customPlugins);
  }

  private CustomPluginDTO doToDTO(CustomPlugin customPlugin) {
    CustomPluginDTO customPluginDTO = customPluginConverter.doToDTO(customPlugin);

    if (null != customPluginDTO.getConf()
        && !CollectionUtils.isEmpty(customPluginDTO.getConf().collectMetrics)) {
      customPluginDTO.getConf().collectMetrics.forEach(collectMetric -> {
        String tableName = collectMetric.tableName + "_" + customPluginDTO.id;
        if (StringUtil.isNotBlank(collectMetric.name)) {
          tableName = collectMetric.name;
        }
        collectMetric.targetTable = tenantInitService.getLogMonitorMetricTable(tableName);
        if (null != collectMetric.getCalculate() && Boolean.TRUE == collectMetric.getCalculate()) {
          String aggTableName = collectMetric.tableName + "_agg_" + customPluginDTO.id;
          collectMetric.logCalculate.aggTableName =
              tenantInitService.getLogMonitorMetricTable(aggTableName);
        }
      });
    }
    return customPluginDTO;
  }

  private List<CustomPluginDTO> dosToDTOs(List<CustomPlugin> customPlugins) {
    List<CustomPluginDTO> customPluginDTOS = customPluginConverter.dosToDTOs(customPlugins);

    if (!CollectionUtils.isEmpty(customPluginDTOS)) {
      customPluginDTOS.forEach(customPluginDTO -> {
        if (null != customPluginDTO.getConf()
            && !CollectionUtils.isEmpty(customPluginDTO.getConf().collectMetrics)) {
          customPluginDTO.getConf().collectMetrics.forEach(collectMetric -> {
            String tableName = collectMetric.tableName + "_" + customPluginDTO.id;
            if (StringUtil.isNotBlank(collectMetric.name)) {
              tableName = collectMetric.name;
            }
            collectMetric.targetTable = tenantInitService.getLogMonitorMetricTable(tableName);

            if (null != collectMetric.getCalculate()
                && Boolean.TRUE == collectMetric.getCalculate()) {
              String aggTableName = collectMetric.tableName + "_agg_" + customPluginDTO.id;
              collectMetric.logCalculate.aggTableName =
                  tenantInitService.getLogMonitorMetricTable(aggTableName);
            }
          });
        }
      });
    }
    return customPluginDTOS;
  }

  @Override
  public Boolean updateById(CustomPluginDTO customPluginDTO) {
    CustomPlugin model = customPluginConverter.dtoToDO(customPluginDTO);
    Boolean result = updateById(model);
    CustomPluginDTO save = doToDTO(model);
    EventBusHolder.post(save);
    return result;
  }
}
