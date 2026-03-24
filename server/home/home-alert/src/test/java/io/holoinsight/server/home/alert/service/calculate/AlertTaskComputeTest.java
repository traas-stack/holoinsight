/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.event.EventInfo;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.common.dao.mapper.AlarmHistoryMapper;
import io.holoinsight.server.common.dao.entity.dto.InspectConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author masaimu
 * @version 2023-04-04 17:35:00
 */
public class AlertTaskComputeTest {
  AlertTaskCompute alertTaskCompute = new AlertTaskCompute();
  MockedStatic<SpringContext> mockedSpringContext;
  MockedStatic<RecordSucOrFailNotify> mockedRecordNotify;

  @Before
  public void setUp() throws Exception {
    // Mock SpringContext first so that RecordSucOrFailNotify's static initializer
    // (which calls SpringContext.getBean()) does not throw NPE during class loading.
    ApplicationContext mockAppCtx = Mockito.mock(ApplicationContext.class);
    mockedSpringContext = Mockito.mockStatic(SpringContext.class);
    mockedSpringContext.when(SpringContext::getContext).thenReturn(mockAppCtx);
    mockedSpringContext.when(() -> SpringContext.getBean(Mockito.any())).thenReturn(null);

    // Now it is safe to mock the static methods of RecordSucOrFailNotify
    mockedRecordNotify = Mockito.mockStatic(RecordSucOrFailNotify.class);

    AlarmHistoryMapper alertHistoryDOMapper = Mockito.mock(AlarmHistoryMapper.class);
    Mockito.when(alertHistoryDOMapper.selectList(Mockito.any()))
        .thenReturn(Collections.emptyList());

    AbstractUniformInspectRunningRule abstractUniformInspectRunningRule =
        Mockito.mock(AbstractUniformInspectRunningRule.class);
    Mockito.when(abstractUniformInspectRunningRule.eval(Mockito.any(), Mockito.anyList()))
        .thenReturn(new EventInfo());
    alertTaskCompute.abstractUniformInspectRunningRule = abstractUniformInspectRunningRule;
    alertTaskCompute.alertHistoryDOMapper = alertHistoryDOMapper;
  }

  @After
  public void tearDown() {
    if (mockedRecordNotify != null) {
      mockedRecordNotify.close();
    }
    if (mockedSpringContext != null) {
      mockedSpringContext.close();
    }
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
