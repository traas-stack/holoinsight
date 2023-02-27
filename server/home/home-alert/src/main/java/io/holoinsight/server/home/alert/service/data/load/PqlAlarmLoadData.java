/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data.load;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.query.grpc.QueryProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zanghaibo
 * @time 2022-12-28 6:56 下午
 */

@Service
public class PqlAlarmLoadData {

  private static Logger LOGGER = LoggerFactory.getLogger(PqlAlarmLoadData.class);

  @Resource
  private QueryClientService queryClientService;

  public List<DataResult> queryDataResult(ComputeTaskPackage computeTask,
      InspectConfig inspectConfig) {
    List<DataResult> dataResults = new ArrayList<>();
    QueryProto.PqlRangeRequest pqlRangeRequest = QueryProto.PqlRangeRequest.newBuilder()
        .setTenant(inspectConfig.getTenant()).setStart(computeTask.getTimestamp())
        .setEnd(computeTask.getTimestamp() + 1L * PeriodType.MINUTE.intervalMillis() - 1L)
        .setQuery(inspectConfig.getPqlRule().getPql()).setStep(60000L).build();

    QueryProto.QueryResponse response = queryClientService.queryPqlRange(pqlRangeRequest);
    if (response != null) {
      for (QueryProto.Result result : response.getResultsList()) {
        DataResult dataResult = new DataResult();
        dataResult.setMetric(result.getMetric());
        dataResult.setTags(result.getTagsMap());
        Map<Long, Double> points = new HashMap<>();
        for (QueryProto.Point point : result.getPointsList()) {
          Double value = Double.valueOf(point.getStrValue());
          points.put(point.getTimestamp(), value);
        }
        dataResult.setPoints(points);
        dataResults.add(dataResult);
      }

    }
    LOGGER.info("query pql result from {} {} {}", computeTask.getTimestamp(),
        inspectConfig.getPqlRule().getPql(), J.toJson(dataResults));
    return dataResults;
  }
}
