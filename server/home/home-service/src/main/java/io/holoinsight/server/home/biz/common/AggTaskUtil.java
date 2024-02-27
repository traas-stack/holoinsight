/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.common;

import io.holoinsight.server.agg.v1.core.conf.AggFunc;
import io.holoinsight.server.agg.v1.core.conf.CompletenessConfig;
import io.holoinsight.server.agg.v1.core.conf.CompletenessConfig.Mode;
import io.holoinsight.server.agg.v1.core.conf.FillZero;
import io.holoinsight.server.agg.v1.core.conf.From;
import io.holoinsight.server.agg.v1.core.conf.FromConfigs;
import io.holoinsight.server.agg.v1.core.conf.FromMetrics;
import io.holoinsight.server.agg.v1.core.conf.GroupBy;
import io.holoinsight.server.agg.v1.core.conf.GroupByItem;
import io.holoinsight.server.agg.v1.core.conf.Output;
import io.holoinsight.server.agg.v1.core.conf.OutputField;
import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.conf.PartitionKey;
import io.holoinsight.server.agg.v1.core.conf.Select;
import io.holoinsight.server.agg.v1.core.conf.SelectItem;
import io.holoinsight.server.agg.v1.core.conf.SelectItem.Elect;
import io.holoinsight.server.agg.v1.core.conf.Where;
import io.holoinsight.server.agg.v1.core.conf.Window;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.Metric;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: AggTaskUtil.java, Date: 2023-12-06 Time: 14:45
 */
public class AggTaskUtil {

  private static Integer groupByKeyLimit = 100000;
  private static String OUTPUT_STORAGE_ENGINE = "TSDB";

  public static Select buildSelect(CollectMetric collectMetric) {

    List<Metric> metrics = collectMetric.getMetrics();

    Select select = new Select();
    List<SelectItem> items = new ArrayList<>();
    metrics.forEach(m -> {
      SelectItem selectItem = new SelectItem();

      Elect elect = new Elect();
      elect.setMetric(collectMetric.getTargetTable());
      elect.setField("value");
      selectItem.setElect(elect);

      AggFunc aggFunc = new AggFunc();
      aggFunc.setType(m.getFunc().toUpperCase());
      if (m.func.equalsIgnoreCase("count")) {
        aggFunc.setType("SUM");
      }

      selectItem.setAgg(aggFunc);

      selectItem.setAs(collectMetric.getTargetTable());
      items.add(selectItem);
    });

    select.setItems(items);
    return select;
  }


  public static From buildFrom(String tableName, CollectMetric collectMetric,
      List<String> groupTags) {

    From from = new From();
    from.setType("metrics");
    FromMetrics fromMetrics = new FromMetrics();
    Set<String> metrics = new HashSet<>();
    metrics.add(collectMetric.getTargetTable());
    fromMetrics.setMetrics(metrics);
    from.setMetrics(fromMetrics);

    FromConfigs fromConfigs = new FromConfigs();
    Set<String> tableNames = new HashSet<>();
    tableNames.add(tableName);
    fromConfigs.setTableNames(tableNames);
    from.setConfigs(fromConfigs);

    CompletenessConfig completenessConfig = new CompletenessConfig();
    completenessConfig.setMode(Mode.COMPLETENESS_INFO);

    List<GroupByItem> groupByItems = new ArrayList<>();
    groupTags.forEach(tag -> {
      GroupByItem groupByItem = new GroupByItem();
      groupByItem.setTag(tag);
      groupByItem.setAs(tag);
      groupByItems.add(groupByItem);
    });
    GroupBy groupBy = new GroupBy();
    groupBy.setItems(groupByItems);
    completenessConfig.setGroupBy(groupBy);
    from.setCompleteness(completenessConfig);
    return from;
  }

  public static Where buildWhere(CollectMetric collectMetric) {
    Where where = new Where();

    return where;
  }

  public static GroupBy buildGroupBy(CollectMetric collectMetric, List<String> defaultGroupByTags) {
    GroupBy groupBy = new GroupBy();
    groupBy.setKeyLimit(groupByKeyLimit);

    List<GroupByItem> groupByItems = new ArrayList<>();
    List<String> tags = collectMetric.getTags();
    if (!CollectionUtils.isEmpty(defaultGroupByTags)) {
      defaultGroupByTags.forEach(tag -> {
        GroupByItem groupByItem = new GroupByItem();
        groupByItem.setTag(tag);
        groupByItems.add(groupByItem);
      });
    }
    if (!CollectionUtils.isEmpty(tags)) {
      tags.forEach(tag -> {
        GroupByItem groupByItem = new GroupByItem();
        groupByItem.setTag(tag);
        groupByItems.add(groupByItem);
      });
    }
    groupBy.setItems(groupByItems);
    return groupBy;
  }

  public static Window buildWindow(long period) {
    Window window = new Window();
    window.setInterval(period);
    return window;
  }

  public static Output buildOutput(CollectMetric collectMetric) {
    Output output = new Output();

    OutputItem outputItem = new OutputItem();
    outputItem.setType(OUTPUT_STORAGE_ENGINE);
    outputItem.setName(collectMetric.getLogCalculate().getAggTableName());

    List<OutputField> fields = new ArrayList<>();
    OutputField outputField = new OutputField();
    outputField.setName(collectMetric.getTargetTable());
    outputField.setExpression(collectMetric.getTargetTable());
    fields.add(outputField);
    outputItem.setFields(fields);

    if (null != collectMetric.getLogCalculate().getTopn()
        && collectMetric.getLogCalculate().getTopn().isEnabled()) {
      outputItem.setTopn(collectMetric.getLogCalculate().getTopn());
    }

    List<OutputItem> items = new ArrayList<>();
    items.add(outputItem);

    output.setItems(items);
    return output;
  }

  public static List<PartitionKey> buildPartition(CollectMetric collectMetric) {
    return new ArrayList<>();
  }

  public static FillZero buildFillZero(CollectMetric collectMetric) {
    if (null != collectMetric.getLogCalculate().getFillZero()
        && collectMetric.getLogCalculate().getFillZero().isEnabled()) {
      return collectMetric.getLogCalculate().getFillZero();
    }
    return new FillZero();
  }
}
