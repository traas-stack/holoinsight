/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.model.DataQueryRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author masaimu
 * @version 2023-08-10 14:56:00
 */
public class QueryFacadeImplTest {

  @Test
  public void name() {
    String json =
        "{\"start\":1691644303000,\"end\":1691647903000,\"name\":\"a\",\"metric\":\"k8s_pod_mem_util\",\"groupBy\":[\"app\",\"hostname\",\"workspace\",\"pod\",\"ip\",\"namespace\"],\"filters\":[{\"type\":\"literal_or\",\"name\":\"app\",\"value\":\"holoinsight-server|aaaaa\"},{\"type\":\"literal\",\"name\":\"应用ID\",\"value\":\"111111111\"},{\"type\":\"literal\",\"name\":\"fake应用ID\",\"value\":\"22222222\"}]}";
    DataQueryRequest.QueryDataSource queryDataSource =
        J.fromJson(json, DataQueryRequest.QueryDataSource.class);
    MetricInfo metricInfo = new MetricInfo();
    metricInfo.setTags(J.toJson(Arrays.asList("app", "应用ID")));
    QueryFacadeImpl queryFacade = new QueryFacadeImpl();
    queryFacade.parseQl(queryDataSource, metricInfo);
    System.out.println(queryDataSource.ql);
    Assert.assertTrue(StringUtils.equals(
        "select `period` ,  count(1) as value  from k8s_pod_mem_util where `period` <= 1691647903000 and `period` >= 1691644303000 and `app` in ('holoinsight-server','aaaaa') and `应用ID` = '111111111' group by `period` , `app` order by `period` asc",
        queryDataSource.ql));

    json =
        "{\"start\":1691644303000,\"end\":1691647903000,\"name\":\"a\",\"metric\":\"k8s_pod_mem_util\",\"groupBy\":[\"app\",\"hostname\",\"workspace\",\"pod\",\"ip\",\"namespace\"],\"filters\":[{\"type\":\"literal_or\",\"name\":\"app\",\"value\":\"holoinsight-server|aaaaa\"},{\"type\":\"literal\",\"name\":\"应用ID\",\"value\":\"111111111\"},{\"type\":\"literal\",\"name\":\"fake应用ID\",\"value\":\"22222222\"}],\"select\":{\"app\":null,\"dd\":\"distinct(`aaa`)\"}}";
    queryDataSource = J.fromJson(json, DataQueryRequest.QueryDataSource.class);
    metricInfo = new MetricInfo();
    metricInfo.setTags(J.toJson(Arrays.asList("app", "应用ID")));
    queryFacade = new QueryFacadeImpl();
    queryFacade.parseQl(queryDataSource, metricInfo);
    System.out.println(queryDataSource.ql);
    Assert.assertTrue(StringUtils.equals(
        "select `period` , `app` , distinct(`aaa`) as dd from k8s_pod_mem_util where `period` <= 1691647903000 and `period` >= 1691644303000 and `app` in ('holoinsight-server','aaaaa') and `应用ID` = '111111111' group by `period` , `app` order by `period` asc",
        queryDataSource.ql));

    System.out.println(queryDataSource.ql);
    Assert.assertTrue(StringUtils.equals(
        "select `app` , distinct(`aaa`) as dd from k8s_pod_mem_util where `period` <= 1691647903000 and `period` >= 1691644303000 and `app` in ('holoinsight-server','aaaaa') and `应用ID` = '111111111' group by `app`",
        queryDataSource.ql));

    json =
        "{\"start\":1691644303000,\"end\":1691647903000,\"name\":\"a\",\"metric\":\"k8s_pod_mem_util\",\"groupBy\":[\"app\",\"hostname\",\"workspace\",\"pod\",\"ip\",\"namespace\"],\"filters\":[{\"type\":\"literal_or\",\"name\":\"app\",\"value\":\"holoinsight-server|aaaaa\"},{\"type\":\"literal\",\"name\":\"应用ID\",\"value\":\"111111111\"},{\"type\":\"literal\",\"name\":\"fake应用ID\",\"value\":\"22222222\"}],\"select\":{\"app\":null,\"dd\":\"distinct(`aaa`)\", \"period\":null}}";
    queryDataSource = J.fromJson(json, DataQueryRequest.QueryDataSource.class);
    metricInfo = new MetricInfo();
    metricInfo.setTags(J.toJson(Arrays.asList("app", "应用ID")));
    queryFacade = new QueryFacadeImpl();
    System.out.println(queryDataSource.ql);
    Assert.assertTrue(StringUtils.equals(
        "select `app` , distinct(`aaa`) as dd , `period` from k8s_pod_mem_util where `period` <= 1691647903000 and `period` >= 1691644303000 and `app` in ('holoinsight-server','aaaaa') and `应用ID` = '111111111' group by `app` order by `period` desc",
        queryDataSource.ql));
  }
}
