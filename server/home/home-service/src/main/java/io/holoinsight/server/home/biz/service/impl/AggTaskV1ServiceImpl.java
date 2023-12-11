/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.home.biz.service.AggTaskV1Service;
import io.holoinsight.server.home.dal.converter.AggTaskV1Converter;
import io.holoinsight.server.home.dal.mapper.AggTaskV1Mapper;
import io.holoinsight.server.home.dal.model.AggTaskV1;
import io.holoinsight.server.home.dal.model.dto.AggTaskV1DTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: AggTaskV1ServiceImpl.java, Date: 2023-12-06 Time: 15:38
 */
@Service
@Slf4j
public class AggTaskV1ServiceImpl extends ServiceImpl<AggTaskV1Mapper, AggTaskV1>
    implements AggTaskV1Service {

  @Autowired
  private AggTaskV1Converter aggTaskV1Converter;

  @Override
  public List<AggTaskV1DTO> findByRefId(String refId) {

    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("ref_id", refId);
    columnMap.put("deleted", 0);
    return aggTaskV1Converter.dosToDTOs(listByMap(columnMap));
  }

  public AggTaskV1DTO create(AggTaskV1DTO aggTaskV1DTO) {
    aggTaskV1DTO.setGmtCreate(new Date());
    aggTaskV1DTO.setGmtModified(new Date());
    AggTaskV1 aggTaskV1 = aggTaskV1Converter.dtoToDO(aggTaskV1DTO);

    save(aggTaskV1);
    return aggTaskV1Converter.doToDTO(aggTaskV1);
  }

  @Override
  public Long updateDeleted(String aggId) {
    // 查询db里 deleted=0 的配置
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("agg_id", aggId);
    columnMap.put("deleted", 0);

    List<AggTaskV1> byRefId = listByMap(columnMap);

    if (CollectionUtils.isEmpty(byRefId)) {
      // 如果db 不存在 TableName，
      return null;
    }
    for (AggTaskV1 aggTaskV1 : byRefId) {
      aggTaskV1.setDeleted(1);
      aggTaskV1.setGmtModified(new Date());
      saveOrUpdate(aggTaskV1);
    }

    return byRefId.get(0).getId();
  }

  @Override
  public void updateDeleted(Long id) {
    AggTaskV1 byId = getById(id);

    if (null == byId) {
      // 如果db 不存在，
      return;
    }

    byId.setDeleted(1);
    byId.setGmtModified(new Date());
    saveOrUpdate(byId);
  }

  @Override
  public AggTaskV1DTO upsert(AggTaskV1DTO aggTaskV1DTO) {
    // 查询db里 deleted=0 的配置
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("agg_id", aggTaskV1DTO.getAggId());
    columnMap.put("deleted", 0);

    List<AggTaskV1> byTableName = listByMap(columnMap);

    if (CollectionUtils.isEmpty(byTableName)) {
      // 如果db 不存在 TableName，则新增
      return create(aggTaskV1DTO);
    }

    // db 里面 agg_id+version 是组合唯一键
    // 判断是否有更新，如果没有更新则跳过
    // 如果有更新，先将 old 配置置为 deleted=1，然后重新创建一条新配置
    // md5 比对
    AggTaskV1 dbConfig = byTableName.get(0);
    if (MD5Hash.getMD5(J.toJson(aggTaskV1DTO.getJson()))
        .equalsIgnoreCase(MD5Hash.getMD5(dbConfig.getJson()))) {
      log.info("{}-{} md5 is not update, continue", dbConfig.getId(), dbConfig.getAggId());
      return null;
    }

    // 先软删除
    updateDeleted(dbConfig.getId());

    // 然后新增
    aggTaskV1DTO.setVersion(dbConfig.getVersion() + 1);
    return create(aggTaskV1DTO);
  }
}
