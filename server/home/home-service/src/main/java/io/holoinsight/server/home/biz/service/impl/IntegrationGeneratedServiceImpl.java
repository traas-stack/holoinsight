/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.IntegrationGeneratedService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.IntegrationGeneratedConverter;
import io.holoinsight.server.home.dal.mapper.IntegrationGeneratedMapper;
import io.holoinsight.server.home.dal.model.IntegrationGenerated;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: IntegrationGeneratedServiceImpl.java, v 0.1 2022年12月14日 上午11:54 jinsong.yjs Exp $
 */
@Service
public class IntegrationGeneratedServiceImpl
    extends ServiceImpl<IntegrationGeneratedMapper, IntegrationGenerated>
    implements IntegrationGeneratedService {

  @Autowired
  private IntegrationGeneratedConverter integrationGeneratedConverter;

  @Override
  public void insert(IntegrationGeneratedDTO integrationGeneratedDTO) {
    save(integrationGeneratedConverter.dtoToDO(integrationGeneratedDTO));
  }

  @Override
  public void update(IntegrationGeneratedDTO integrationGeneratedDTO) {
    updateById(integrationGeneratedConverter.dtoToDO(integrationGeneratedDTO));
  }

  @Override
  public IntegrationGeneratedDTO queryById(Long id, String tenant, String workspace) {
    QueryWrapper<IntegrationGenerated> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    if (StringUtil.isNotBlank(workspace)) {
      wrapper.eq("workspace", workspace);
    }
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    return integrationGeneratedConverter.doToDTO(this.getOne(wrapper));
  }

  @Override
  public List<IntegrationGenerated> queryByTenant(String tenant) {
    QueryWrapper<IntegrationGenerated> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("name", "product", "tenant", "workspace", "item", "id").eq("deleted", 0)
        .eq("tenant", tenant);
    return baseMapper.selectList(queryWrapper);
  }

  @Override
  public List<IntegrationGenerated> queryByTenant(String tenant, String workspace) {
    QueryWrapper<IntegrationGenerated> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("name", "product", "tenant", "workspace", "item", "id").eq("deleted", 0)
        .eq("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      queryWrapper.eq("workspace", workspace);
    }
    return baseMapper.selectList(queryWrapper);
  }

  @Override
  public List<IntegrationGeneratedDTO> queryByName(String tenant, String workspace, String name) {
    Map<String, Object> map = new HashMap<>();
    map.put("tenant", tenant);
    if (StringUtil.isNotBlank(workspace)) {
      map.put("workspace", workspace);
    }
    map.put("name", name);
    return integrationGeneratedConverter.dosToDTOs(listByMap(map));
  }
}
