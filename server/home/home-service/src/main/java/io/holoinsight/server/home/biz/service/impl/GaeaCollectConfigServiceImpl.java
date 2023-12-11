/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.home.biz.service.GaeaCollectConfigService;
import io.holoinsight.server.home.dal.converter.GaeaCollectConfigConverter;
import io.holoinsight.server.home.dal.mapper.GaeaCollectConfigMapper;
import io.holoinsight.server.home.dal.model.GaeaCollectConfig;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaCollectConfigServiceImpl.java, v 0.1 2022年11月03日 下午9:32 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class GaeaCollectConfigServiceImpl extends
    ServiceImpl<GaeaCollectConfigMapper, GaeaCollectConfig> implements GaeaCollectConfigService {

  private GaeaCollectConfigConverter gaeaCollectConfigConverter =
      Mappers.getMapper(GaeaCollectConfigConverter.class);

  @Override
  public GaeaCollectConfigDTO findById(Long id) {
    return gaeaCollectConfigConverter.doToDTO(getById(id));
  }

  @Override
  public List<GaeaCollectConfigDTO> findByRefId(String refId) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("ref_id", refId);
    columnMap.put("deleted", 0);
    return gaeaCollectConfigConverter.dosToDTOs(listByMap(columnMap));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public GaeaCollectConfigDTO upsert(GaeaCollectConfigDTO gaeaCollectConfigDTO) {
    gaeaCollectConfigDTO.setVersion(System.currentTimeMillis());
    // 查询db里 deleted=0 的配置
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("table_name", gaeaCollectConfigDTO.tableName);
    columnMap.put("deleted", 0);

    List<GaeaCollectConfig> byTableName = listByMap(columnMap);

    if (CollectionUtils.isEmpty(byTableName)) {
      // 如果db 不存在 TableName，则新增
      return create(gaeaCollectConfigDTO);
    }

    // db 里面 tableName+version 是组合唯一键
    // 判断是否有更新，如果没有更新则跳过
    // 如果有更新，先将 old 配置置为 deleted=1，然后重新创建一条新配置
    // md5 比对

    GaeaCollectConfig dbConfig = byTableName.get(0);
    if (equelByMd5(gaeaCollectConfigConverter.dtoToDO(gaeaCollectConfigDTO), dbConfig)) {
      log.info("{}-{} md5 is not update, continue", dbConfig.id, dbConfig.tableName);
      return null;
    }
    dbConfig.setGmtModified(new Date());
    updateDeleted(dbConfig.id);

    return create(gaeaCollectConfigDTO);
  }

  private boolean equelByMd5(GaeaCollectConfig source, GaeaCollectConfig db) {

    if (!source.deleted.equals(db.deleted)) {
      return false;
    }

    if (!source.tenant.equalsIgnoreCase(db.tenant)) {
      return false;
    }

    if (!MD5Hash.getMD5(source.json).equalsIgnoreCase(MD5Hash.getMD5(db.json))) {
      return false;
    }

    if (!MD5Hash.getMD5(source.collectRange).equalsIgnoreCase(MD5Hash.getMD5(db.collectRange))) {
      return false;
    }

    if (!MD5Hash.getMD5(source.executorSelector)
        .equalsIgnoreCase(MD5Hash.getMD5(db.executorSelector))) {
      return false;
    }
    return true;
  }

  @Override
  public GaeaCollectConfigDTO create(GaeaCollectConfigDTO gaeaCollectConfigDTO) {
    gaeaCollectConfigDTO.setGmtCreate(new Date());
    gaeaCollectConfigDTO.setGmtModified(new Date());
    GaeaCollectConfig gaeaCollectConfig = gaeaCollectConfigConverter.dtoToDO(gaeaCollectConfigDTO);

    save(gaeaCollectConfig);
    return gaeaCollectConfigConverter.doToDTO(gaeaCollectConfig);
  }

  @Override
  public void deleteById(Long id) {

    GaeaCollectConfigDTO gaeaCollectConfigDTO = findById(id);

    if (null == gaeaCollectConfigDTO) {
      return;
    }
    removeById(id);
  }

  @Override
  public void updateDeleted(Long id) {

    GaeaCollectConfig byId = getById(id);

    if (null == byId) {
      // 如果db 不存在，
      return;
    }

    byId.setDeleted(1);
    byId.setGmtModified(new Date());

    saveOrUpdate(byId);
  }

  @Override
  public Long updateDeleted(String tableName) {
    // 查询db里 deleted=0 的配置
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("table_name", tableName);
    columnMap.put("deleted", 0);

    List<GaeaCollectConfig> byRefId = listByMap(columnMap);

    if (CollectionUtils.isEmpty(byRefId)) {
      // 如果db 不存在 TableName，
      return null;
    }
    for (GaeaCollectConfig collectConfig : byRefId) {
      collectConfig.setDeleted(1);
      collectConfig.setGmtModified(new Date());

      saveOrUpdate(collectConfig);
    }

    return byRefId.get(0).id;
  }

  @Override
  public Long updateDeletedByRefId(String refId) {
    // 查询db里 deleted=0 的配置
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("ref_id", refId);
    columnMap.put("deleted", 0);

    List<GaeaCollectConfig> byRefId = listByMap(columnMap);

    if (CollectionUtils.isEmpty(byRefId)) {
      // 如果db 不存在 TableName，
      return null;
    }

    for (GaeaCollectConfig collectConfig : byRefId) {
      collectConfig.setDeleted(1);
      collectConfig.setGmtModified(new Date());

      saveOrUpdate(collectConfig);
    }

    return byRefId.get(0).id;
  }

  @Override
  public GaeaCollectConfigDTO update(GaeaCollectConfigDTO gaeaCollectConfigDTO) {
    gaeaCollectConfigDTO.setGmtModified(new Date());

    GaeaCollectConfig gaeaCollectConfig = gaeaCollectConfigConverter.dtoToDO(gaeaCollectConfigDTO);
    saveOrUpdate(gaeaCollectConfig);

    return gaeaCollectConfigConverter.doToDTO(gaeaCollectConfig);
  }
}
