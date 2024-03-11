/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.holoinsight.server.common.LatchWork;
import io.holoinsight.server.common.UtilMisc;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.common.service.query.KeyResult;
import io.holoinsight.server.home.common.service.query.QueryResponse;
import io.holoinsight.server.home.common.service.query.QuerySchemaResponse;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.home.common.service.query.ValueResult;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.PqlParser;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.common.pql.PqlException;
import io.holoinsight.server.common.model.DataQueryRequest;
import io.holoinsight.server.common.model.DataQueryRequest.QueryDataSource;
import io.holoinsight.server.common.model.DataQueryRequest.QueryFilter;
import io.holoinsight.server.home.web.controller.model.DelTagReq;
import io.holoinsight.server.home.web.controller.model.PqlInstanceRequest;
import io.holoinsight.server.home.web.controller.model.PqlParseRequest;
import io.holoinsight.server.home.web.controller.model.PqlParseResult;
import io.holoinsight.server.home.web.controller.model.PqlRangeQueryRequest;
import io.holoinsight.server.home.web.controller.model.TagQueryRequest;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.Datasource;
import io.holoinsight.server.query.grpc.QueryProto.Datasource.Builder;
import io.holoinsight.server.query.grpc.QueryProto.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.holoinsight.server.home.biz.common.MetaDictKey.SCHEMA_METRIC_TABLE_PREFIX;
import static io.holoinsight.server.home.biz.common.MetaDictType.GLOBAL_CONFIG;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_TENANT;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_WORKSPACE;

@RestController
@RequestMapping("/webapi/v1/query")
@TokenUrls("/webapi/v1/query")
@Slf4j
public class QueryFacadeImpl extends BaseFacade {

  @Autowired
  private QueryClientService queryClientService;

  @Autowired
  private PqlParser pqlParser;

  @Autowired
  private CommonThreadPools commonThreadPools;

  @Autowired
  private TenantInitService tenantInitService;

  @Autowired
  private SuperCacheService superCacheService;

  @Autowired
  private ParameterSecurityService parameterSecurityService;
  @Resource
  private MetricInfoMapper metricInfoMapper;

  @PostMapping
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<QueryResponse> query(@RequestBody DataQueryRequest request) {

    final JsonResult<QueryResponse> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotEmpty(request.datasources, "datasources");
        ParaCheckUtil.checkParaNotBlank(request.datasources.get(0).metric, "metric");
        ParaCheckUtil.checkTimeRange(request.datasources.get(0).start,
            request.datasources.get(0).end, "start time must less than end time");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
        ParaCheckUtil.checkCustomQl(request, "Refusing to execute sql query");
        ParaCheckUtil.checkSQlInjection(getGroupBys(request), "Illegal params in groupby");
        ParaCheckUtil.checkSQlInjection(parameterSecurityService.getDetailFilters(request),
            "Illegal params in filter");
      }

      @Override
      public void doManage() {
        try {
          QueryResponse response = queryClientService.query(convertRequest(request));
          JsonResult.createSuccessResult(result, response);
        } catch (Throwable t) {
          log.error("query error", t);
          QueryResponse response = new QueryResponse();
          if ((t.getMessage().contains("Evaluated points num")
              || t.getMessage().contains("Evaluated series num"))
              && t.getMessage().contains("larger than")) {
            response.setErrorCode(ResultCodeEnum.EXCEED_SERIES_LIMIT.name());
          } else {
            response.setErrorCode(ResultCodeEnum.DOWNSTREAM_SYSTEM_ERROR.name());
          }
          response.setSuccess(false);
          response.setMessage(t.getMessage());
          result.setData(response);
          JsonResult.fillFailResultTo(result, t.getMessage());
        }
      }
    });

    return result;
  }

  private List<String> getGroupBys(DataQueryRequest request) {
    if (request == null || CollectionUtils.isEmpty(request.getDatasources())) {
      return Collections.emptyList();
    }
    Set<String> groupBys = new HashSet<>();
    for (QueryDataSource queryDataSource : request.getDatasources()) {
      if (CollectionUtils.isEmpty(queryDataSource.getGroupBy())) {
        continue;
      }
      groupBys.addAll(queryDataSource.getGroupBy());
    }
    return new ArrayList<>(groupBys);
  }

  @PostMapping("/tags")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<?> queryTags(@RequestBody DataQueryRequest request) {

    final JsonResult<QueryResponse> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotEmpty(request.datasources, "datasources");
        ParaCheckUtil.checkParaNotBlank(request.datasources.get(0).metric, "metric");
        ParaCheckUtil.checkTimeRange(request.datasources.get(0).start,
            request.datasources.get(0).end, "start time must less than end time");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
        ParaCheckUtil.checkCustomQl(request, "Refusing to execute sql query");
        ParaCheckUtil.checkSQlInjection(getGroupBys(request), "Illegal params in groupby");
      }

      @Override
      public void doManage() {

        try {
          QueryResponse response = queryClientService.queryTags(convertRequest(request));
          JsonResult.createSuccessResult(result, response);
        } catch (Throwable t) {
          QueryResponse response = new QueryResponse();
          if ((t.getMessage().contains("Evaluated points num")
              || t.getMessage().contains("Evaluated series num"))
              && t.getMessage().contains("larger than")) {
            response.setErrorCode(ResultCodeEnum.EXCEED_SERIES_LIMIT.name());
          } else {
            response.setErrorCode(ResultCodeEnum.DOWNSTREAM_SYSTEM_ERROR.name());
          }
          response.setSuccess(false);
          response.setMessage(t.getMessage());
          result.setData(response);
          JsonResult.fillFailResultTo(result, t.getMessage());
        }
      }
    });

    return result;
  }

  @DeleteMapping("/deltags")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<?> delTags(@RequestBody DelTagReq request) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        ParaCheckUtil.checkParaNotNull(request.getMetric(), "metric");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        List<DataQueryRequest> queryRequests = convertQueryRequest(request);
        if (CollectionUtils.isEmpty(queryRequests)) {
          log.info("queryRequests is empty");
          return;
        }
        final CountDownLatch latch = new CountDownLatch(queryRequests.size());
        for (DataQueryRequest dataQueryRequest : queryRequests) {

          final QueryRequest queryRequest = convertRequest(dataQueryRequest);
          commonThreadPools.getScheduler().execute(new LatchWork("delTags", latch) {
            @Override
            public void doWork() {
              queryClientService.delTags(queryRequest);
            }

            @Override
            public void doException(String msg) {
              log.error("queryRequests error, {}", msg);
            }
          });

        }
        try {
          latch.await(30L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          log.info("delTags timeout {}", e.getMessage());
          JsonResult.fillFailResultTo(result, "delTags timeout " + e.getMessage());
          return;
        }
        JsonResult.createSuccessResult(result, true);
      }
    });
    return result;
  }

  @GetMapping("/schema")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<KeyResult> tagsKey(@RequestParam("name") String metric) {
    final JsonResult<KeyResult> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        if (useGlobalMetricInfo(metric)) {
          MetricInfo metricInfo = getGlobalMetricInfo(metric);
          if (metricInfo != null) {
            List<String> tags = getTagsFromMetricInfo(metricInfo);
            KeyResult keyResult = new KeyResult();
            keyResult.setMetric(metric);
            keyResult.setTags(tags);
            result.setData(keyResult);
            return;
          }
        }
        MonitorScope ms = RequestContext.getContext().ms;
        Builder builder = Datasource.newBuilder().setMetric(metric)
            .setStart(System.currentTimeMillis() - 60000 * 60 * 5)
            .setEnd(System.currentTimeMillis() - 60000 * 5);

        List<QueryProto.QueryFilter> tenantFilters = tenantInitService
            .getTenantFilters(ms.getTenant(), ms.getWorkspace(), ms.getEnvironment(), metric);
        if (!CollectionUtils.isEmpty(tenantFilters)) {
          builder.addAllFilters(tenantFilters);
        }

        QueryProto.QueryRequest.Builder requestBuilder = QueryProto.QueryRequest.newBuilder();
        MetricInfo metricInfo = getMetricInfo(metric);
        requestBuilder.setTenant(tenantInitService.getTsdbTenant(ms.getTenant(), metricInfo));
        if (isCeresdb4Storage(metricInfo)) {
          builder.setQl("DESCRIBE " + metric);
        }

        QueryProto.QueryRequest request =
            requestBuilder.addAllDatasources(Collections.singletonList(builder.build())).build();
        QuerySchemaResponse querySchema = queryClientService.querySchema(request);
        KeyResult keyResult = new KeyResult();
        if (querySchema != null && querySchema.getResults() != null
            && !querySchema.getResults().isEmpty()) {
          keyResult = querySchema.getResults().get(0);
        }
        JsonResult.createSuccessResult(result, keyResult);
      }
    });

    return result;
  }

  private List<String> getTagsFromMetricInfo(MetricInfo metricInfo) {
    String tags = metricInfo.getTags();
    if (StringUtils.isBlank(tags)) {
      return Collections.emptyList();
    }
    return J.toList(tags);
  }

  private MetricInfo getGlobalMetricInfo(String metric) {
    QueryWrapper<MetricInfo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("tenant", GLOBAL_TENANT);
    queryWrapper.eq("workspace", GLOBAL_WORKSPACE);
    queryWrapper.eq("metric_table", metric);
    List<MetricInfo> metricInfos = metricInfoMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(metricInfos)) {
      return null;
    }
    return metricInfos.get(0);
  }

  private boolean useGlobalMetricInfo(String metric) {
    List<String> schemaMetricTablePrefix =
        MetaDictUtil.getList(GLOBAL_CONFIG, SCHEMA_METRIC_TABLE_PREFIX);
    if (CollectionUtils.isEmpty(schemaMetricTablePrefix)) {
      return false;
    }
    for (String prefix : schemaMetricTablePrefix) {
      if (metric.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 模糊匹配查询
   *
   * @param name
   * @return
   */
  @GetMapping("/metric")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<String>> metric(@RequestParam("name") String name) {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryProto.QueryMetricsRequest.Builder builder =
            QueryProto.QueryMetricsRequest.newBuilder();
        if (null != ms) {
          builder.setTenant(tenantInitService.getTsdbTenant(ms.getTenant()));
        }
        builder.setName(name);
        builder.setLimit(3000);
        QueryProto.QueryMetricsRequest queryMetricsRequest = builder.build();
        List<String> list = queryClientService.queryMetrics(queryMetricsRequest);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  /**
   * 查询指定tag的值
   *
   * @param tagQueryRequest
   * @return
   */
  @PostMapping("/tagValues")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<ValueResult> queryTagValues(@RequestBody TagQueryRequest tagQueryRequest) {

    final JsonResult<ValueResult> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(tagQueryRequest, "request");
        ParaCheckUtil.checkParaNotNull(tagQueryRequest.getMetric(), "metric");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Builder builder = Datasource.newBuilder().setMetric(tagQueryRequest.getMetric())
            .setStart(System.currentTimeMillis() - 60000 * 60 * 5)
            .setEnd(System.currentTimeMillis()).setAggregator("count")
            .addAllGroupBy(Collections.singletonList(tagQueryRequest.getKey()));

        List<QueryProto.QueryFilter> tenantFilters = tenantInitService.getTenantFilters(
            ms.getTenant(), ms.getWorkspace(), ms.getEnvironment(), tagQueryRequest.getMetric());

        if (!CollectionUtils.isEmpty(tagQueryRequest.getConditions())) {
          tagQueryRequest.getConditions().forEach((k, v) -> {
            tenantFilters.add(QueryProto.QueryFilter.newBuilder().setName(k).setType("literal_or")
                .setValue(v).build());
          });
        }

        if (!CollectionUtils.isEmpty(tenantFilters)) {
          builder.addAllFilters(tenantFilters);
        }

        QueryProto.Datasource datasource = builder.build();
        QueryProto.QueryRequest.Builder requestBuilder = QueryProto.QueryRequest.newBuilder()
            .addAllDatasources(Collections.singletonList(datasource));
        requestBuilder.setTenant(tenantInitService.getTsdbTenant(ms.getTenant()));

        ValueResult response =
            queryClientService.queryTagValues(requestBuilder.build(), tagQueryRequest.getKey());
        JsonResult.createSuccessResult(result, response);
      }
    });

    return result;
  }

  @PostMapping(value = "/pql/range")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Result>> pqlRangeQuery(@RequestBody PqlRangeQueryRequest request) {

    final JsonResult<List<Result>> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        try {
          QueryProto.PqlRangeRequest rangeRequest =
              QueryProto.PqlRangeRequest.newBuilder().setQuery(request.getQuery())
                  .setTenant(
                      tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
                  .setTimeout(request.getTimeout()).setStart(request.getStart())
                  .setEnd(request.getEnd()).setFillZero(request.getFillZero())
                  .setStep(request.getStep()).build();
          QueryResponse response = queryClientService.pqlRangeQuery(rangeRequest);
          JsonResult.createSuccessResult(result, response.getResults());
        } catch (Throwable t) {
          QueryResponse response = new QueryResponse();
          if ((t.getMessage().contains("Evaluated points num")
              || t.getMessage().contains("Evaluated series num"))
              && t.getMessage().contains("larger than")) {
            response.setErrorCode(ResultCodeEnum.EXCEED_SERIES_LIMIT.name());
          } else {
            response.setErrorCode(ResultCodeEnum.DOWNSTREAM_SYSTEM_ERROR.name());
          }
          response.setSuccess(false);
          response.setMessage(t.getMessage());
          JsonResult.fillFailResultTo(result, t.getMessage());
        }
      }
    });

    return result;
  }

  @PostMapping(value = "/pql/instant")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Result>> pqlInstanceQuery(@RequestBody PqlInstanceRequest request) {
    final JsonResult<List<Result>> result = new JsonResult<>();
    RequestContext.getContext();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request, "request");
        if (StringUtils.isNotBlank(request.getTenant())) {
          ParaCheckUtil.checkEquals(request.getTenant(), RequestContext.getContext().ms.getTenant(),
              "tenant is illegal");
        }
      }

      @Override
      public void doManage() {
        try {
          QueryProto.PqlInstantRequest instantRequest =
              QueryProto.PqlInstantRequest.newBuilder().setQuery(request.getQuery())
                  .setTenant(
                      tenantInitService.getTsdbTenant(RequestContext.getContext().ms.getTenant()))
                  .setDelta(request.getDelta()).setTimeout(request.getTimeout())
                  .setTime(request.getTime()).build();
          QueryResponse response = queryClientService.pqlInstantQuery(instantRequest);
          JsonResult.createSuccessResult(result, response.getResults());
        } catch (Throwable t) {
          QueryResponse response = new QueryResponse();
          if ((t.getMessage().contains("Evaluated points num")
              || t.getMessage().contains("Evaluated series num"))
              && t.getMessage().contains("larger than")) {
            response.setErrorCode(ResultCodeEnum.EXCEED_SERIES_LIMIT.name());
          } else {
            response.setErrorCode(ResultCodeEnum.DOWNSTREAM_SYSTEM_ERROR.name());
          }
          response.setSuccess(false);
          response.setMessage(t.getMessage());
          JsonResult.fillFailResultTo(result, t.getMessage());
        }
      }
    });

    return result;
  }

  public List<DataQueryRequest> convertQueryRequest(DelTagReq req) {

    MonitorScope ms = RequestContext.getContext().ms;
    long start = (req.getStart() == null) ? (System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000)
        : req.getStart();
    long end = (req.getEnd() == null) ? (System.currentTimeMillis()) : req.getEnd();

    List<DataQueryRequest> queryRequests = new ArrayList<>();
    List<QueryDataSource> queryDataSources = new ArrayList<>();

    if (req.isAll()) {
      {
        QueryDataSource queryDataSource = new QueryDataSource();
        {
          queryDataSource.setStart(start);
          queryDataSource.setEnd(end);
          queryDataSource.setMetric(req.getMetric());
        }

        queryDataSources.add(queryDataSource);
      }
    } else if (!CollectionUtils.isEmpty(req.getKeys())) {
      for (Map<String, String> map : req.getKeys()) {
        if (CollectionUtils.isEmpty(map))
          continue;
        QueryDataSource queryDataSource = new QueryDataSource();
        queryDataSource.setStart(start);
        queryDataSource.setEnd(end);
        queryDataSource.setMetric(req.getMetric());
        List<QueryFilter> filters = new ArrayList<>();
        for (Entry<String, String> entry : map.entrySet()) {
          QueryFilter filter = new QueryFilter();
          filter.setName(entry.getKey());
          filter.setValue(entry.getValue());
          filter.setType("literal");
          filters.add(filter);
        }
        queryDataSource.setFilters(filters);
        queryDataSources.add(queryDataSource);
      }
    }

    if (CollectionUtils.isEmpty(queryDataSources)) {
      return new ArrayList<>();
    }

    List<List<QueryDataSource>> lists = UtilMisc.divideList(queryDataSources, 200);
    lists.forEach(list -> {
      if (CollectionUtils.isEmpty(list))
        return;
      DataQueryRequest queryRequest = new DataQueryRequest();
      queryRequest.setTenant(tenantInitService.getTsdbTenant(ms.getTenant()));
      queryRequest.setDatasources(list);
      queryRequests.add(queryRequest);
    });

    return queryRequests;
  }

  public QueryProto.QueryRequest convertRequest(DataQueryRequest request) {
    return convertRequest(request, RequestContext.getContext().ms, RequestContext.getContext().mu);
  }

  public QueryProto.QueryRequest convertRequest(DataQueryRequest request, MonitorScope ms,
      MonitorUser mu) {
    QueryProto.QueryRequest.Builder builder = QueryProto.QueryRequest.newBuilder();
    builder.setTenant(tenantInitService.getTsdbTenant(ms.getTenant()));
    if (StringUtil.isNotBlank(request.getQuery())) {
      builder.setQuery(request.getQuery());
    }
    if (StringUtil.isNotBlank(request.getDownsample())) {
      builder.setDownsample(request.getDownsample());
    }
    if (StringUtil.isNotBlank(request.getFillPolicy())) {
      builder.setFillPolicy(request.getFillPolicy());
    }

    request.datasources.forEach(d -> {
      // Timeline alignment
      if (StringUtils.isNotBlank(d.downsample)) {
        long interval = UtilMisc.getInterval(d.downsample);
        d.end -= d.end % interval;
        d.start = d.start % interval > 0 ? (d.start + interval - d.start % interval) : d.start;
      }
      // wait data collect
      if ((System.currentTimeMillis() - d.end) < 80000L) {
        d.end -= 60000L;
      }
      if ((System.currentTimeMillis() - d.start) < 80000L) {
        d.start -= 60000L;
      }
      d.setQl(null);
      MetricInfo metricInfo = getMetricInfo(d.getMetric());
      if (isCeresdb4Storage(metricInfo)) {
        parseQl(d, metricInfo);
        builder.setTenant(tenantInitService.getTsdbTenant(ms.getTenant(), metricInfo));
      }

      QueryProto.Datasource.Builder datasourceBuilder = QueryProto.Datasource.newBuilder();
      Map<String, Object> objMap = J.toMap(J.toJson(d));
      if (objMap != null) {
        objMap.remove("select");
      }
      toProtoBean(datasourceBuilder, objMap);
      Boolean aBoolean =
          tenantInitService.checkConditions(ms.getTenant(), ms.getWorkspace(), ms.getEnvironment(),
              datasourceBuilder.getMetric(), datasourceBuilder.getFiltersList(), ms, mu);
      if (!aBoolean) {
        throw new MonitorException("workspace is illegal");
      }
      datasourceBuilder
          .setApmMaterialized(d.isApmMaterialized() || MetaDictUtil.isApmMaterialized());
      builder.addDatasources(datasourceBuilder);
    });

    return builder.build();
  }

  private String getTimestampName(QueryDataSource d) {
    String timestampName = "period";
    MetricInfo metricInfo = this.superCacheService.getSc().metricInfoMap.get(d.metric);
    if (metricInfo == null || StringUtils.isEmpty(metricInfo.getExtInfo())) {
      return timestampName;
    }
    Map<String, Object> metricInfoExtInfo = J.toMap(metricInfo.getExtInfo());
    Map<String, Object> tableSchema = (Map<String, Object>) metricInfoExtInfo.get("tableSchema");
    if (CollectionUtils.isEmpty(tableSchema)) {
      return timestampName;
    }
    List<Map<String, Object>> rowSchemaList =
        (List<Map<String, Object>>) tableSchema.get("rowSchemaList");
    if (CollectionUtils.isEmpty(rowSchemaList)) {
      return timestampName;
    }
    for (Map<String, Object> rowSchema : rowSchemaList) {
      String type = (String) rowSchema.get("type");
      if (StringUtils.equals("timestamp", type)) {
        return (String) rowSchema.get("name");
      }
    }
    return timestampName;
  }

  protected void parseQl(QueryDataSource d, MetricInfo metricInfo) {
    String timestampName = getTimestampName(d);
    Set<String> tags = new HashSet<>(J.toList(metricInfo.getTags()));
    StringBuilder select = new StringBuilder("select ");
    List<String> gbList = parseGroupby(d, tags, timestampName);
    List<String> selects = new ArrayList<>();
    String downsample = getPeriodOfTime(d.getDownsample());

    selects.add("time_bucket(`" + timestampName + "`, '" + downsample
        + "', 'yyyy-MM-dd HH:mm:ss', '+0800') AS `timestamp`");
    for (String gb : gbList) {
      if (!tags.contains(gb)) {
        continue;
      }
      selects.add("`" + gb + "`");
    }

    parseSelect(selects, select, d, tags);

    select.append(" from ").append(d.metric);

    parseFilters(select, d, tags, timestampName);

    String groupBy = "";
    if (!CollectionUtils.isEmpty(gbList)) {
      groupBy = "`" + String.join("` , `", gbList) + "`";
    }

    select.append(" group by ") //
        .append(groupBy) //
        .append(StringUtils.isEmpty(groupBy) ? "" : ", ") //
        .append(" time_bucket(`") //
        .append(timestampName) //
        .append("`, '") //
        .append(downsample) //
        .append("', 'yyyy-MM-dd HH:mm:ss', '+0800') ");

    select.append(" order by `timestamp` asc");

    log.info("parse sql {}", select);
    d.setQl(select.toString());
  }

  private String getPeriodOfTime(String downsample) {
    if (StringUtils.isBlank(downsample)) {
      return "PT1M";
    }
    switch (downsample) {
      case "1m":
        return "PT1M";
      case "5m":
        return "PT5M";
      case "30m":
        return "PT30M";
      case "1h":
        return "PT1H";
      default:
        return "PT1M";
    }
  }

  private List<String> parseGroupby(QueryDataSource d, Set<String> tags, String timestampName) {
    List<String> gbList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(d.groupBy)) {
      for (String gb : d.groupBy) {
        if (!StringUtils.equals(timestampName, gb) && !tags.contains(gb)) {
          continue;
        }
        gbList.add(gb);
      }
    }
    return gbList;
  }

  private void parseSelect(List<String> selects, StringBuilder select, QueryDataSource d,
      Set<String> tags) {
    if (CollectionUtils.isEmpty(d.select)) {
      selects.add(" count(1) as value ");
    } else {
      for (Entry<String /* column name */, String /* expression */> entry : d.select.entrySet()) {
        String columnName = entry.getKey();
        String expression = entry.getValue();
        if (!tags.contains(columnName)) {
          continue;
        }
        if (expression.equals("approx_distinct")) {
          selects.add(" approx_distinct(`" + columnName + "`) as value ");
        } else {
          selects.add(" count(1) as value ");
        }
      }
    }

    select.append(String.join(" , ", selects));
  }

  private void parseFilters(StringBuilder select, QueryDataSource d, Set<String> tags,
      String timestampName) {
    select.append(" where `") //
        .append(timestampName) //
        .append("` <= ") //
        .append(d.end) //
        .append(" and `") //
        .append(timestampName) //
        .append("` >= ") //
        .append(d.start);
    if (!CollectionUtils.isEmpty(d.filters)) {
      for (QueryFilter filter : d.filters) {
        if (!tags.contains(filter.name)) {
          continue;
        }
        switch (filter.type) {
          case "literal_or":
            select.append(" and `").append(filter.name).append("` in ('")
                .append(String.join("','", Arrays.asList(filter.value.split("\\|")))).append("')");
            break;
          case "not_literal_or":
            select.append(" and ").append(filter.name).append(" not in ('")
                .append(String.join("','", Arrays.asList(filter.value.split("\\|")))).append("')");
            break;
          case "wildcard":
            select.append(" and `").append(filter.name).append("` like '").append(filter.value)
                .append("'");
            break;
          case "regexp":
            select.append(" and `").append(filter.name).append("` REGEXP '").append(filter.value)
                .append("'");
            break;
          case "not_regexp_match":
            select.append(" and `").append(filter.name).append("` NOT REGEXP '")
                .append(filter.value).append("'");
            break;
          case "literal":
            select.append(" and `").append(filter.name).append("` = '").append(filter.value)
                .append("'");
            break;
          case "not_literal":
            select.append(" and `").append(filter.name).append("` <> '").append(filter.value)
                .append("'");
            break;
        }
      }
    }
  }

  private MetricInfo getMetricInfo(String metric) {
    if (CollectionUtils.isEmpty(this.superCacheService.getSc().metricInfoMap)
        || StringUtils.isEmpty(metric)) {
      return null;
    }
    return this.superCacheService.getSc().metricInfoMap.get(metric);
  }

  private boolean isCeresdb4Storage(MetricInfo metricInfo) {
    if (metricInfo == null) {
      return false;
    }
    if (StringUtils.isNotEmpty(metricInfo.getStorageTenant())
        && StringUtils.equals(metricInfo.getStorageTenant(), "ceresdb4")) {
      return true;
    }
    return false;
  }


  public static void toProtoBean(Message.Builder destPojoClass, Object source) {
    String json = J.toJson(source);
    try {
      JsonFormat.parser().merge(json, destPojoClass);
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException("InvalidProtocolBufferException error", e);
    }
  }

  @PostMapping(value = "/pql/parse")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<PqlParseResult> pqlParse(@RequestBody PqlParseRequest request) {
    final JsonResult<PqlParseResult> result = new JsonResult<>();
    PqlParseResult pqlParseResult = new PqlParseResult();
    pqlParseResult.setRawPql(request.getPql());
    try {
      List<String> exprs = pqlParser.parseList(request.getPql());
      if (exprs != null && !exprs.isEmpty()) {
        pqlParseResult.setExprs(exprs);
        JsonResult.createSuccessResult(result, pqlParseResult);
      } else {
        JsonResult.fillFailResultTo(result, "parse failed or pql is empty");
      }
    } catch (PqlException e) {
      JsonResult.fillFailResultTo(result, e.getMessage());
    }
    return result;
  }
}
