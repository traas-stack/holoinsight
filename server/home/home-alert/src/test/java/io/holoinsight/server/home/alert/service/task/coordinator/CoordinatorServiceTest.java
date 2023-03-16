/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.service.task.coordinator;

import io.holoinsight.server.home.alert.service.task.CacheAlertTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-03-16 10:33:00
 */
public class CoordinatorServiceTest {

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
  }

  @Captor
  ArgumentCaptor<Integer> rulePageSizeArgument;
  @Captor
  ArgumentCaptor<Integer> rulePageNumArgument;
  @Captor
  ArgumentCaptor<Integer> aiPageSizeArgument;
  @Captor
  ArgumentCaptor<Integer> aiPageNumArgument;
  @Captor
  ArgumentCaptor<Integer> pqlPageSizeArgument;
  @Captor
  ArgumentCaptor<Integer> pqlPageNumArgument;

  @Test
  public void testCalculateSelectRange() {
    CoordinatorService service = new CoordinatorService();
    service.orderMap = Mockito.mock(OrderMap.class);
    Mockito.when(service.orderMap.getRealSize()).thenReturn(3);
    service.cacheAlertTask = Mockito.mock(CacheAlertTask.class);
    Mockito.when(service.cacheAlertTask.ruleSize("ai")).thenReturn(73);
    Mockito.when(service.cacheAlertTask.ruleSize("rule")).thenReturn(43);
    Mockito.when(service.cacheAlertTask.ruleSize("pql")).thenReturn(1);

    order(service);
  }

  private void order(CoordinatorService service) {
    service.calculateSelectRange(0);
    service.calculateSelectRange(1);
    service.calculateSelectRange(2);
    Mockito.verify(service.cacheAlertTask, Mockito.times(3))
        .setRulePageNum(rulePageNumArgument.capture());
    List<Integer> rulePageNums = rulePageNumArgument.getAllValues();
    Assert.assertEquals(rulePageNums, Arrays.asList(0, 15, 30));

    Mockito.verify(service.cacheAlertTask, Mockito.times(3))
        .setRulePageSize(rulePageSizeArgument.capture());
    List<Integer> rulePageSize = rulePageSizeArgument.getAllValues();
    Assert.assertEquals(rulePageSize, Arrays.asList(15, 15, 15));

    Mockito.verify(service.cacheAlertTask, Mockito.times(3))
        .setAiPageNum(aiPageNumArgument.capture());
    List<Integer> aiPageNum = aiPageNumArgument.getAllValues();
    Assert.assertEquals(aiPageNum, Arrays.asList(0, 25, 50));

    Mockito.verify(service.cacheAlertTask, Mockito.times(3))
        .setAiPageSize(aiPageSizeArgument.capture());
    List<Integer> aiPageSize = aiPageSizeArgument.getAllValues();
    Assert.assertEquals(aiPageSize, Arrays.asList(25, 25, 25));

    Mockito.verify(service.cacheAlertTask, Mockito.times(3))
        .setPqlPageNum(pqlPageNumArgument.capture());
    List<Integer> pqlPageNum = pqlPageNumArgument.getAllValues();
    Assert.assertEquals(pqlPageNum, Arrays.asList(0, 1, 2));

    Mockito.verify(service.cacheAlertTask, Mockito.times(3))
        .setPqlPageSize(pqlPageSizeArgument.capture());
    List<Integer> pqlPageSize = pqlPageSizeArgument.getAllValues();
    Assert.assertEquals(pqlPageSize, Arrays.asList(1, 1, 1));
  }
}
