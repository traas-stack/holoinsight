/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.holoinsight.server.home.alert.service.task.coordinator.OrderMap.curPeriod;


/**
 * @author masaimu
 * @version 2022-10-19 15:01:00
 */
public class CoordinatorReceiver extends ChannelInboundHandlerAdapter {
  private static Logger LOGGER = LoggerFactory.getLogger(CoordinatorReceiver.class);
  CoordinatorService service;

  public CoordinatorReceiver(CoordinatorService service) {
    this.service = service;
  }

  // 接收到新的数据
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    parseMsg(msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    LOGGER.info("channelActive");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    LOGGER.info("channelInactive");
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    LOGGER.error("catch exception in coordinator receive for {}", cause.getMessage(), cause);
  }


  public void parseMsg(Object msgObj) {
    try {
      if (!(msgObj instanceof String)) {
        return;
      }
      String msg = String.valueOf(msgObj);
      if (StringUtils.isEmpty(msg)) {
        return;
      }
      LOGGER.info("receive spread msg {}", msg);
      if (msg.startsWith("PREORDER")) {
        String[] msgArr = msg.split("__");
        parsePreOrder(msgArr);
      } else if (msg.startsWith("ORDER")) {
        String[] msgArr = msg.split("__", 3);
        parseOrder(msgArr);
      }
    } catch (Exception e) {
      LOGGER.error("fail to parse msg for {}", e.getMessage(), e);
    }
  }

  private void parseOrder(String[] msgArr) {
    if (msgArr.length < 3) {
      throw new RuntimeException("invalid msg length " + msgArr.length);
    }
    String ip = msgArr[1];
    String data = msgArr[2];
    service.count(data);
    service.distribute(ip, data);
  }

  private void parsePreOrder(String[] msgArr) {
    if (msgArr.length < 5) {
      throw new RuntimeException("invalid msg length " + msgArr.length);
    }
    Long heartbeat = Long.valueOf(msgArr[2]);
    long curPeriod = curPeriod();

    if (heartbeat < curPeriod) {
      // 过期报数，作废，不处理
      return;
    }

    String ip = msgArr[3];
    String order = msgArr[4];
    service.putOrderingMap(ip, order);
  }
}
