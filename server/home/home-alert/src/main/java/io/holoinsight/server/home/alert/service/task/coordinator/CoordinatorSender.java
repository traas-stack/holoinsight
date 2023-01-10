/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import com.alibaba.fastjson.JSONObject;
import io.holoinsight.server.common.AddressUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2022-10-18 17:45:00
 */
public class CoordinatorSender {
  private static Logger LOGGER = LoggerFactory.getLogger(CoordinatorSender.class);

  private String ip = AddressUtil.getLocalHostIPV4();
  private List<String> otherMembers;
  private String order;
  private String periodId;
  private CoordinatorService coordinatorService;
  private OrderMap orderMap;

  public CoordinatorSender(List<String> otherMembers, String order, String periodId,
      CoordinatorService coordinatorService, OrderMap orderMap) {
    this.otherMembers = otherMembers;
    this.order = order;
    this.periodId = periodId;
    this.coordinatorService = coordinatorService;
    this.orderMap = orderMap;
  }

  public void sendOrder(long heartbeat) {
    try {
      refreshClient();
      spreadGossip(buildPreOrderMsg(heartbeat));
      coordinatorService.putOrderingMap(ip, order);
      List orderedMap = coordinatorService.getSortedOrderedMap();
      spreadGossip(buildDistribution(orderedMap));
    } catch (Exception e) {
      LOGGER.error("fail to send order, for {}", e.getMessage(), e);
    }
  }

  private void refreshClient() {
    if (CollectionUtils.isEmpty(otherMembers)) {
      return;
    }
    for (String ip : otherMembers) {
      boolean exist = orderMap.existActive(ip);
      if (exist) {
        continue;
      }
      try {
        orderMap.addNewClient(ip);
        LOGGER.info("add client {} to orderMap", ip);
      } catch (Exception e) {
        LOGGER.error("fail to start netty client {} for {}", ip, e.getMessage(), e);
      }
    }
    orderMap.rmInactiveClient();
  }

  private String buildDistribution(List orderedMap) {
    String data;
    if (CollectionUtils.isEmpty(orderedMap)) {
      data = "[]";
    } else {
      data = JSONObject.toJSONString(orderedMap);
    }
    return String.join("__", "ORDER", ip, data, "\r\n");
  }

  private void spreadGossip(String msg) {
    for (Map.Entry<String, ChannelFuture> entry : orderMap.channelMap.entrySet()) {
      String ip = entry.getKey();
      ChannelFuture channel = entry.getValue();
      channel.channel().writeAndFlush(Unpooled.wrappedBuffer(msg.getBytes()))
          .addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
              if (future.isSuccess()) {
                LOGGER.info("spread gossip msg {} to {}", msg, ip);
              } else {
                LOGGER.info("fail to spread gossip msg {} to {} for {}", msg, ip,
                    future.cause().getMessage());
              }
            }
          });
    }
  }


  private String buildPreOrderMsg(long heartbeat) {
    return String.join("__", "PREORDER", periodId, String.valueOf(heartbeat), ip,
        String.valueOf(order), "\r\n");
  }
}
