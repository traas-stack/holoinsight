/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * UtilMisc class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: UtilMisc.java, v 0.1 2022年04月24日 12:36 下午 jinsong.yjs Exp $
 */
@Slf4j
public class UtilMisc {
  private static Random seed = new Random();

  // 切分List
  /**
   * <p>
   * divideList.
   * </p>
   */
  public static <T> List<List<T>> divideList(List<T> listInput, Integer num) {
    List<List<T>> res = new ArrayList<List<T>>();
    if (CollectionUtils.isEmpty(listInput)) {
      return res;
    }
    if (num <= 0) {
      res.add(listInput);
      return res;
    }
    List<T> temp = new ArrayList<T>();
    int count = 0;
    for (T t : listInput) {
      temp.add(t);
      count++;
      if (count >= num) {
        res.add(temp);
        temp = new ArrayList<T>();
        count = 0;
      }
    }
    if (!CollectionUtils.isEmpty(temp)) {
      res.add(temp);
    }
    return res;
  }

  /**
   * 获取0到max-1的随机数
   *
   * @param max
   */
  public static int getRandom(int max) {
    return Math.abs(seed.nextInt() % max);
  }

  public static long getInterval(String downsample) {
    long interval = 60000L;
    switch (downsample) {
      case "1m":
        interval = 60000L;
        break;
      case "1s":
        interval = 1000L;
        break;
      case "5s":
        interval = 5000L;
        break;
      case "15s":
        interval = 15000L;
        break;
      case "30s":
        interval = 30000L;
        break;
      case "10m":
        interval = 10 * 60000L;
        break;
      default:
    }
    return interval;
  }

  /**
   * 将inputStream里的数据读取出来并转换成字符串
   */
  public static String inputStream2String(InputStream inputStream) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }

    return sb.toString();
  }

  public static boolean validateWithTimeout(String regex, String input, long timeout) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Boolean> future = executor.submit(() -> {
      try {
        Pattern pattern = Pattern.compile(regex);
        Matcher interruptableMatcher = pattern.matcher(new InterruptibleCharSequence(input));
        interruptableMatcher.find();
      } catch (Exception e) {
        log.warn("parse regex exception, " + e.getMessage(), e);
      }
      return true;
    });
    try {
      future.get(timeout, TimeUnit.MILLISECONDS);
    } catch (TimeoutException timeoutException) {
      future.cancel(true); // 取消任务执行
      log.error("parse regex timeout exception, " + timeoutException.getMessage(),
          timeoutException);
      return false;
    } catch (Exception e) {
      log.error("parse regex exception, " + e.getMessage(), e);
      return false;
    } finally {
      executor.shutdown();
    }
    return true;
  }

  private static class InterruptibleCharSequence implements CharSequence {
    CharSequence inner;

    public InterruptibleCharSequence(CharSequence inner) {
      super();
      this.inner = inner;
    }

    @Override
    public char charAt(int index) {
      if (Thread.currentThread().isInterrupted()) {
        throw new RuntimeException("Interrupted!");
      }
      return inner.charAt(index);
    }

    @Override
    public int length() {
      return inner.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
      return new InterruptibleCharSequence(inner.subSequence(start, end));
    }

    @Override
    public String toString() {
      return inner.toString();
    }
  }
}
