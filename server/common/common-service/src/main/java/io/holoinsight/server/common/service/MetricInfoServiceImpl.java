/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.converter.MetricInfoConverter;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: MetricInfoServiceImpl.java, Date: 2023-04-24 Time: 20:28
 */
@Service
public class MetricInfoServiceImpl extends ServiceImpl<MetricInfoMapper, MetricInfo>
    implements MetricInfoService {

  @Autowired
  private MetricInfoConverter metricInfoConverter;

  @Override
  public void create(MetricInfoDTO metricInfoDTO) {
    metricInfoDTO.setGmtCreate(new Date());
    metricInfoDTO.setGmtModified(new Date());

    save(metricInfoConverter.dtoToDO(metricInfoDTO));
  }

  @Override
  public void update(MetricInfoDTO metricInfoDTO) {
    metricInfoDTO.setGmtModified(new Date());
    updateById(metricInfoConverter.dtoToDO(metricInfoDTO));
  }

  @Override
  public List<MetricInfoDTO> queryListByTenant(String tenant, String workspace) {
    Map<String, Object> columnMap = new HashMap<>();
    if (StringUtils.isNotBlank(tenant)) {
      columnMap.put("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace)) {
      columnMap.put("workspace", workspace);
    }
    columnMap.put("deleted", 0);
    List<MetricInfo> metricInfos = listByMap(columnMap);
    if (CollectionUtils.isEmpty(metricInfos)) {
      return null;
    }
    return metricInfoConverter.dosToDTOs(metricInfos);
  }

  @Override
  public List<MetricInfoDTO> queryListByTenantProduct(String tenant, String workspace,
      String product) {
    Map<String, Object> columnMap = new HashMap<>();
    if (StringUtils.isNotBlank(tenant) || "-".equalsIgnoreCase(tenant)) {
      columnMap.put("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace) || "-".equalsIgnoreCase(workspace)) {
      columnMap.put("workspace", workspace);
    }
    columnMap.put("product", product);
    columnMap.put("deleted", 0);
    List<MetricInfo> metricInfos = listByMap(columnMap);
    if (CollectionUtils.isEmpty(metricInfos)) {
      return null;
    }
    return metricInfoConverter.dosToDTOs(metricInfos);
  }

  @Override
  public MetricInfoDTO queryByMetric(String tenant, String workspace, String metric) {
    Map<String, Object> columnMap = new HashMap<>();

    if (StringUtils.isNotBlank(tenant) || "-".equalsIgnoreCase(tenant)) {
      columnMap.put("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace) || "-".equalsIgnoreCase(workspace)) {
      columnMap.put("workspace", workspace);
    }
    columnMap.put("metric_table", metric);
    columnMap.put("deleted", 0);
    List<MetricInfo> metricInfos = listByMap(columnMap);
    if (CollectionUtils.isEmpty(metricInfos)) {
      return null;
    }
    return metricInfoConverter.doToDTO(metricInfos.get(0));
  }
}
