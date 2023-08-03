/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.reflect.TypeToken;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.service.OpenmetricsScraperService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.OpenmetricsScraperConverter;
import io.holoinsight.server.home.dal.mapper.OpenmetricsScraperMapper;
import io.holoinsight.server.home.dal.model.OpenmetricsScraper;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.OpenmetricsScraperDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.registry.model.OpenmetricsScraperTask;

@Service
public class OpenmetricsScraperServiceImpl extends
    ServiceImpl<OpenmetricsScraperMapper, OpenmetricsScraper> implements OpenmetricsScraperService {

  private static final Type RELABEL_CONFIG_LIST_TYPE =
      new TypeToken<List<OpenmetricsScraperTask.RelabelConfig>>() {}.getType();

  @Autowired
  private OpenmetricsScraperConverter openmetricsScraperConverter;

  @Override
  public OpenmetricsScraperDTO queryById(Long id, String tenant, String workspace) {
    QueryWrapper<OpenmetricsScraper> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    if (StringUtils.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    OpenmetricsScraper model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }

    return toDTO(model);
  }

  @Override
  public void saveByDTO(OpenmetricsScraperDTO openmetricsScraperDTO) {
    OpenmetricsScraper model = toDO(openmetricsScraperDTO);
    saveOrUpdate(model);
    openmetricsScraperDTO.setId(model.getId());
    EventBusHolder.post(openmetricsScraperDTO);
  }

  @Override
  public void deleteById(Long id) {
    OpenmetricsScraper openmetricsScraper = getById(id);
    if (null == openmetricsScraper) {
      return;
    }
    removeById(id);
    EventBusHolder.post(toDTO(openmetricsScraper));
  }

  @Override
  public OpenmetricsScraperDTO toDTO(OpenmetricsScraper model) {
    Map<String, String> conf = model.getConf();
    if (conf == null) {
      conf = new HashMap<>();
      model.setConf(conf);
    }

    OpenmetricsScraperDTO dto = openmetricsScraperConverter.doToDTO(model);
    dto.setMetricsPath(conf.get("metricsPath"));
    dto.setSchema(conf.get("schema"));
    dto.setScrapeInterval(conf.get("scrapeInterval"));
    dto.setScrapeTimeout(conf.get("scrapeTimeout"));
    dto.setPort(conf.get("port"));

    dto.setRelabelConfigs(J.fromJson(conf.get("relabelConfigs"), RELABEL_CONFIG_LIST_TYPE));
    dto.setMetricRelabelConfigs(
        J.fromJson(conf.get("metricRelabelConfigs"), RELABEL_CONFIG_LIST_TYPE));

    dto.setCollectRanges(
        J.fromJson(conf.get("collectRanges"), (new TypeToken<CloudMonitorRange>() {}).getType()));
    return dto;
  }

  @Override
  public OpenmetricsScraper toDO(OpenmetricsScraperDTO dto) {
    OpenmetricsScraper model = openmetricsScraperConverter.dtoToDO(dto);
    if (model.getConf() == null) {
      model.setConf(new HashMap<>());
    }
    model.getConf().put("metricsPath", dto.getMetricsPath());
    model.getConf().put("schema", dto.getSchema());
    model.getConf().put("scrapeInterval", dto.getScrapeInterval());
    model.getConf().put("scrapeTimeout", dto.getScrapeTimeout());
    model.getConf().put("port", dto.getPort());
    model.getConf().put("collectRanges", J.toJson(dto.getCollectRanges()));
    model.getConf().put("relabelConfigs", J.toJson(dto.getRelabelConfigs()));
    model.getConf().put("metricRelabelConfigs", J.toJson(dto.getMetricRelabelConfigs()));
    return model;
  }

  @Override
  public MonitorPageResult<OpenmetricsScraperDTO> getListByPage(
      MonitorPageRequest<OpenmetricsScraperDTO> request) {
    if (request.getTarget() == null) {
      return null;
    }

    QueryWrapper<OpenmetricsScraper> wrapper = new QueryWrapper<>();

    OpenmetricsScraperDTO scraperDTO = request.getTarget();

    if (null != scraperDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", scraperDTO.getGmtCreate());
    }
    if (null != scraperDTO.getGmtModified()) {
      wrapper.le("gmt_modified", scraperDTO.getGmtCreate());
    }

    if (StringUtil.isNotBlank(scraperDTO.getCreator())) {
      wrapper.eq("creator", scraperDTO.getCreator().trim());
    }

    if (StringUtil.isNotBlank(scraperDTO.getModifier())) {
      wrapper.eq("modifier", scraperDTO.getModifier().trim());
    }

    if (null != scraperDTO.getId()) {
      wrapper.eq("id", scraperDTO.getId());
    }

    if (StringUtil.isNotBlank(scraperDTO.getTenant())) {
      wrapper.eq("tenant", scraperDTO.getTenant().trim());
    }

    if (StringUtil.isNotBlank(scraperDTO.getName())) {
      wrapper.like("name", scraperDTO.getName().trim());
    }

    if (StringUtils.isNotBlank(scraperDTO.getWorkspace())) {
      wrapper.eq("workspace", scraperDTO.getWorkspace());
    }

    wrapper.orderByDesc("gmt_modified");

    Page<OpenmetricsScraper> page = new Page<>(request.getPageNum(), request.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<OpenmetricsScraperDTO> scraperDTOMonitorPageResult =
        new MonitorPageResult<>();

    List<OpenmetricsScraper> records = page.getRecords();
    List<OpenmetricsScraperDTO> result = new ArrayList<>();
    records.forEach(r -> {
      result.add(toDTO(r));
    });

    scraperDTOMonitorPageResult.setItems(result);
    scraperDTOMonitorPageResult.setPageNum(request.getPageNum());
    scraperDTOMonitorPageResult.setPageSize(request.getPageSize());
    scraperDTOMonitorPageResult.setTotalCount(page.getTotal());
    scraperDTOMonitorPageResult.setTotalPage(page.getPages());

    return scraperDTOMonitorPageResult;
  }

}
