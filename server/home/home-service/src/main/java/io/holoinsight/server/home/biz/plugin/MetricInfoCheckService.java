/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;

import java.util.List;

/**
 * @author masaimu
 * @version 2023-09-14 10:37:00
 */
public interface MetricInfoCheckService {

  List<MetricInfoDTO> queryMetricInfoByMetricType(String tenant, String workspace, String product);
}
