/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event.element;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: diag spi factory
 * @author: jianyu.wl
 * @date: 2021/2/4 9:25 下午
 * @version: 1.0
 */
public class ElementSpiServiceFactory {

  private static Map<String, IElementHandler> serviceMap = new ConcurrentHashMap<>();

  public static IElementHandler getServiceByType(String spiType) {
    return serviceMap.get(spiType);
  }

  public static void register(String spiType, IElementHandler iElementHandler) {
    Assert.notNull(spiType, "spiType can't be null");
    serviceMap.put(spiType, iElementHandler);
  }
}
