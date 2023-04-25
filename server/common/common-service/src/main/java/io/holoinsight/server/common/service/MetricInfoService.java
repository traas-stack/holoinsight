/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MetricInfoService.java, Date: 2023-04-24 Time: 20:28
 */
public interface MetricInfoService extends IService<MetricInfo> {

  List<MetricInfoDTO> queryListByTenantProduct(String tenant, String workspace, String product);

}
