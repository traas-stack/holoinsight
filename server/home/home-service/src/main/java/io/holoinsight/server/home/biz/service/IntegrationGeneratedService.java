/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.IntegrationGenerated;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: IntegrationGeneratedService.java, v 0.1 2022年12月14日 上午11:53 jinsong.yjs Exp $
 */
public interface IntegrationGeneratedService extends IService<IntegrationGenerated> {

  void insert(IntegrationGeneratedDTO integrationGeneratedDTO);

  void update(IntegrationGeneratedDTO integrationGeneratedDTO);

  List<IntegrationGenerated> queryByTenant(String tenant);

  List<IntegrationGenerated> queryByTenant(String tenant, String workspace);
}
