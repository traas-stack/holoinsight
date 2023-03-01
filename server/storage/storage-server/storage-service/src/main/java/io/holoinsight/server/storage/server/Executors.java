/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.storage.server;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author xiangwanpeng
 * @version : Executors.java, v 0.1 2023年02月28日 18:45 xiangwanpeng Exp $
 */
public class Executors {

  public static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
          new BasicThreadFactory.Builder().namingPattern("tatris-%d").build());
}
