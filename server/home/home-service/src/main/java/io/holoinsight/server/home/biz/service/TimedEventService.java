/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.TimedEvent;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: TimedEventService.java, Date: 2024-03-14 Time: 14:38
 */
public interface TimedEventService extends IService<TimedEvent> {

  List<TimedEvent> selectPendingWardEvents(String guardianServer);

  TimedEvent seletForUpdate(Long id);
}
