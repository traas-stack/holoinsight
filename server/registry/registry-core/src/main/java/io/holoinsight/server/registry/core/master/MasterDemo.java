/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.master;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.common.threadpool.CommonThreadPools;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/8/29
 *
 * @author xzchaoo
 */
// @Component
@Slf4j
public class MasterDemo {
  @Autowired
  private MasterService masterService;
  @Autowired
  private CommonThreadPools commonThreadPools;

  @PostConstruct
  public void start() {
    MasterListener listener1 = new MasterListener() {
      @Override
      public void onChange(MasterJson oldId, MasterJson newId) {
        log.info("[master] 1 onChange old=[{}] new=[{}]", oldId, newId);
      }

      @Override
      public void onEnter(MasterJson id) {
        log.info("[master] 1 onEnter id=[{}]", id);
      }

      @Override
      public void onLeave(MasterJson id) {
        log.info("[master] 1 onLeave id=[{}]", id);
      }
    };
    MasterService.RegisterRecord rr1 =
        masterService.register("aaa", "bbb", "111", "111", listener1);

    MasterService.RegisterRecord rr2 =
        masterService.register("aaa", "bbb", "222", "222", new MasterListener() {
          @Override
          public void onChange(MasterJson oldMj, MasterJson newMj) {
            log.info("[master] 2 onChange old=[{}] new=[{}]", oldMj, newMj);
          }

          @Override
          public void onEnter(MasterJson mj) {
            log.info("[master] 2 onEnter mj=[{}]", mj);
          }

          @Override
          public void onLeave(MasterJson mj) {
            log.info("[master] 2 onLeave id=[{}]", mj);
          }
        });


    commonThreadPools.getScheduler().schedule(() -> {
      log.info("[master] i unregister 1");
      masterService.unregister(rr1);
    }, 12, TimeUnit.SECONDS);

    commonThreadPools.getScheduler().schedule(() -> {
      masterService.register("aaa", "bbb", "111", "111", listener1);
    }, 18, TimeUnit.SECONDS);


    commonThreadPools.getScheduler().schedule(() -> {
      log.info("[master] i unregister 2");
      masterService.unregister(rr2);
    }, 26, TimeUnit.SECONDS);

  }
}
