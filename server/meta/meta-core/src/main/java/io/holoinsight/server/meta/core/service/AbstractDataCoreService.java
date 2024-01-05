/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import io.holoinsight.server.meta.dal.service.MetaTableService;
import io.holoinsight.server.common.MD5Hash;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.meta.common.util.ConstModel.default_pk;
import static io.holoinsight.server.meta.common.util.ConstModel.default_type;

/**
 *
 * @author jsy1001de
 * @version 1.0: AbstractDataCoreService.java, v 0.1 2022年08月11日 9:44 上午 jinsong.yjs Exp $
 */
public abstract class AbstractDataCoreService implements DBCoreService {

  public static final Logger logger = LoggerFactory.getLogger(AbstractDataCoreService.class);

  @Autowired
  private MetaTableService metaTableService;

  @Override
  public void startBuildIndex() {

  }

  public List<Map<String, Object>> addUkValues(String tableName, List<Map<String, Object>> rows) {

    List<Map<String, Object>> filterRows = new ArrayList<>();
    Map<String, List<String>> ukMaps = metaTableService.getUksForCache(tableName);

    for (Map<String, Object> row : rows) {

      if (row.containsKey(default_pk)) {
        filterRows.add(row);
        continue;
      }

      // 所有元数据都需要 关键字 _type
      if (!row.containsKey(default_type)) {
        logger.info("[addUkValues] has not _type, table={}, row={}.", tableName, row.toString());
        continue;
      }

      List<String> uks = ukMaps.getOrDefault(row.get(default_type).toString().toLowerCase(),
          Collections.singletonList("_uk"));

      StringBuilder ukValue = new StringBuilder();
      for (String uk : uks) {
        if (null == row.get(uk) || StringUtils.isBlank(row.get(uk).toString())) {
          continue;
        }
        ukValue.append(row.get(uk));
      }
      if (StringUtils.isBlank(ukValue)) {
        continue;
      }

      row.put(default_pk, MD5Hash.getMD5(ukValue.toString()));
      filterRows.add(row);
    }

    return filterRows;
  }

  public List<String> getUks(String tableName, List<Map<String, Object>> rows) {

    Map<String, List<String>> ukMaps = metaTableService.getUksForCache(tableName);

    List<String> uniqueKeys = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      if (row.containsKey(default_pk) && null != row.get(default_pk)
          && StringUtils.isNotBlank(row.get(default_pk).toString())) {
        uniqueKeys.add(row.get(default_pk).toString());
        continue;
      }

      // 所有元数据都需要 关键字 _type
      if (!row.containsKey(default_type)) {
        logger.info("[getUks] has not _type, table={}, row={}.", tableName, row.toString());
        continue;
      }

      List<String> uks = ukMaps.getOrDefault(row.get(default_type).toString().toLowerCase(),
          Collections.singletonList("_uk"));

      StringBuilder ukValue = new StringBuilder();
      for (String uk : uks) {
        if (null == row.get(uk) || StringUtils.isBlank(row.get(uk).toString())) {
          continue;
        }
        ukValue.append(row.get(uk));
      }
      if (StringUtils.isBlank(ukValue)) {
        continue;
      }
      uniqueKeys.add(MD5Hash.getMD5(ukValue.toString()));
    }

    return uniqueKeys;
  }
}
