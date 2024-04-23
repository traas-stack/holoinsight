/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.entity.MetaDimData;
import io.holoinsight.server.common.dao.mapper.MetaDimDataMapper;
import io.holoinsight.server.common.service.MetaDimDataService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: MetaDimDataServiceImpl.java, Date: 2024-04-18 Time: 11:57
 */
@Service
public class MetaDimDataServiceImpl extends ServiceImpl<MetaDimDataMapper, MetaDimData>
    implements MetaDimDataService {
  @Override
  public void batchInsertOrUpdate(String tableName, List<MetaDimData> metaDimDatas) {
    if (CollectionUtils.isEmpty(metaDimDatas))
      return;
    Map<String, MetaDimData> metaDataMaps = new HashMap<>();
    metaDimDatas.forEach(metaDimData -> {
      metaDataMaps.put(metaDimData.getUk(), metaDimData);
    });

    Set<String> uks = metaDataMaps.keySet();

    List<MetaDimData> dbMetaDimData = selectByUks(tableName, new ArrayList<>(uks));

    Map<String, MetaDimData> dbMetaDimDataMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(dbMetaDimData)) {
      dbMetaDimData.forEach(d -> {
        dbMetaDimDataMap.put(d.getUk(), d);
      });
    }

    for (Map.Entry<String, MetaDimData> entry : metaDataMaps.entrySet()) {
      if (!dbMetaDimDataMap.containsKey(entry.getKey())) {
        // insert
        this.save(entry.getValue());
        continue;
      }

      // update
      MetaDimData metaDimData = entry.getValue();
      metaDimData.setGmtModified(new Date());
      metaDimData.setId(dbMetaDimDataMap.get(entry.getKey()).getId());
      this.updateById(metaDimData);
    }
  }

  @Override
  public List<MetaDimData> selectByUks(String tableName, Collection<String> pkList) {
    QueryWrapper<MetaDimData> wrapper = new QueryWrapper<>();

    wrapper.eq("deleted", 0);
    wrapper.eq("table_name", tableName);
    wrapper.in("uk", pkList);
    return this.baseMapper.selectList(wrapper);
  }

  @Override
  public Integer softDeleteByUks(String tableName, Collection<String> pkList, Date gmtModified) {

    List<MetaDimData> metaDimDatas = selectByUks(tableName, pkList);
    if (CollectionUtils.isEmpty(metaDimDatas)) {
      return 0;
    }

    metaDimDatas.forEach(metaDimData -> {
      metaDimData.setDeleted(1);
      metaDimData.setGmtModified(gmtModified);
      this.updateById(metaDimData);
    });
    return metaDimDatas.size();
  }

  @Override
  public void updateByUk(String tableName, MetaDimData item) {
    UpdateWrapper<MetaDimData> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("table_name", tableName);
    updateWrapper.eq("uk", item.getUk());
    updateWrapper.eq("deleted", 0);
    updateWrapper.set("json", item.getJson());
    updateWrapper.set("annotations", item.getAnnotations());
    updateWrapper.set("gmt_modified", new Date());
    this.update(item, updateWrapper);
  }

  @Override
  public List<MetaDimData> queryChangedMeta(Date start, Date end, Boolean containDeleted,
      int pateNum, int pageSize) {
    QueryWrapper<MetaDimData> queryWrapper = new QueryWrapper<>();
    queryWrapper.ge("gmt_modified", start);
    queryWrapper.le("gmt_modified", end);
    if (!containDeleted) {
      queryWrapper.eq("deleted", 0);
    }
    queryWrapper.select("id", "gmt_modified", "table_name", "uk", "json", "deleted");

    Page<MetaDimData> page = new Page<>(pateNum, pageSize);
    page = page(page, queryWrapper);
    return page.getRecords();
  }

  @Override
  public List<MetaDimData> queryTableChangedMeta(String table, Date start, Date end,
      Boolean containDeleted, int pateNum, int pageSize) {
    QueryWrapper<MetaDimData> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("table_name", table);
    queryWrapper.ge("gmt_modified", start);
    queryWrapper.le("gmt_modified", end);
    if (!containDeleted) {
      queryWrapper.eq("deleted", 0);
    }
    queryWrapper.select("id", "gmt_modified", "table_name", "uk", "json", "deleted");

    Page<MetaDimData> page = new Page<>(pateNum, pageSize);
    page = page(page, queryWrapper);
    return page.getRecords();
  }

  // @Override
  // public Integer cleanMetaData(Date end) {
  // QueryWrapper<MetaDimData> queryWrapper = new QueryWrapper<>();
  // queryWrapper.le("gmt_modified", end);
  // queryWrapper.select("id");
  //
  // int pageNum = 1;
  // int pageSize = 500;
  // int count = 0;
  // while (true) {
  // Page<MetaDimData> page = new Page<>(pageNum, pageSize);
  // page = page(page, queryWrapper);
  // List<MetaDimData> metaDimDatas = page.getRecords();
  // if (CollectionUtils.isEmpty(metaDimDatas)) {
  // break;
  // }
  // metaDimDatas.forEach(metaDimData -> {
  // this.baseMapper.deleteById(metaDimData.getId());
  // });
  // pageNum++;
  // count += metaDimDatas.size();
  // }
  // return count;
  // }
}
