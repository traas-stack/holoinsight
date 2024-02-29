/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.converter.MetricInfoConverter;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.QueryRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
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
  public List<MetricInfo> queryListByWorkspace(String workspace) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("workspace", workspace);
    columnMap.put("deleted", 0);
    List<MetricInfo> metricInfos = listByMap(columnMap);
    if (CollectionUtils.isEmpty(metricInfos)) {
      return null;
    }
    return metricInfos;
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
  public List<MetricInfoDTO> queryListByRef(String tenant, String workspace, String product,
      String ref) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("tenant", tenant);
    columnMap.put("workspace", workspace);
    columnMap.put("product", product);
    columnMap.put("ref", ref);
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
      Map<String, Object> newColumnMap = new HashMap<>();
      // newColumnMap.put("tenant", "-");
      // newColumnMap.put("workspace", "-");
      newColumnMap.put("metric_table", metric);
      newColumnMap.put("deleted", 0);
      List<MetricInfo> globalMetrics = listByMap(newColumnMap);
      if (CollectionUtils.isEmpty(globalMetrics)) {
        return null;
      }
      return metricInfoConverter.doToDTO(globalMetrics.get(0));
    }
    return metricInfoConverter.doToDTO(metricInfos.get(0));
  }

  @Override
  public Map<String, QueryRequest> querySpmList() {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("deleted", 0);
    columnMap.put("metric_type", "logspm");
    List<MetricInfo> metricInfos = listByMap(columnMap);
    if (CollectionUtils.isEmpty(metricInfos)) {
      return new HashMap<>();
    }

    List<MetricInfoDTO> metricInfoDTOS = metricInfoConverter.dosToDTOs(metricInfos);
    Map<String, QueryRequest> map = new HashMap<>();
    metricInfoDTOS.forEach(metricInfoDTO -> {
      QueryProto.QueryRequest.Builder builder = QueryProto.QueryRequest.newBuilder();
      String downsample = "1m";
      switch (metricInfoDTO.getPeriod()) {
        case 1:
          downsample = "1s";
          break;
        case 5:
          downsample = "5s";
          break;
      }

      builder.setFillPolicy("percent");
      builder.setQuery("a/b*100");
      builder.setDownsample(downsample);
      builder.setTenant(metricInfoDTO.getTenant());

      QueryProto.Datasource.Builder datasourceBuilderA = QueryProto.Datasource.newBuilder();
      datasourceBuilderA.setName("a");
      datasourceBuilderA.setAggregator("sum");
      datasourceBuilderA
          .setMetric(metricInfoDTO.getMetricTable().replace("successPercent", "success"));

      QueryProto.Datasource.Builder datasourceBuilderB = QueryProto.Datasource.newBuilder();
      datasourceBuilderB.setName("b");
      datasourceBuilderB.setAggregator("sum");
      datasourceBuilderB
          .setMetric(metricInfoDTO.getMetricTable().replace("successPercent", "total"));
      builder
          .addAllDatasources(Arrays.asList(datasourceBuilderA.build(), datasourceBuilderB.build()));
      map.put(metricInfoDTO.getMetricTable(), builder.build());
    });
    return map;
  }

  @Override
  public List<MetricInfoDTO> getListByKeyword(String keyword, String tenant, String workspace) {
    QueryWrapper<MetricInfo> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(tenant) || "-".equalsIgnoreCase(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    if (StringUtils.isNotBlank(workspace) || "-".equalsIgnoreCase(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("deleted", 0);
    wrapper.like("metric_table", keyword);
    wrapper.last("LIMIT 10");
    return metricInfoConverter.dosToDTOs(baseMapper.selectList(wrapper));
  }
}
