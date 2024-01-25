/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.model.compute.ComputeInfo;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.service.data.load.RuleAlarmLoadData;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.query.grpc.QueryProto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author masaimu
 * @version 2023-06-02 16:53:00
 */
@Slf4j
@Service
public class NullValueTracker {
  @Autowired
  private RuleAlarmLoadData ruleAlarmLoadData;

  @Resource
  private QueryClientService queryClientService;

  private ConcurrentHashMap<Long, List<Record>> map = new ConcurrentHashMap<>();


  public void record(DataResult dataResult, Trigger trigger, List<Long> nullValTimes,
      ComputeInfo computeInfo) {
    if (!enable()) {
      return;
    }
    long period = computeInfo.getPeriod();
    log.info("record at {} {}", period, dataResult.getKey());
    List<Record> records = map.computeIfAbsent(period, k -> new ArrayList<>());
    records.add(new Record(dataResult, trigger, nullValTimes, period, computeInfo.getTenant()));
  }

  public List<Long> hasNullValue(DataResult dataResult,
      List<FunctionConfigParam> functionConfigParams) {
    if (CollectionUtils.isEmpty(functionConfigParams)) {
      return Collections.emptyList();
    }
    List<Long> nullValTimes = new ArrayList<>();
    Map<Long, Double> points = dataResult.getPoints();
    for (FunctionConfigParam functionConfigParam : functionConfigParams) {
      long duration = functionConfigParam.getDuration();
      for (long i = 0; i < duration; i++) {
        long time = functionConfigParam.getPeriod() - i * 60000L;
        Double current = points.get(time);
        if (current == null) {
          nullValTimes.add(time);
        }
      }
    }
    return nullValTimes;
  }

  @Scheduled(cron = "0 0/1 * * * ?")
  public void review() {
    if (!enable()) {
      return;
    }
    List<Record> allRecords = new ArrayList<>();
    long cur = System.currentTimeMillis() - 1800_000;
    for (Long k : map.keySet()) {
      if (k <= cur) {
        List<Record> records = map.remove(k);
        if (!CollectionUtils.isEmpty(records)) {
          allRecords.addAll(records);
        }
      }
    }
    log.info("FIND_CONFLICT_SIZE {}", allRecords.size());
    for (Record record : allRecords) {
      CheckResult checkResult = doCheck(record);
      if (checkResult.hasConflict()) {
        log.info("FIND_CONFLICT_RESULT {}", J.toJson(checkResult));
      }
    }
  }

  private boolean enable() {
    String value = MetaDictUtil.getStringValue("task_enable", "NullValueTracker");
    return StringUtils.isNotEmpty(value) && "true".equals(value);
  }

  private CheckResult doCheck(Record record) {
    if (CollectionUtils.isEmpty(record.nullValTimes)) {
      return CheckResult.noConflict();
    }

    QueryProto.QueryRequest request =
        ruleAlarmLoadData.buildRequest(record.period, record.tenant, record.trigger);
    QueryProto.QueryResponse response = queryClientService.queryData(request, "ALERT");

    Map<Long, Double> points = new HashMap<>();
    if (response != null && !CollectionUtils.isEmpty(response.getResultsList())) {
      for (QueryProto.Result result : response.getResultsList()) {
        if (record.equalMetricAndTags(result)) {
          for (QueryProto.Point point : result.getPointsList()) {
            points.put(point.getTimestamp(), point.getValue());
          }
        }
      }
    }
    if (CollectionUtils.isEmpty(points)) {
      return CheckResult.noConflict();
    }

    for (Long timestamp : record.nullValTimes) {
      Double curValue = points.get(timestamp);
      if (curValue != null) {
        return CheckResult.conflictAt(curValue, record, timestamp);
      }
    }
    return CheckResult.noConflict();
  }

  private static class Record {
    DataResult dataResult;
    Trigger trigger;
    List<Long> nullValTimes;
    long period;
    String tenant;

    public Record(DataResult dataResult, Trigger trigger, List<Long> nullValTimes, long period,
        String tenant) {
      this.dataResult = dataResult;
      this.trigger = trigger;
      this.nullValTimes = nullValTimes;
      this.period = period;
      this.tenant = tenant;
    }

    public boolean equalMetricAndTags(QueryProto.Result result) {
      DataResult key = new DataResult();
      key.setMetric(result.getMetric());
      key.setTags(result.getTagsMap());
      return StringUtils.equals(dataResult.getKey(), key.getKey());
    }
  }

  private static class CheckResult {
    boolean conflict;
    double curValue;
    String metric;

    Map<String, String> tags;
    long cur;


    public static CheckResult noConflict() {
      CheckResult checkResult = new CheckResult();
      checkResult.conflict = false;
      return checkResult;
    }

    public static CheckResult conflictAt(Double curValue, Record record, Long timestamp) {
      CheckResult checkResult = new CheckResult();
      checkResult.conflict = true;
      checkResult.curValue = curValue;
      checkResult.metric = record.dataResult.getMetric();
      checkResult.tags = record.dataResult.getTags();
      checkResult.cur = timestamp;
      return checkResult;
    }

    public boolean hasConflict() {
      return conflict;
    }
  }
}
