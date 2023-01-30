/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : IntegrationProductDTO.java, v 0.1 2022年06月08日 16:41 xiangwanpeng Exp $
 */
@Data
public class IntegrationMetricsDTO {
  Map<String, List<IntegrationMetricDTO>> subMetrics;
}
