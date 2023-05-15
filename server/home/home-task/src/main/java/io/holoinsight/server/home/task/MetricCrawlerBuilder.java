/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MetricCrawler.java, Date: 2023-05-10 Time: 16:39
 */
public interface MetricCrawlerBuilder {

  List<MetricInfo> buildEntity(IntegrationProductDTO integrationProduct);

}
