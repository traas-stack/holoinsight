/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeDTO;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/4/7 2:03 下午
 */
public interface AlertSubscribeService extends IService<AlarmSubscribe> {


  Boolean saveDataBatch(AlarmSubscribeDTO alarmSubscribeDTO, String creator, String tenant,
      String workspace);

  AlarmSubscribeDTO queryByUniqueId(Map<String, Object> columnMap);

  List<AlarmSubscribeInfo> queryByMap(Map<String, Object> columnMap);

  Long save(AlarmSubscribeInfo alarmSubscribeInfo);

  Boolean updateById(AlarmSubscribeInfo alarmSubscribeInfo);
}
