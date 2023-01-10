/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * MultiFutureTask class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: MultiFutureTask.java, v 0.1 2022年04月28日 4:42 下午 jinsong.yjs Exp $
 */
public class MultiFutureTask {

  static final class Delayer {
    static final ScheduledThreadPoolExecutor delayer;

    static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
      return delayer.schedule(command, delay, unit);
    }

    static final class DaemonThreadFactory implements ThreadFactory {
      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("CompletableFutureDelayScheduler");
        return t;
      }
    }

    // 注意，这里使用一个线程就可以搞定 因为这个线程并不真的执行请求 而是仅仅抛出一个异常
    static {
      (delayer = new ScheduledThreadPoolExecutor(1, new Delayer.DaemonThreadFactory()))
          .setRemoveOnCancelPolicy(true);
    }
  }

  /**
   * <p>
   * timeoutAfter.
   * </p>
   */
  public static <V> CompletableFuture<V> timeoutAfter(long timeout, TimeUnit unit) {
    CompletableFuture<V> result = new CompletableFuture<V>();
    // timeout 时间后 抛出TimeoutException 类似于sentinel / watcher
    Delayer.delayer.schedule(
        () -> result.completeExceptionally(new TimeoutException("query time out")), timeout, unit);
    return result;
  }

  /**
   * 并发任务，返回顺序和入参数一致
   *
   * @param params
   * @param function
   * @param exceptionFunction
   * @param executor
   * @param <T>
   * @param <V>
   */
  public static <T, V> List<V> load(List<T> params, Function<T, V> function,
      Function<Pair<T, Throwable>, V> exceptionFunction, ExecutorService executor) {
    List<CompletableFuture<V>> listOfFutures = params.stream()
        .map(param -> loadData(param, function, exceptionFunction, executor)).collect(toList());

    CompletableFuture<List<V>> futureList = sequence(listOfFutures);
    return futureList.join();
  }

  /**
   * 并发调用，控制超时时间
   *
   * @param params
   * @param function
   * @param exceptionFunction
   * @param executor
   * @param timeout
   * @param unit
   * @param <T>
   * @param <V>
   */
  public static <T, V> List<V> loadTimeout(List<T> params, Function<T, V> function,
      Function<Pair<T, Throwable>, V> exceptionFunction, ExecutorService executor, long timeout,
      TimeUnit unit) {

    List<CompletableFuture<V>> listOfFutures = params.stream()
        .map(param -> loadDataTimeout(param, function, exceptionFunction, executor, timeout, unit))
        .collect(toList());
    CompletableFuture<List<V>> futureList = sequence(listOfFutures);
    return futureList.join();
  }

  /**
   * <p>
   * loadData.
   * </p>
   */
  public static <T, V> CompletableFuture<V> loadData(T param, Function<T, V> function,
      Function<Pair<T, Throwable>, V> exceptionFunction, Executor executor) {
    return CompletableFuture.supplyAsync(() -> function.apply(param), executor)
        .exceptionally((throwable -> exceptionFunction.apply(new Pair<>(param, throwable))));
  }

  /**
   * <p>
   * loadDataTimeout.
   * </p>
   */
  public static <T, V> CompletableFuture<V> loadDataTimeout(T param, Function<T, V> function,
      Function<Pair<T, Throwable>, V> exceptionFunction, Executor executor, long timeout,
      TimeUnit unit) {
    final CompletableFuture<V> timeoutFuture = timeoutAfter(timeout, unit);
    return CompletableFuture.supplyAsync(() -> function.apply(param), executor)
        .applyToEither(timeoutFuture, Function.identity())
        .exceptionally(throwable -> exceptionFunction.apply(new Pair<>(param, throwable)));
  }

  private static <V> CompletableFuture<List<V>> sequence(List<CompletableFuture<V>> listOfFutures) {
    CompletableFuture<List<V>> identity = CompletableFuture.completedFuture(new ArrayList<>());

    BiFunction<CompletableFuture<List<V>>, CompletableFuture<V>, CompletableFuture<List<V>>> accumulator =
        (futureList, futureValue) -> futureValue.thenCombine(futureList, (value, list) -> {
          List<V> newList = new ArrayList<>(list.size() + 1);
          newList.addAll(list);
          newList.add(value);
          return newList;
        });

    BinaryOperator<CompletableFuture<List<V>>> combiner =
        (futureList1, futureList2) -> futureList1.thenCombine(futureList2, (list1, list2) -> {
          List<V> newList = new ArrayList<>(list1.size() + list2.size());
          newList.addAll(list1);
          newList.addAll(list2);
          return newList;
        });

    return listOfFutures.stream().reduce(identity, accumulator, combiner);
  }

}
