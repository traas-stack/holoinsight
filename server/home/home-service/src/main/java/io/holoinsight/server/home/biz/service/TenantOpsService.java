/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;


import io.holoinsight.server.common.dao.entity.TenantOps;
import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsDTO;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantOpsService.java, v 0.1 2022年05月31日 11:27 上午 jinsong.yjs Exp $
 */
public interface TenantOpsService extends IService<TenantOps> {

  TenantOpsDTO get(Long id);

  void create(TenantOpsDTO tenantOpsDTO);

  void update(TenantOpsDTO tenantOpsDTO);

  void deleteById(Long id);

  void createOrUpdate(TenantOpsDTO tenantOpsDTO);
}
