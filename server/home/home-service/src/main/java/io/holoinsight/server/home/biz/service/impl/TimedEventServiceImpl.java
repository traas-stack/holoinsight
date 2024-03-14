/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.TimedEventService;
import io.holoinsight.server.home.dal.mapper.TimedEventMapper;
import io.holoinsight.server.home.dal.model.TimedEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: TimedEventService.java, Date: 2024-03-14 Time: 14:38
 */
@Service
public class TimedEventServiceImpl extends ServiceImpl<TimedEventMapper, TimedEvent>
    implements TimedEventService {

  @Override
  public List<TimedEvent> selectPendingWardEvents(String guardianServer) {
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("guardian_server", guardianServer);
    columnMap.put("status", "NEW");
    return listByMap(columnMap);
  }

  @Override
  public TimedEvent seletForUpdate(Long id) {
    QueryWrapper<TimedEvent> wrapper = new QueryWrapper<>();
    wrapper.eq("id", id);
    wrapper.last("for update");


    return this.baseMapper.selectOne(wrapper);
  }
}
