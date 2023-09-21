/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.event.EventInfo;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.InspectConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author masaimu
 * @version 2023-04-04 17:35:00
 */
public class AlertTaskComputeTest {
  AlertTaskCompute alertTaskCompute = new AlertTaskCompute();

  @Before
  public void setUp() throws Exception {
    AlarmHistoryMapper alertHistoryDOMapper = Mockito.mock(AlarmHistoryMapper.class);
    AbstractUniformInspectRunningRule abstractUniformInspectRunningRule =
        Mockito.mock(AbstractUniformInspectRunningRule.class);
    List<AlertNotifyRecordDTO> alertNotifyRecordDTOList = new ArrayList<>();
    Mockito.when(abstractUniformInspectRunningRule.eval(Mockito.any(), alertNotifyRecordDTOList))
        .thenReturn(new EventInfo());
    alertTaskCompute.abstractUniformInspectRunningRule = abstractUniformInspectRunningRule;
    alertTaskCompute.alertHistoryDOMapper = alertHistoryDOMapper;
  }

  @Test
  public void testConcurrency() {
    ComputeTaskPackage computeTaskPackage = new ComputeTaskPackage();
    computeTaskPackage.setInspectConfigs(new ArrayList<>());
    for (int i = 0; i < 100; i++) {
      InspectConfig inspectConfig = new InspectConfig();
      computeTaskPackage.inspectConfigs.add(inspectConfig);
    }
    AtomicInteger count = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);
    for (int j = 0; j < 1000; j++) {
      try {
        List<EventInfo> eventInfos = alertTaskCompute.calculate(computeTaskPackage);
        for (EventInfo eventInfo : eventInfos) {
          if (eventInfo == null) {
            count.incrementAndGet();
          }
        }
      } catch (Throwable t) {
        errorCount.incrementAndGet();
      }
    }
    assertEquals(0, count.get());
    assertEquals(0, errorCount.get());
  }
}
