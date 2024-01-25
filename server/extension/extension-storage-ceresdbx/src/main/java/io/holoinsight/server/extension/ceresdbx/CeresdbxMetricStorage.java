/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.ceresdb.common.parser.SqlParser;
import io.ceresdb.common.parser.SqlParserFactoryProvider;
import io.holoinsight.server.common.J;
import io.holoinsight.server.extension.MetricMeterService;
import io.holoinsight.server.extension.model.DetailResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xzchaoo.commons.stat.StringsKey;

import io.ceresdb.CeresDBClient;
import io.ceresdb.models.Err;
import io.ceresdb.models.Point.PointBuilder;
import io.ceresdb.models.Row;
import io.ceresdb.models.Row.Column;
import io.ceresdb.models.SqlQueryOk;
import io.ceresdb.models.SqlQueryRequest;
import io.ceresdb.models.Value;
import io.ceresdb.models.WriteOk;
import io.ceresdb.models.WriteRequest;
import io.grpc.Context;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.ceresdbx.utils.StatUtils;
import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryMetricsParam;
import io.holoinsight.server.extension.model.QueryParam;
import io.holoinsight.server.extension.model.QueryParam.QueryFilter;
import io.holoinsight.server.extension.model.QueryParam.SlidingWindow;
import io.holoinsight.server.extension.model.QueryResult;
import io.holoinsight.server.extension.model.QueryResult.Result;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import io.holoinsight.server.extension.model.WriteMetricsParam.Point;
import io.holoinsight.server.extension.promql.PqlQueryService;
import reactor.core.publisher.Mono;

/**
 * @author jiwliu
 * @date 2023/1/13
 */
@Slf4j
public class CeresdbxMetricStorage implements MetricStorage {

  public static final int DEFAULT_BATCH_RECORDS = 200;
  public static final int DEFAULT_BATCH_POINTS = 512;
  public static final int DEFAULT_METRIC_LIMIT = 100;
  private static final Logger LOGGER = LoggerFactory.getLogger(CeresdbxMetricStorage.class);
  private static final List<String> SUPPORT_SMH_UNIT = Lists.newArrayList("s", "m", "h");
  private static final List<String> SUPPORT_DMY_UNIT = Lists.newArrayList("d", "M", "y");
  CeresdbxClientManager ceresdbxClientManager;

  private PqlQueryService pqlQueryService;

  @Autowired(required = false)
  private MetricMeterService metricMeterService;

  public CeresdbxMetricStorage(CeresdbxClientManager ceresdbxClientManager) {
    this.ceresdbxClientManager = ceresdbxClientManager;
  }

  public CeresdbxMetricStorage(CeresdbxClientManager ceresdbxClientManager,
      PqlQueryService pqlQueryService) {
    this.ceresdbxClientManager = ceresdbxClientManager;
    this.pqlQueryService = pqlQueryService;
  }

  @Override
  public Mono<Void> write(WriteMetricsParam writeMetricsParam) {
    List<Point> points = writeMetricsParam.getPoints();
    if (CollectionUtils.isEmpty(points)) {
      return Mono.empty();
    }
    String tenant = writeMetricsParam.getTenant();
    Iterator<Point> pointIter = points.iterator();
    List<io.ceresdb.models.Point> oneBatch = Lists.newLinkedList();
    int dps = 0;
    Set<String> metrics = Sets.newHashSet();
    while (pointIter.hasNext()) {
      Point point = pointIter.next();
      dps += convertToRows(metrics, point, oneBatch);
      if (!pointIter.hasNext() || oneBatch.size() >= DEFAULT_BATCH_RECORDS
          || dps >= DEFAULT_BATCH_POINTS) {
        List<io.ceresdb.models.Point> oneBatch2 = oneBatch;
        Context.ROOT.run(() -> doBatchInsert(metrics, tenant, oneBatch2));
        oneBatch = Lists.newLinkedList();
        dps = 0;
      }
    }
    if (metricMeterService != null && !writeMetricsParam.isFree()) {
      metricMeterService.meter(writeMetricsParam);
    }
    return Mono.empty();
  }

  @Override
  public List<Result> queryData(QueryParam queryParam) {
    String sql = queryParam.getQl();
    String[] tableNames;
    if (StringUtils.isNotBlank(sql)) {
      SqlParser parser = SqlParserFactoryProvider.getSqlParserFactory().getParser(sql);
      List<String> tables = parser.tableNames();
      tableNames = tables.stream().map(this::fixName).collect(Collectors.toList())
          .toArray(new String[tables.size()]);
      sql = StringUtils.replaceEach(sql, tables.toArray(new String[tables.size()]), tableNames);
    } else {
      String whereStatement = parseWhere(queryParam);
      String fromStatement = parseFrom(queryParam);
      if (StringUtils.isBlank(fromStatement)) {
        LOGGER.warn("fromStatement is empty, queryParam:{}", queryParam);
        return Lists.newArrayList();
      }
      tableNames = new String[] {fixName(queryParam.getMetric())};
      sql = genSqlWithGroupBy(queryParam, fromStatement, whereStatement);

    }
    LOGGER.info("queryData queryparam:{}, sql:{}", queryParam, sql);
    final SqlQueryRequest queryRequest =
        SqlQueryRequest.newBuilder().forTables(tableNames).sql(sql).build();
    long begin = System.currentTimeMillis();
    try {
      CompletableFuture<io.ceresdb.models.Result<SqlQueryOk, Err>> qf = Context.ROOT.call(
          () -> ceresdbxClientManager.getClient(queryParam.getTenant()).sqlQuery(queryRequest));
      final io.ceresdb.models.Result<SqlQueryOk, Err> qr = qf.get();
      if (!qr.isOk()) {
        LOGGER.error("[CERESDBX_QUERY] failed to exec sql:{}, qr:{}, error:{}", sql, qr,
            qr.getErr().getError());
        throw new RuntimeException(qr.getErr().getError());
      }
      final List<Row> rows = qr.getOk().getRowList();
      if (CollectionUtils.isEmpty(rows)) {
        return Lists.newArrayList();
      }
      String[] header = getHeader(rows.get(0));
      return transToResults(queryParam, header, rows);
    } catch (Exception e) {
      LOGGER.error("[CERESDBX_QUERY] failed to exec sql:{}, cost:{}", sql,
          System.currentTimeMillis() - begin, e);
      return Lists.newArrayList();
    }
  }

  @Override
  public List<Result> queryTags(QueryParam queryParam) {
    List<Result> results = queryData(queryParam);
    return results;
  }

  @Override
  public void deleteKeys(QueryParam queryParam) {}

  @Override
  public Result querySchema(QueryParam queryParam) {
    String metric = fixName(queryParam.getMetric());
    SqlQueryOk sqlQueryOk = execSQL(queryParam.getTenant(), "DESCRIBE " + metric);
    Result result = new Result();
    result.setMetric(metric);
    List<Row> rows = sqlQueryOk.getRowList();
    if (CollectionUtils.isEmpty(rows)) {
      result.setTags(Maps.newHashMap());
      return result;
    }
    Map<Column, Boolean> colIsTag =
        rows.stream().collect(Collectors.toMap(row -> row.getColumn("name"),
            row -> row.getColumn("is_tag").getValue().getBoolean()));
    Map<String, String> fields = colIsTag.entrySet().stream().filter(Entry::getValue)
        .map(entry -> entry.getKey().getValue().getString())
        .collect(Collectors.toMap(Function.identity(), (v) -> "-"));
    result.setTags(fields);
    return result;
  }

  @Override
  public List<String> queryMetrics(QueryMetricsParam queryParam) {
    SqlQueryOk sqlResult =
        execSQL(queryParam.getTenant(), "show tables like '" + queryParam.getName() + "%%'");
    List<Row> rows = sqlResult.getRowList();
    List<String> result = Lists.newArrayList();
    if (CollectionUtils.isEmpty(rows)) {
      return result;
    }
    int limit = queryParam.getLimit() > 0 ? queryParam.getLimit() : DEFAULT_METRIC_LIMIT;
    return rows.stream().limit(limit).flatMap(row -> row.getColumns().stream())
        .filter(row -> "Tables".equalsIgnoreCase(row.getName()))
        .map(column -> column.getValue().getString()).collect(Collectors.toList());
  }

  @Override
  public List<Result> pqlInstantQuery(PqlParam pqlParam) {
    if (Objects.isNull(pqlQueryService)) {
      LOGGER.warn("[CeresDB] pqlQueryService is null");
      return Lists.newArrayList();
    }
    return pqlQueryService.query(pqlParam);
  }

  @Override
  public List<Result> pqlRangeQuery(PqlParam pqlParam) {
    if (Objects.isNull(pqlQueryService)) {
      LOGGER.warn("[CeresDB] pqlQueryService is null");
      return Lists.newArrayList();
    }
    return pqlQueryService.queryRange(pqlParam);
  }

  @Override
  public DetailResult queryDetail(QueryParam queryParam) {
    String sql = queryParam.getQl();
    List<String> tables = null;
    if (StringUtils.isNotBlank(sql)) {
      SqlParser parser = SqlParserFactoryProvider.getSqlParserFactory().getParser(sql);
      tables = parser.tableNames();
    } else {
      log.warn("sql is empty for {}", J.toJson(queryParam));
      return DetailResult.empty();
    }
    if (CollectionUtils.isEmpty(tables)) {
      log.warn("tables is empty for {}", J.toJson(queryParam));
      return DetailResult.empty();
    }
    LOGGER.info("queryDetail queryParam:{}, sql:{}", J.toJson(queryParam), sql);
    final SqlQueryRequest queryRequest =
        SqlQueryRequest.newBuilder().forTables(tables.toArray(new String[0])).sql(sql).build();
    long begin = System.currentTimeMillis();
    try {
      CompletableFuture<io.ceresdb.models.Result<SqlQueryOk, Err>> qf = Context.ROOT.call(
          () -> ceresdbxClientManager.getClient(queryParam.getTenant()).sqlQuery(queryRequest));
      final io.ceresdb.models.Result<SqlQueryOk, Err> qr = qf.get();
      if (!qr.isOk()) {
        LOGGER.error("[CERESDBX_QUERY_DETAIL] failed to exec sql:{}, qr:{}, error:{}", sql, qr,
            qr.getErr().getError());
        throw new RuntimeException(qr.getErr().getError());
      }
      final List<Row> rows = qr.getOk().getRowList();
      if (CollectionUtils.isEmpty(rows)) {
        return DetailResult.empty();
      }
      String[] header = getHeader(rows.get(0));
      return transToDetailResult(queryParam, header, rows, tables);
    } catch (Exception e) {
      LOGGER.error("[CERESDBX_QUERY_DETAIL] failed to exec sql:{}, cost:{}", sql,
          System.currentTimeMillis() - begin, e);
      return DetailResult.empty();
    }
  }

  private DetailResult transToDetailResult(QueryParam queryParam, String[] header, List<Row> rows,
      List<String> tables) {
    DetailResult detailResult = DetailResult.empty();
    if (header == null || header.length == 0) {
      return detailResult;
    }
    detailResult.setHeaders(Arrays.asList(header));
    detailResult.setSql(queryParam.getQl());
    detailResult.setTables(tables);
    for (Row row : rows) {
      DetailResult.DetailRow detailRow = new DetailResult.DetailRow();
      for (String name : header) {
        Column column = row.getColumn(name);
        Value value = column.getValue();
        switch (value.getDataType()) {
          case String:
            detailRow.addStringValue(value.getString());
            break;
          case Timestamp:
            detailRow.addTimestampValue(value.getTimestamp());
            break;
          case Boolean:
            detailRow.addBooleanValue(value.getBoolean());
            break;
          case Varbinary:
            log.info("reject unknown data type {}", value.getObject());
            break;
          default:
            detailRow.addNumValue(value.getObject());
            break;
        }
      }
      detailResult.add(detailRow);
    }
    log.info("detailResult result {} rows {}", J.toJson(detailResult), J.toJson(rows));
    return detailResult;
  }

  private void doBatchInsert(Set<String> metrics, String tenant,
      List<io.ceresdb.models.Point> oneBatch) {
    long start = System.currentTimeMillis();
    CeresDBClient client = ceresdbxClientManager.getClient(tenant);
    try {
      CompletableFuture<io.ceresdb.models.Result<WriteOk, Err>> future =
          client.write(new WriteRequest(oneBatch));
      future.whenComplete((result, throwable) -> {
        if (result != null && result.isOk()) {
          StatUtils.STORAGE_WRITE.add(StringsKey.of("CeresDBx", tenant, "Y"),
              new long[] {1, oneBatch.size(), System.currentTimeMillis() - start});
        } else {
          StatUtils.STORAGE_WRITE.add(StringsKey.of("CeresDBx", tenant, "N"),
              new long[] {1, oneBatch.size(), System.currentTimeMillis() - start});
          if (result != null && null != result.getErr()) {
            LOGGER.error("save metrics:{} to CeresDBx error msg:{}", metrics,
                result.getErr().getError());
          } else if (null != throwable) {
            LOGGER.error("save metrics:{} to CeresDBx error msg:{}", metrics,
                throwable.getMessage(), throwable);
          } else {
            LOGGER.error("save metrics:{} to CeresDBx error", metrics);
          }
        }
      });
    } catch (Exception e) {
      StatUtils.STORAGE_WRITE.add(StringsKey.of("CeresDBx", tenant, "N"),
          new long[] {1, oneBatch.size(), System.currentTimeMillis() - start});
      LOGGER.error("save metrics:{} to CeresDBx error, msg:{}", metrics, e.getMessage(), e);
    }

  }

  private int convertToRows(Set<String> metrics, Point point,
      List<io.ceresdb.models.Point> oneBatch) {
    Map<String, String> tags = point.getTags();
    String metric = fixName(point.getMetricName());
    metrics.add(metric);
    final PointBuilder builder =
        io.ceresdb.models.Point.newPointBuilder(metric).setTimestamp(point.getTimeStamp());
    tags.forEach(builder::addTag);
    if (point.getStrValue() != null) {
      builder.addField("value", Value.withString(point.getStrValue()));
    } else {
      builder.addField("value", Value.withDouble(point.getValue()));
    }
    oneBatch.add(builder.build());
    return tags.size();
  }

  private List<Result> transToResults(QueryParam queryParam, String[] header, List<Row> records) {
    Map<Map<String, String>, Result> tagsWithResults =
        getTagsWithResults(queryParam, header, records);
    SlidingWindow slidingWindow = queryParam.getSlidingWindow();
    if (Objects.nonNull(slidingWindow) && slidingWindow.getWindowMs() > 0
        && !StringUtils.equalsIgnoreCase("none", slidingWindow.getAggregator())) {
      return slidingWindowResult(queryParam.getStart(), queryParam.getEnd(), slidingWindow,
          tagsWithResults);
    }
    return Lists.newArrayList(tagsWithResults.values());
  }

  private Map<Map<String, String>, Result> getTagsWithResults(QueryParam queryParam,
      String[] header, List<Row> rows) {
    Map<Map<String, String>, Result> tagsToResult = Maps.newHashMap();
    for (Row row : rows) {
      Result result = new Result();
      QueryResult.Point point = new QueryResult.Point();
      Map<String, String> tags = Maps.newHashMap();
      for (String name : header) {
        Column column = row.getColumn(name);
        Value value = column.getValue();
        if ("tsid".equals(name)) {
          continue;
        }
        if ("timestamp".equals(name) || value.getDataType() == Value.DataType.Timestamp) {
          point.setTimestamp(value.getTimestamp());
          continue;
        }
        if ("value".equals(name)) {
          if (value.getObject() instanceof String) {
            point.setStrValue(value.getString());
          } else {
            try {
              point.setValue(Double.parseDouble(value.getObject().toString()));
            } catch (Exception e) {
              point.setValue(0D);
            }
          }
          continue;
        }
        tags.put(name, column.getValue().getString());
      }
      if (point.getTimestamp() == null) {
        point.setTimestamp(0L);
      }
      Result existResult = tagsToResult.get(tags);
      if (existResult != null) {
        existResult.getPoints().add(point);
      } else {
        result.setMetric(fixName(queryParam.getMetric()));
        result.setTags(tags);
        result.setPoints(Lists.newArrayList(point));
        tagsToResult.put(tags, result);
      }
    }
    return tagsToResult;
  }

  private String[] getHeader(Row row) {
    List<Column> columns = row.getColumns();
    String[] headers = new String[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      headers[i] = columns.get(i).getName();
    }
    return headers;
  }

  private List<Result> slidingWindowResult(long start, long end, SlidingWindow slidingWindow,
      Map<Map<String, String>, Result> tagsWithResults) {
    String aggregator = slidingWindow.getAggregator();
    long windowMs = slidingWindow.getWindowMs();
    long window = windowMs / 1000 / 60;
    long windowStart = start / 1000 * 1000;
    long windowStartEnd = end / 1000 * 1000;
    tagsWithResults.forEach((tags, data) -> {
      long now = windowStart;
      LinkedList<Double> windows = Lists.newLinkedList();
      ArrayList<QueryResult.Point> windowPoints = Lists.newArrayList();
      while (now <= windowStartEnd) {
        Map<Long, QueryResult.Point> timePointMap = data.getPoints().stream()
            .collect(Collectors.toMap(QueryResult.Point::getTimestamp, Function.identity()));
        QueryResult.Point point = timePointMap.get(now);
        if (Objects.nonNull(point)) {
          windows.add(point.getValue());
        } else {
          windows.add(-1D);
        }
        // 满足一个窗口，才开始做计算；
        if (windows.size() == window) {
          Double aggregatorValue = getAggregatorValue(windows, aggregator);
          QueryResult.Point windowPoint = new QueryResult.Point();
          windowPoint.setTimestamp(now);
          windowPoint.setValue(aggregatorValue);
          windowPoints.add(windowPoint);
          windows.removeFirst();
        }
        now += 60 * 1000;
      }
      data.setPoints(windowPoints);
    });
    return Lists.newArrayList(tagsWithResults.values());
  }

  private Double getAggregatorValue(LinkedList<Double> windows, String aggregator) {
    Stream<Double> doubleStream = windows.stream().filter(value -> value != -1D);
    if (StringUtils.equalsIgnoreCase("avg", aggregator)) {
      return doubleStream.collect(Collectors.averagingDouble(Double::doubleValue));
    } else if (StringUtils.equalsIgnoreCase("max", aggregator)) {
      return doubleStream.max(Double::compareTo).orElse(0D);
    } else if (StringUtils.equalsIgnoreCase("min", aggregator)) {
      return doubleStream.min(Double::compareTo).orElse(0D);
    } else if (StringUtils.equalsIgnoreCase("sum", aggregator)) {
      return doubleStream.mapToDouble(Double::doubleValue).sum();
    } else if (StringUtils.equalsIgnoreCase("count", aggregator)) {
      return ((Long) doubleStream.count()).doubleValue();
    }
    return 0D;
  }

  private String genSqlWithGroupBy(QueryParam queryParam, String fromSql, String whereSql) {
    List<String> groupBy = queryParam.getGroupBy();
    String aggregator = queryParam.getAggregator();
    String sql;
    if (!CollectionUtils.isEmpty(groupBy)) {
      String groupByStr = groupBy.stream().collect(Collectors.joining("`,`", "`", "`"));
      if (StringUtils.isNotBlank(aggregator) && !StringUtils.equalsIgnoreCase("none", aggregator)) {
        if (StringUtils.isNotBlank(whereSql)) {
          sql =
              "SELECT timestamp, %s, %s(value) as value FROM %s WHERE %s group by timestamp, %s ORDER BY timestamp";
          return String.format(sql, groupByStr, aggregator, fromSql, whereSql, groupByStr);
        } else {
          sql =
              "SELECT timestamp, %s, %s(value) as value FROM %s group by timestamp, %s ORDER BY timestamp";
          return String.format(sql, groupByStr, aggregator, fromSql, groupByStr);
        }
      } else {
        if (StringUtils.isNotBlank(whereSql)) {
          sql = "SELECT timestamp, %s, value FROM %s WHERE %s ORDER BY timestamp";
          return String.format(sql, groupByStr, fromSql, whereSql);
        } else {
          sql = "SELECT timestamp, %s, value FROM %s ORDER BY timestamp";
          return String.format(sql, groupByStr, fromSql);
        }
      }
    } else if (StringUtils.isNotBlank(whereSql)) {
      sql = "SELECT * FROM %s WHERE %s ORDER BY timestamp";
      return String.format(sql, fromSql, whereSql);
    } else {
      sql = "SELECT * FROM %s ORDER BY timestamp";
      return String.format(sql, fromSql);
    }
  }

  private String parseWhere(QueryParam queryParam) {
    StringBuilder whereSqlBuilder = new StringBuilder();
    boolean isNestedQuery = StringUtils.isNotBlank(queryParam.getDownsample())
        && StringUtils.isNotBlank(queryParam.getAggregator())
        && !StringUtils.equalsIgnoreCase(queryParam.getAggregator(), "none");
    if (!isNestedQuery) {
      long start = queryParam.getStart();
      long end = queryParam.getEnd();
      whereSqlBuilder.append("timestamp >= ").append(start).append(" AND ").append(" timestamp <= ")
          .append(end);
    }
    List<QueryFilter> filters = queryParam.getFilters();
    if (!CollectionUtils.isEmpty(filters)) {
      if (!isNestedQuery) {
        whereSqlBuilder.append(" AND ");
      }
      for (int i = 0; i < filters.size(); i++) {
        QueryFilter queryFilter = filters.get(i);
        String type = queryFilter.getType();
        if (StringUtils.equalsIgnoreCase(type, "literal_or")) {
          whereSqlBuilder.append(queryFilter.getName()).append(" IN ('")
              .append(queryFilter.getValue().replace("|", "','")).append("')");
        } else if (StringUtils.equalsIgnoreCase(type, "not_literal_or")) {
          whereSqlBuilder.append(queryFilter.getName()).append(" NOT IN ('")
              .append(queryFilter.getValue().replace("|", "','")).append("')");
        } else if (StringUtils.equalsIgnoreCase(type, "literal")) {
          whereSqlBuilder.append(queryFilter.getName()).append(" = '")
              .append(queryFilter.getValue()).append("'");
        } else if (StringUtils.equalsIgnoreCase(type, "not_literal")) {
          whereSqlBuilder.append(queryFilter.getName()).append(" != '")
              .append(queryFilter.getValue()).append("'");
        } else {
          // todo wildcard, regexp, not_regexp_match
          throw new IllegalArgumentException(String.format("not support %s filter", type));
        }
        if (i != filters.size() - 1) {
          whereSqlBuilder.append(" AND ");
        }
      }
    }
    return whereSqlBuilder.toString();
  }

  private String parseFrom(QueryParam queryParam) {
    String downsample = queryParam.getDownsample();
    String fromMetrics = fixName(queryParam.getMetric());
    String aggregator = queryParam.getAggregator();
    if (StringUtils.isNotBlank(downsample) && StringUtils.isNotBlank(aggregator)
        && !StringUtils.equalsIgnoreCase(aggregator, "none")) {
      String downsampleSql = "(SELECT time_bucket(`%s`, '%s') as timestamp, %s,"
          + " %s(value) as value FROM %s WHERE timestamp >= %s AND timestamp <= %s group by time_bucket(`%s`, '%s'),%s)";
      String interval = getInterval(downsample);
      if (StringUtils.isNotBlank(interval)) {
        String tagNames;
        try {
          tagNames = getTagNames(queryParam);
        } catch (Exception e) {
          LOGGER.error("get {} tags error", fromMetrics, e);
          return StringUtils.EMPTY;
        }
        return String.format(downsampleSql, "timestamp", interval, tagNames, aggregator,
            queryParam.getMetric(), queryParam.getStart(), queryParam.getEnd(), "timestamp",
            interval, tagNames);
      }
    }
    return fromMetrics;
  }

  private String getTagNames(QueryParam queryParam) {
    Set<String> fields = querySchema(queryParam).getTags().keySet();
    if (CollectionUtils.isEmpty(fields)) {
      throw new RuntimeException("fields is empty");
    }
    return fields.stream().collect(Collectors.joining("`,`", "`", "`"));
  }

  private String getInterval(String str) {
    String lastChar = StringUtils.substring(str, str.length() - 1, str.length());
    if (SUPPORT_SMH_UNIT.contains(lastChar)) {
      return "PT" + str.toUpperCase();
    } else if (SUPPORT_DMY_UNIT.contains(lastChar)) {
      return "P" + str.toUpperCase();
    }
    return null;
  }

  public SqlQueryOk execSQL(String tenant, String sql) {
    try {
      io.ceresdb.models.Result<SqlQueryOk, Err> result =
          ceresdbxClientManager.getClient(tenant).sqlQuery(new SqlQueryRequest(sql)).get();
      if (!result.isOk()) {
        LOGGER.error("[CERESDBX_EXEC] {}, exec sql:{} is not ok ", tenant, sql);
      }
      return result.getOk();
    } catch (Exception e) {
      LOGGER.error("[CERESDBX_EXEC] {} failed to exec sql:{}, error:{}", tenant, sql,
          e.getMessage(), e);
      return SqlQueryOk.emptyOk();
    }
  }

  private String fixName(String name) {
    return name.replace('.', '_');
  }

}
