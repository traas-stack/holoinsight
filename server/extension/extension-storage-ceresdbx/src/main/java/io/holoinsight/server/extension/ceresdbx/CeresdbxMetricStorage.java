package io.holoinsight.server.extension.ceresdbx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xzchaoo.commons.stat.StringsKey;
import io.ceresdb.CeresDBClient;
import io.ceresdb.models.Err;
import io.ceresdb.models.FieldValue;
import io.ceresdb.models.QueryOk;
import io.ceresdb.models.QueryRequest;
import io.ceresdb.models.Record;
import io.ceresdb.models.Record.FieldDescriptor;
import io.ceresdb.models.Rows;
import io.ceresdb.models.Series;
import io.ceresdb.models.Series.Builder;
import io.ceresdb.models.SqlResult;
import io.ceresdb.models.WriteOk;
import io.grpc.Context;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.ceresdbx.utils.StatUtils;
import io.holoinsight.server.extension.ceresdbx.utils.StorageMonitor;
import io.holoinsight.server.extension.model.QueryMetricsParam;
import io.holoinsight.server.extension.model.QueryParam.SlidingWindow;
import io.holoinsight.server.extension.model.QueryResult;
import io.holoinsight.server.extension.model.QueryResult.Result;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import io.holoinsight.server.extension.model.WriteMetricsParam.Point;
import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryParam;
import io.holoinsight.server.extension.model.QueryParam.QueryFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

/**
 * @author jinyan.ljw
 * @date 2023/1/13
 */
public class CeresdbxMetricStorage implements MetricStorage {

  private static final Logger LOGGER = LoggerFactory.getLogger(CeresdbxMetricStorage.class);

  CeresdbxClientManager ceresdbxClientManager;
  private static final List<String> SUPPORT_SMH_UNIT = Lists.newArrayList("s", "m", "h");
  private static final List<String> SUPPORT_DMY_UNIT = Lists.newArrayList("d", "M", "y");
  public static final int DEFAULT_BATCH_RECORDS = 200;
  public static final int DEFAULT_BATCH_POINTS = 512;
  public static final int DEFAULT_METRIC_LIMIT = 100;

  public CeresdbxMetricStorage(CeresdbxClientManager ceresdbxClientManager) {
    this.ceresdbxClientManager = ceresdbxClientManager;
  }

  @Override
  public Mono<Void> write(WriteMetricsParam writeMetricsParam) {
    List<Point> points = writeMetricsParam.getPoints();
    if (CollectionUtils.isEmpty(points)) {
      return Mono.empty();
    }
    String tenant = writeMetricsParam.getTenant();
    Iterator<Point> pointIter = points.iterator();
    List<Rows> oneBatch = Lists.newLinkedList();
    int dps = 0;
    Set<String> metrics = Sets.newHashSet();
    while (pointIter.hasNext()) {
      Point point = pointIter.next();
      dps += convertToRows(metrics, point, oneBatch);
      if (!pointIter.hasNext() || oneBatch.size() >= DEFAULT_BATCH_RECORDS
          || dps >= DEFAULT_BATCH_POINTS) {
        List<Rows> oneBatch2 = oneBatch;
        Context.ROOT.run(() -> doBatchInsert(metrics, tenant, oneBatch2));
        oneBatch = Lists.newLinkedList();
        dps = 0;
      }
    }
    return Mono.empty();
  }

  private void doBatchInsert(Set<String> metrics, String tenant, List<Rows> oneBatch) {
    long start = System.currentTimeMillis();
    CeresDBClient client = ceresdbxClientManager.getClient(tenant);
    CompletableFuture<io.ceresdb.models.Result<WriteOk, Err>> future = client.write(oneBatch);
    future.whenComplete((result, throwable) -> {
      if (result != null && result.isOk()) {
        StatUtils.STORAGE_WRITE.add(StringsKey.of("CeresDBx", tenant, "Y"),
            new long[] {1, oneBatch.size(), System.currentTimeMillis() - start});
      } else {
        StatUtils.STORAGE_WRITE.add(StringsKey.of("CeresDBx", tenant, "N"),
            new long[] {1, oneBatch.size(), System.currentTimeMillis() - start});
        if (result != null && null != result.getErr()) {
          LOGGER.error("save metrics:{} to CeresDBx result:{}, error msg:{}", metrics, result,
              result.getErr().getError());
        } else if (null != throwable) {
          LOGGER.error("save metrics:{} to CeresDBx result:{}, error msg:{}", metrics, result,
              throwable);
        } else {
          LOGGER.error("save metrics:{} to CeresDBx error", metrics);
        }
      }
    });
  }

  private int convertToRows(Set<String> metrics, Point point, List<Rows> oneBatch) {
    Map<String, String> tags = point.getTags();
    String metric = fixName(point.getMetricName());
    metrics.add(metric);
    Builder builder = Series.newBuilder(metric);
    tags.forEach(builder::tag);
    HashMap<String, FieldValue> fields = new HashMap<>();
    fields.put("value", FieldValue.withDouble(point.getValue()));
    oneBatch.add(builder.toRowsBuilder().fields(point.getTimeStamp(), fields).build());
    return tags.size();
  }

  @Override
  public List<Result> queryData(QueryParam queryParam) {
    String whereStatement = parseWhere(queryParam);
    String fromStatement = parseFrom(queryParam);
    String sql = genSqlWithGroupBy(queryParam, fromStatement, whereStatement);
    LOGGER.info("queryData queryparam:{}, sql:{}", queryParam, sql);
    QueryRequest request =
        QueryRequest.newBuilder().forMetrics(fixName(queryParam.getMetric())).ql(sql).build();
    CompletableFuture<io.ceresdb.models.Result<QueryOk, Err>> qf =
        ceresdbxClientManager.getClient(queryParam.getTenant()).query(request);
    try {
      final io.ceresdb.models.Result<QueryOk, Err> qr = qf.get();
      if (!qr.isOk()) {
        LOGGER.error("[CERESDBX_QUERY] failed to exec sql:{}, qr:{}, error:{}", sql, qr,
            qr.getErr().getError());
        throw new RuntimeException(qr.getErr().getError());
      }
      final List<Record> records = qr.getOk().mapToRecord().collect(Collectors.toList());
      if (CollectionUtils.isEmpty(records)) {
        return Lists.newArrayList();
      }
      String[] header = getHeader(records.get(0));
      return transToResults(queryParam, header, records);
    } catch (Exception e) {
      LOGGER.error("[CERESDBX_QUERY] failed to exec sql:{}, error:{}", sql, e.getMessage());
      return Lists.newArrayList();
    }
  }

  private List<Result> transToResults(QueryParam queryParam, String[] header,
      List<Record> records) {
    Map<Map<String, String>, Result> tagsWithResults =
        getTagsWithResults(queryParam, header, records);
    SlidingWindow slidingWindow = queryParam.getSlidingWindow();
    if (Objects.nonNull(slidingWindow) && slidingWindow.getWindowMs() > 0
        && !StringUtils.equalsIgnoreCase("none", slidingWindow.getAggregator())) {
      return SlidingWindowResult(queryParam.getStart(), queryParam.getEnd(), slidingWindow,
          tagsWithResults);
    }
    return Lists.newArrayList(tagsWithResults.values());
  }

  private Map<Map<String, String>, Result> getTagsWithResults(QueryParam queryParam,
      String[] header, List<Record> records) {
    Map<Map<String, String>, Result> tagsToResult = Maps.newHashMap();
    for (Record record : records) {
      Result result = new Result();
      QueryResult.Point point = new QueryResult.Point();
      Map<String, String> tags = Maps.newHashMap();
      for (String name : header) {
        if ("value".equals(name)) {
          Object value = record.get(name);
          if (Objects.nonNull(value)) {
            point.setValue((((Number) value).doubleValue()));
          } else {
            point.setValue(0D);
          }
          continue;
        }
        if ("timestamp".equals(name)) {
          point.setTimestamp(record.getTimestamp(name));
          continue;
        }
        if ("tsid".equals(name)) {
          continue;
        }
        tags.put(name, record.get(name).toString());
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

  private String[] getHeader(Record record) {
    List<FieldDescriptor> fieldDescriptors = record.getFieldDescriptors();
    String[] headers = new String[fieldDescriptors.size()];
    for (int i = 0; i < fieldDescriptors.size(); i++) {
      headers[i] = fieldDescriptors.get(i).getName();
    }
    return headers;
  }

  private List<Result> SlidingWindowResult(long start, long end, SlidingWindow slidingWindow,
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
      String groupByStr = StringUtils.join(groupBy, ",");
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
      whereSqlBuilder.append("timestamp >= ").append(start).append(" AND ").append(" timestamp < ")
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
          + " %s(value) as value FROM %s WHERE timestamp >= %s AND timestamp < %s group by time_bucket(`%s`, '%s'),%s)";
      String interval = getInterval(downsample);
      if (StringUtils.isNotBlank(interval)) {
        String tagNames = getTagNames(queryParam);
        fromMetrics = String.format(downsampleSql, "timestamp", interval, tagNames, aggregator,
            queryParam.getMetric(), queryParam.getStart(), queryParam.getEnd(), "timestamp",
            interval, tagNames);
      }
    }
    return fromMetrics;
  }

  private String getTagNames(QueryParam queryParam) {
    Set<String> fields = querySchema(queryParam).getTags().keySet();
    return StringUtils.join(fields, ",");
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
    SqlResult sqlResult = execSQL(queryParam.getTenant(), "DESCRIBE " + metric);
    Result result = new Result();
    result.setMetric(metric);
    Map<String, String> tags = Maps.newHashMap();
    List<Map<String, Object>> rows = sqlResult.getRows();
    if (CollectionUtils.isEmpty(rows)) {
      result.setTags(Maps.newHashMap());
      return result;
    }
    rows.stream().filter(row -> (boolean) row.get("is_tag"))
        .forEach(map -> tags.put(map.get("name").toString(), "-"));
    result.setTags(tags);
    return result;
  }

  @Override
  public List<String> queryMetrics(QueryMetricsParam queryParam) {
    SqlResult sqlResult =
        execSQL(queryParam.getTenant(), "show tables like '" + queryParam.getName() + "%%'");
    List<Map<String, Object>> rows = sqlResult.getRows();
    List<String> result = Lists.newArrayList();
    if (CollectionUtils.isEmpty(rows)) {
      return result;
    }
    int limit = queryParam.getLimit() > 0 ? queryParam.getLimit() : DEFAULT_METRIC_LIMIT;
    rows.stream().limit(limit).forEach(map -> result.add(map.get("Tables").toString()));
    return result;
  }

  @Override
  public List<Result> pqlInstantQuery(PqlParam pqlParam) {
    return null;
  }

  @Override
  public List<Result> pqlRangeQuery(PqlParam pqlParam) {
    return null;
  }

  public SqlResult execSQL(String tenant, String sql) {
    try {
      LOGGER.info("[ceresdbx_exec] {}, exec sql:{} ", tenant, sql);
      return ceresdbxClientManager.getClient(tenant).management().executeSql(sql);
    } catch (Exception e) {
      LOGGER.error("[ceresdbx_exec] {} failed to exec sql:{}, error:{}", tenant, sql,
          e.getMessage(), e);
    }
    return SqlResult.EMPTY_RESULT;
  }

  private String fixName(String name) {
    return name.replace('.', '_');
  }

}
