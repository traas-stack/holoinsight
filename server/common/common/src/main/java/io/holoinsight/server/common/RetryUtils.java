/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.util.List;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;

/**
 * 重试工具类
 *
 * @author xzchaoo
 * @version 1.0: RetryUtils.java, v 0.1 2022年02月24日 5:15 下午 jinsong.yjs Exp $
 */
@Slf4j
public abstract class RetryUtils {
  /**
   * 重试调度方法
   *
   * @param dataSupplier 返回数据方法执行体
   * @param canRetry can retry predicate
   * @param retryCount 重试次数
   * @param sleepTime 重试间隔睡眠时间(注意：阻塞当前线程)
   * @param expectedExceptions 期待异常(抛出符合相应异常时候重试),空或者空容器默认进行重试 当匹配到异常，此时返回为空null
   * @param <R> 数据类型
   * @return R 当最后一次失败，返回异常
   */
  public static <R> R invoke(Supplier<R> dataSupplier, Predicate<Throwable> canRetry,
      int retryCount, long sleepTime, List<Class<? extends Throwable>> expectedExceptions) {

    // 匹配期待异常或者允许任何异常重试
    Throwable lastEx = null;
    for (int i = 0; i < retryCount; i++) {
      try {
        // i>0: only sleep when retry
        if (i > 0 && sleepTime > 0) {
          Thread.sleep(sleepTime);
        }
        return dataSupplier.get();
      } catch (InterruptedException e) {
        log.error("thread interrupted !! break retry", e);
        // 恢复中断信号
        Thread.currentThread().interrupt();
        // 线程中断直接退出重试
        break;
      } catch (Exception e) {
        // If e matches expectedExceptions return null result.
        if (expectedExceptions != null && !expectedExceptions.isEmpty()) {
          Class<? extends Throwable> exClass = e.getClass();
          boolean match = expectedExceptions.stream().anyMatch(clazz -> clazz == exClass);
          if (!match) {
            return null;
          }
        }

        try {
          if (canRetry == null || canRetry.test(e)) {
            lastEx = e;
          } else {
            wrapAndThrows(e);
          }
        } catch (RuntimeException testE) {
          log.error("testPredicate error", testE);
        }
      }
    }

    wrapAndThrows(lastEx);
    return null;
  }

  /**
   * Wrap e as {@link RuntimeException} and throw it.
   * 
   * @param e
   */
  private static void wrapAndThrows(Throwable e) {
    if (e == null) {
      throw new RuntimeException("thread interrupted");
    }
    if (e instanceof RuntimeException) {
      throw (RuntimeException) e;
    }

    throw new RuntimeException(e);
  }

  /**
   * 函数式接口可以抛出异常
   *
   * @param <T>
   */
  @FunctionalInterface
  public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     *
     * @throws Exception 错误时候抛出异常
     */
    T get() throws Exception;
  }
}
