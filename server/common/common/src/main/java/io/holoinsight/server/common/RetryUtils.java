/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

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
     * @param dataSupplier
     *      返回数据方法执行体
     * @param exceptionCaught
     *      出错异常处理(包括第一次执行和重试错误)
     * @param retryCount
     *      重试次数
     * @param sleepTime
     *      重试间隔睡眠时间(注意：阻塞当前线程)
     * @param expectExceptions
     *      期待异常(抛出符合相应异常时候重试),空或者空容器默认进行重试
     *      当匹配到异常，此时返回为空null
     * @param <R>
     *      数据类型
     * @return R 当最后一次失败，返回异常
     */
    public static <R> R invoke(Supplier<R> dataSupplier, Consumer<Throwable> exceptionCaught, int retryCount, long sleepTime,
                               List<Class<? extends Throwable>> expectExceptions) {

        // 匹配期待异常或者允许任何异常重试
        Throwable ex = null;
        for (int i = 0; i < retryCount; i++) {
            try {
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
                return dataSupplier.get();
            } catch (InterruptedException e) {
                log.error("thread interrupted !! break retry", e);
                // 恢复中断信号
                Thread.currentThread().interrupt();
                // 线程中断直接退出重试
                break;
            } catch (Throwable throwable) {
                catchException(exceptionCaught, throwable);
                ex = throwable;
            }

            if (expectExceptions != null && !expectExceptions.isEmpty()) {
                // 校验异常是否匹配期待异常
                Class<? extends Throwable> exClass = ex.getClass();
                boolean match = expectExceptions.stream().anyMatch(clazz -> clazz == exClass);
                if (!match) {
                    return null;
                }
            }
        }

        throw new RuntimeException(ex);
    }

    private static void catchException(Consumer<Throwable> exceptionCaught, Throwable throwable) {
        try {
            if (exceptionCaught != null) {
                exceptionCaught.accept(throwable);
            }
        } catch (Throwable e) {
            log.error("retry exception caught throw error, ", e);
        }
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
