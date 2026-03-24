/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.model.DataQueryRequest;
import io.holoinsight.server.common.service.MetricInfoService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

/**
 * @author masaimu
 * @version 2023-08-10 14:56:00
 */
public class QueryFacadeImplTest {

  @InjectMocks
  QueryFacadeImpl queryFacade;

  @Mock
  MetricInfoService metricInfoService;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Make getTimestampName() return the default "period"
    Mockito.when(metricInfoService.queryByMetric(Mockito.anyString())).thenReturn(null);
  }

  @Test
  public void testParseQl_noSelect() {
    String json =
        "{\"start\":1691644303000,\"end\":1691647903000,\"name\":\"a\",\"metric\":\"k8s_pod_mem_util\","
            + "\"groupBy\":[\"app\",\"hostname\",\"workspace\",\"pod\",\"ip\",\"namespace\"],"
            + "\"filters\":[{\"type\":\"literal_or\",\"name\":\"app\",\"value\":\"holoinsight-server|aaaaa\"},"
            + "{\"type\":\"literal\",\"name\":\"应用ID\",\"value\":\"111111111\"},"
            + "{\"type\":\"literal\",\"name\":\"fake应用ID\",\"value\":\"22222222\"}]}";
    DataQueryRequest.QueryDataSource queryDataSource =
        J.fromJson(json, DataQueryRequest.QueryDataSource.class);
    MetricInfoDTO metricInfo = new MetricInfoDTO();
    metricInfo.setTags(Arrays.asList("app", "应用ID"));

    queryFacade.parseQl(queryDataSource, metricInfo);

    Assert.assertNotNull(queryDataSource.ql);
    // Verify key SQL fragments rather than exact string to avoid fragile assertions
    Assert.assertTrue("Should contain time_bucket",
        queryDataSource.ql.contains("time_bucket(`period`"));
    Assert.assertTrue("Should contain metric table",
        queryDataSource.ql.contains("from k8s_pod_mem_util"));
    Assert.assertTrue("Should filter by app",
        queryDataSource.ql.contains("`app` in ('holoinsight-server','aaaaa')"));
    Assert.assertTrue("Should filter by valid tag",
        queryDataSource.ql.contains("`应用ID` = '111111111'"));
    // fake应用ID is not in tags, should be excluded
    Assert.assertFalse("Should not contain invalid tag filter",
        queryDataSource.ql.contains("fake应用ID"));
    Assert.assertTrue("Should contain count aggregation",
        queryDataSource.ql.contains("count(1) as value"));
    Assert.assertTrue("Should order by timestamp",
        queryDataSource.ql.contains("order by `timestamp` asc"));
  }

  @Test
  public void testParseQl_withApproxDistinctSelect() {
    String json =
        "{\"start\":1691644303000,\"end\":1691647903000,\"name\":\"a\",\"metric\":\"k8s_pod_mem_util\","
            + "\"groupBy\":[\"app\"],"
            + "\"filters\":[{\"type\":\"literal\",\"name\":\"app\",\"value\":\"test-app\"}],"
            + "\"select\":{\"app\":\"approx_distinct\"}}";
    DataQueryRequest.QueryDataSource queryDataSource =
        J.fromJson(json, DataQueryRequest.QueryDataSource.class);
    MetricInfoDTO metricInfo = new MetricInfoDTO();
    metricInfo.setTags(Arrays.asList("app"));

    queryFacade.parseQl(queryDataSource, metricInfo);

    Assert.assertNotNull(queryDataSource.ql);
    Assert.assertTrue("Should contain approx_distinct",
        queryDataSource.ql.contains("approx_distinct(`app`) as value"));
    Assert.assertTrue("Should contain metric table",
        queryDataSource.ql.contains("from k8s_pod_mem_util"));
  }
}
