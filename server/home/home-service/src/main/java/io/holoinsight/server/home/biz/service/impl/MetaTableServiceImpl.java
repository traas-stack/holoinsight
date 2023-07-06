/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.MetaTableService;
import io.holoinsight.server.home.common.util.JpaUpdateUtil;
import io.holoinsight.server.home.dal.converter.MetaTableConverter;
import io.holoinsight.server.home.dal.mapper.MetaTableMapper;
import io.holoinsight.server.home.dal.model.MetaTable;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
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
 * @version 1.0: MetaTableServiceImpl.java, v 0.1 2022年11月03日 下午9:03 jinsong.yjs Exp $
 */
@Service
public class MetaTableServiceImpl extends ServiceImpl<MetaTableMapper, MetaTable>
    implements MetaTableService {

  @Autowired
  private MetaTableConverter metaTableConverter;


  @Override
  public MetaTableDTO queryById(Long id, String tenant) {
    QueryWrapper<MetaTable> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    MetaTable model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }

    return metaTableConverter.doToDTO(model);
  }

  @Override
  public List<MetaTableDTO> findByTenant(String tenant) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("tenant", tenant);
    return metaTableConverter.dosToDTOs(listByMap(columnMap));
  }

  @Override
  public List<MetaTableDTO> findAll() {
    return metaTableConverter.dosToDTOs(list());
  }

  @Override
  public List<MetaTableDTO> findByName(String name) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("name", name);
    return metaTableConverter.dosToDTOs(listByMap(columnMap));
  }

  @Override
  public MetaTableDTO create(MetaTableDTO metaTableDTO) {
    metaTableDTO.setGmtCreate(new Date());
    metaTableDTO.setGmtModified(new Date());
    MetaTable metaTable = metaTableConverter.dtoToDO(metaTableDTO);

    save(metaTable);

    return metaTableConverter.doToDTO(metaTable);
  }

  @Override
  public void deleteById(Long id) {
    removeById(id);
  }

  @Override
  public MetaTableDTO update(MetaTableDTO metaTableDTO) {
    metaTableDTO.setGmtModified(new Date());

    MetaTable target = metaTableConverter.dtoToDO(metaTableDTO);
    // 从数据库中获取对象
    MetaTable original = getById(metaTableDTO.getId());
    // 复制想要更改的字段值
    BeanUtils.copyProperties(target, original, JpaUpdateUtil.getNullPropertyNames(target));
    // 更新操作

    saveOrUpdate(original);

    return metaTableConverter.doToDTO(original);
  }

  @Override
  public MetaTableDTO insertOrUpate(MetaTableDTO metaTableDTO) {
    List<MetaTableDTO> byName = findByName(metaTableDTO.name);

    if (CollectionUtils.isEmpty(byName)) {
      return create(metaTableDTO);
    }

    metaTableDTO.setGmtCreate(byName.get(0).gmtCreate);
    metaTableDTO.setId(byName.get(0).id);
    return update(metaTableDTO);
  }
}
