/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsDTO;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import io.holoinsight.server.home.biz.service.TenantOpsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.dal.converter.TenantOpsConverter;
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
 * @version 1.0: TenantOpsServiceImpl.java, v 0.1 2022年06月21日 3:17 下午 jinsong.yjs Exp $
 */
@Service
public class TenantOpsServiceImpl extends ServiceImpl<TenantOpsMapper, TenantOps>
    implements TenantOpsService {

  @Autowired
  private TenantOpsConverter tenantOpsConverter;

  @Override
  public TenantOpsDTO get(Long id) {
    TenantOps byId = getById(id);
    return tenantOpsConverter.doToDTO(byId);
  }

  @Override
  public void create(TenantOpsDTO tenantOpsDTO) {
    tenantOpsDTO.setGmtCreate(new Date());
    tenantOpsDTO.setGmtCreate(new Date());
    save(tenantOpsConverter.dtoToDO(tenantOpsDTO));
  }

  @Override
  public void update(TenantOpsDTO tenantOpsDTO) {

    tenantOpsDTO.setGmtModified(new Date());
    updateById(tenantOpsConverter.dtoToDO(tenantOpsDTO));
  }

  @Override
  public void deleteById(Long id) {
    removeById(id);
  }

  @Override
  public void createOrUpdate(TenantOpsDTO tenantOpsDTO) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("tenant", tenantOpsDTO.tenant);
    tenantOpsDTO.setGmtModified(new Date());
    List<TenantOps> tenantOps = listByMap(columnMap);
    if (CollectionUtils.isEmpty(tenantOps)) {
      create(tenantOpsDTO);
    } else {
      tenantOpsDTO.setId(tenantOps.get(0).id);
      tenantOpsDTO.setGmtCreate(tenantOps.get(0).gmtCreate);
      update(tenantOpsDTO);
    }
  }
}
