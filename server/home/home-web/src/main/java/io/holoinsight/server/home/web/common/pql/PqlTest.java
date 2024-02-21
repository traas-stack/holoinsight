/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.web.common.pql.expr.Expr;

import java.util.*;

/**
 * @author zzhb101
 * @time 2023-01-25 11:54 上午
 */
public class PqlTest {

  static List<String> pqls = Arrays.asList(

      // 简单时间查询
      "prometheus_http_requests_total", "prometheus_http_requests_total{job=\"prometheus\"}",
      "prometheus_http_requests_total{job=\"prometheus\"}[2m]",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m",
      "prometheus_http_requests_total{job=\"prometheus\"}[2m] offset 1m",

      // 运算
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m /1024",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m *1024",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m +1024",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m -1024",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m %1024",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m ^2",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m + prometheus_http_requests_total{job=\"prometheus\"}",

      // 过滤比较
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m -1024>1",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m -1024==1",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m -1024!=1",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m <1",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m <=1",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m >=1",
      "prometheus_http_requests_total{job=\"prometheus\"} offset 1m >= bool 1",

      // 逻辑运算
      "prometheus_http_requests_total{job=\"prometheus\"} or prometheus_http_requests_total{job=\"prometheus1\"}",
      "container_memory_usage_bytes{id=\"/kubepods.slice/kubepods-burstable.slice/kubepods-burstable-pod22f01897_042a_11ea_91cd_005056967167.slice/docker-74b609f325ecf8262983d21f02446d58c75bfb4dca73c5f7317c3954c5fc4954.scope\"} and container_memory_max_usage_bytes{pod=\"busybox-56546db479-hdf65\"}",
      "container_memory_max_usage_bytes{pod=\"busybox-56546db479-hdf65\"} unless container_memory_usage_bytes{id=\"/kubepods.slice/kubepods-burstable.slice/kubepods-burstable-pod22f01897_042a_11ea_91cd_005056967167.slice/docker-74b609f325ecf8262983d21f02446d58c75bfb4dca73c5f7317c3954c5fc4954.scope\"}",

      // 复合运算
      "prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"} / ignoring(rate) prometheus_http_requests_total",
      "prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"} / ignoring(rate)  group_left prometheus_http_requests_total",
      "prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"} / ignoring(rate)  group_right prometheus_http_requests_total",

      // 聚合运算
      "sum(prometheus_http_requests_total) by (job)",
      "min(prometheus_http_requests_total) by (job)",
      "avg(prometheus_http_requests_total) by (job)",
      "max(prometheus_http_requests_total) by (job)",
      "count(prometheus_http_requests_total) by (job1,job2)",

      // 方差算法
      "stddev(container_memory_usage_bytes{node=\"ingressnode01\",pod=\"node-exporter-kf2cr\"}/1024/1024 )",
      "stdvar(container_memory_usage_bytes{node=\"ingressnode01\",pod=\"node-exporter-kf2cr\"}/1024/1024 )",
      "count_values (\"newlabel\",kube_deployment_created)",
      "bottomk(2, prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"}/2)",
      "topk(2, prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"}/2)",
      "quantile(0.25,prometheus_http_requests_total{rate=\"test\"})",

      // 函数
      "abs(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      "absent(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      "ceil(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      "floor(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      "sqrt(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      "exp(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      // "resets(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"}[5m])?",
      "changes(prometheus_http_requests_total{job=\"prometheus\",rate=\"test\"})",
      "clamp_min(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"},1)",
      "delta(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}-mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"})",
      "idelta(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}-mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"})",
      "increase(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}[5m])",
      "irate(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}[5m])",
      "rate(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}[5m])",
      "sort(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}[5m])",
      "sort_desc(mysql.innodb.rollback.on.timeout_chris{appId=\"npcs_pubs_cpsgw\"}[5m])",

      // 多指标查询
      "{__name__=~\"cpu|memory\"}", "prometheus_http_requests_total{job=~\"pro.*\"}");

  public static void main(String[] args) {
    for (String pql : pqls) {
      // System.out.println(pql);
      Parser p = new Parser();
      p.initLexer(pql);
      Expr e = null;
      try {
        e = p.parse(pql);
      } catch (PqlException ex) {
        ex.printStackTrace();
      }
      // System.out.println(e.explain());
      // System.out.println(J.toJson(e.explainToList()));
      // System.out.println("\n");
    }
  }
}
