/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.google.common.collect.Maps;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaDictValueService.java, v 0.1 2022年03月21日 8:20 下午 jinsong.yjs Exp $
 */
public class MetaDictValueService {
  @Autowired
  private MetaDataDictValueService metaDataDictValueService;

  public Map<String /* type */, Map<String /* k */, MetaDataDictValue>> getMetaDictValue() {

    Map<String /* type */, Map<String /* k */, MetaDataDictValue>> mapMap = Maps.newHashMap();
    List<MetaDataDictValue> metaDataDictValues = metaDataDictValueService.list();

    metaDataDictValues.forEach(metaDataDictValue -> {
      // 当type/key/version为空的时候，过滤掉该数据
      if (null == metaDataDictValue.getVersion() || null == metaDataDictValue.getType()
          || null == metaDataDictValue.getDictValue()) {
        return;
      }
      if (!mapMap.containsKey(metaDataDictValue.getType())) {
        Map<String /* k */, MetaDataDictValue> kMap = Maps.newHashMap();
        kMap.put(metaDataDictValue.getDictKey(), metaDataDictValue);
        mapMap.put(metaDataDictValue.getType(), kMap);
      } else {
        Map<String, MetaDataDictValue> newKMap = mapMap.get(metaDataDictValue.getType());
        if (!newKMap.containsKey(metaDataDictValue.getDictKey())) {
          newKMap.put(metaDataDictValue.getDictKey(), metaDataDictValue);
          mapMap.put(metaDataDictValue.getType(), newKMap);
        } else {
          // 取 version 最大的
          if (newKMap.get(metaDataDictValue.getDictKey()).getVersion() < metaDataDictValue
              .getVersion()) {
            newKMap.put(metaDataDictValue.getDictKey(), metaDataDictValue);
          }
          mapMap.put(metaDataDictValue.getType(), newKMap);
        }
      }
    });

    return mapMap;
  }

  public MetaDataDictValue getById(Long id) {
    return metaDataDictValueService.getById(id);
  }

  public MetaDataDictValue create(MetaDataDictValue metaDataDictValue) {
    metaDataDictValue.setGmtCreate(new Date());
    metaDataDictValue.setGmtModified(new Date());
    metaDataDictValueService.save(metaDataDictValue);
    return metaDataDictValue;
  }

  public MetaDataDictValue update(MetaDataDictValue metaDataDictValue) {
    metaDataDictValue.setGmtModified(new Date());
    metaDataDictValueService.saveOrUpdate(metaDataDictValue);
    return metaDataDictValue;
  }

  public void updateById(MetaDataDictValue metaDataDictValue) {
    metaDataDictValueService.saveOrUpdate(metaDataDictValue);
  }

  public void deleteById(Long id) {
    metaDataDictValueService.removeById(id);
  }

}
