/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import io.holoinsight.server.common.dao.entity.dto.TenantDTO;
import io.holoinsight.server.common.dao.mapper.TenantMapper;
import io.holoinsight.server.common.dao.entity.Tenant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.converter.TenantConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantService.java, v 0.1 2022年02月22日 2:15 下午 jinsong.yjs Exp $
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

  @Autowired
  private TenantConverter tenantConverter;

  public List<TenantDTO> queryAll() {

    return tenantConverter.dosToDTOs(list());
  }

  public TenantDTO get(Long id) {
    Tenant byId = getById(id);
    return tenantConverter.doToDTO(byId);
  }

  @Override
  public TenantDTO getByCode(String code) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("code", code);
    List<Tenant> tenants = listByMap(columnMap);
    if (CollectionUtils.isEmpty(tenants))
      return null;
    return tenantConverter.doToDTO(tenants.get(0));
  }

  public void create(TenantDTO tenantDTO) {
    tenantDTO.setGmtCreate(new Date());
    tenantDTO.setGmtModified(new Date());

    save(tenantConverter.dtoToDO(tenantDTO));
  }

  public void update(TenantDTO tenantDTO) {
    tenantDTO.setGmtModified(new Date());
    updateById(tenantConverter.dtoToDO(tenantDTO));
  }

  public void deleteById(Long id) {
    removeById(id);
  }

}
