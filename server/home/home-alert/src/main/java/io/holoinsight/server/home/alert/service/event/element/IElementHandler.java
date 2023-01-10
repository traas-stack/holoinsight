/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event.element;


import java.util.Map;

/**
 * @description:
 * @author: jianyu.wl
 * @date: 2021/2/23 10:27 上午
 * @version: 1.0
 */
public interface IElementHandler {

  String handler(String element, Map<String, String> requestMap);
}
