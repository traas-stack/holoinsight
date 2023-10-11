/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.holoinsight.server.common.dao.entity.AggTaskV1DO;
import io.holoinsight.server.common.dao.entity.AggTaskV1DOExample;
import io.holoinsight.server.common.dao.mapper.AggTaskV1DOMapper;
import lombok.Data;

/**
 * <p>
 * created at 2023/9/28
 *
 * @author xzchaoo
 */
public class AggTaskV1DOUtils {

  public static Map<String, Delta> selectDeltas(AggTaskV1DOMapper mapper, Date begin, Date end) {
    AggTaskV1DOExample example = AggTaskV1DOExample.newAndCreateCriteria() //
        .andGmtModifiedGreaterThanOrEqualTo(begin) //
        .andGmtModifiedLessThan(end) //
        .example(); //

    List<AggTaskV1DO> deltaList = mapper.selectByExampleWithBLOBs(example);

    Map<String, Delta> deltaMap = new HashMap<>();

    for (AggTaskV1DO task : deltaList) {
      Delta d = deltaMap.computeIfAbsent(task.getAggId(), Delta::new);
      if (task.getDeleted() == 0) {
        d.add.add(task);
      } else {
        d.del.add(task);
      }
    }

    List<String> doubleChecks = new ArrayList<>();
    for (Delta e : deltaMap.values()) {
      if (e.add.size() != 1) {
        doubleChecks.add(e.aggId);
      }
    }

    if (!doubleChecks.isEmpty()) {
      for (String aggId : doubleChecks) {
        deltaMap.remove(aggId);
      }

      example = AggTaskV1DOExample.newAndCreateCriteria() //
          .andAggIdIn(doubleChecks) //
          .example(); //

      List<AggTaskV1DO> deltaList2 = mapper.selectByExampleWithBLOBs(example);
      for (AggTaskV1DO task : deltaList2) {
        Delta d = deltaMap.computeIfAbsent(task.getAggId(), Delta::new);
        if (task.getDeleted() == 0) {
          d.add.add(task);
        } else {
          d.del.add(task);
        }
      }
    }

    return deltaMap;
  }

  @Data
  public static class Delta {
    public String aggId;
    public List<AggTaskV1DO> add = new ArrayList<>();
    public List<AggTaskV1DO> del = new ArrayList<>();

    Delta(String aggId) {
      this.aggId = aggId;
    }
  }
}

