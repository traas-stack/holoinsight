/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;

import static io.holoinsight.server.common.service.FuseProtector.CRITICAL_AlertSaveHistoryHandler;
import static io.holoinsight.server.common.service.FuseProtector.CRITICAL_AlertTaskCompute;
import static io.holoinsight.server.common.service.FuseProtector.CRITICAL_AlertTaskScheduler;
import static io.holoinsight.server.common.service.FuseProtector.NORMAL_NotifyChain;

/**
 * @author masaimu
 * @version 2023-03-15 15:54:00
 */
public class FuseProtectorTest {

  @After
  public void tearDown() throws Exception {
    FuseProtector.clean();
  }

  @Test
  public void testVoteCriticalError() {
    FuseProtector.voteCriticalError(CRITICAL_AlertSaveHistoryHandler, "test critical");
    Exception exception = null;
    try {
      FuseProtector.doAssert();
    } catch (Exception e) {
      exception = e;
    }
    Assert.notNull(exception, "can not be null");
    Assert.isInstanceOf(HoloInsightCriticalException.class, exception);
    Assert.isTrue(StringUtils.equals("test critical", exception.getMessage()), "exception equal");
  }

  @Test
  public void testVoteNormalError() {
    FuseProtector.voteNormalError(NORMAL_NotifyChain, "test normal");
    Exception exception = null;
    try {
      FuseProtector.doAssert();
    } catch (Exception e) {
      exception = e;
    }
    Assert.isNull(exception, "should be null");

    for (int i = 0; i < 10; i++) {
      FuseProtector.voteNormalError(NORMAL_NotifyChain, "test normal");
    }
    try {
      FuseProtector.doAssert();
    } catch (Exception e) {
      exception = e;
    }
    Assert.notNull(exception, "can not be null");
    Assert.isInstanceOf(HoloInsightCriticalException.class, exception);
    Assert.isTrue(StringUtils.equals("test normal", exception.getMessage()), "exception equal");
  }

  @Test
  public void testVoteComplete() {
    Assert.isTrue(
        !FuseProtector
            .doAssert(Arrays.asList(CRITICAL_AlertTaskCompute, CRITICAL_AlertTaskScheduler)),
        "should be pending");

    FuseProtector.voteComplete(CRITICAL_AlertTaskCompute);
    Assert.isTrue(
        !FuseProtector
            .doAssert(Arrays.asList(CRITICAL_AlertTaskCompute, CRITICAL_AlertTaskScheduler)),
        "should be pending");

    FuseProtector.voteComplete(CRITICAL_AlertTaskScheduler);
    Assert.isTrue(
        FuseProtector
            .doAssert(Arrays.asList(CRITICAL_AlertTaskCompute, CRITICAL_AlertTaskScheduler)),
        "should complete");
  }

  @Test
  public void testClean() {
    FuseProtector.voteNormalError(NORMAL_NotifyChain, "test normal");
    FuseProtector.voteComplete(CRITICAL_AlertTaskCompute);
    FuseProtector.voteCriticalError(CRITICAL_AlertSaveHistoryHandler, "test critical");
    FuseProtector.clean();
    Assert.isTrue(CollectionUtils.isEmpty(FuseProtector.criticalEventMap), "should be cleaned");
    Assert.isTrue(CollectionUtils.isEmpty(FuseProtector.normalEventMap), "should be cleaned");
    Assert.isTrue(CollectionUtils.isEmpty(FuseProtector.completeEventMap), "should be cleaned");

  }
}
