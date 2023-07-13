/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.AlarmMetric;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AlarmMetricService.java, Date: 2023-06-08 Time: 21:50
 */
public interface AlarmMetricService extends IService<AlarmMetric> {

  AlarmMetric queryByMetric(Long ruleId, String metric, String tenant, String workspace);

  List<AlarmMetric> queryByMetric(String metric, String tenant, String workspace);

  List<AlarmMetric> queryByRuleId(Long ruleId, String tenant, String workspace);

  List<AlarmMetric> queryByMetric(List<String> metrics, String tenant, String workspace);
}
