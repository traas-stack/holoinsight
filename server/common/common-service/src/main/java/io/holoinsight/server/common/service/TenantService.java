/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.dao.entity.Tenant;
import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.dto.TenantDTO;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantService.java, v 0.1 2022年05月31日 11:27 上午 jinsong.yjs Exp $
 */
public interface TenantService extends IService<Tenant> {
  List<TenantDTO> queryAll();

  TenantDTO get(Long id);

  TenantDTO getByCode(String code);

  void create(TenantDTO tenantDTO);

  void update(TenantDTO tenantDTO);

  void deleteById(Long id);
}
