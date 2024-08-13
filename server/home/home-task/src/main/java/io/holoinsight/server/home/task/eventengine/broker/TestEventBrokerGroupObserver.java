/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.broker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: TestEventBrokerGroupObserver.java, Date: 2024-03-14 Time: 15:28
 */
@Slf4j
public class TestEventBrokerGroupObserver implements EventBrokerGroupObserver {

  private EventBrokerGroupCallback groupChangeCallback;

  @Override
  public void watch(EventBrokerGroupCallback groupChangeCallback) {
    this.groupChangeCallback = groupChangeCallback;

    // EventBrokerObserver observer = new EventBrokerObserver();
    //
    // String localHostAddress = AddressUtil.getHostAddress();
    // String subscriberName = this.getClass().getCanonicalName() + "@" + localHostAddress;
    // String dataId = EventBrokerGroupConstant.EVENT_BROKER_GROUP_PREFIX
    // + EventBrokerGroupConstant.EVENT_BROKER_GROUP_IDENTITY;

    // todo 实现订阅

  }


  private class EventBrokerObserver {

    public void handleData(String dataId, Object data) {
      String brokerGroupDataId = EventBrokerGroupConstant.EVENT_BROKER_GROUP_PREFIX
          + EventBrokerGroupConstant.EVENT_BROKER_GROUP_IDENTITY;

      if (StringUtils.isEmpty(dataId) || !dataId.equals(brokerGroupDataId)) {
        return;
      }

      Set<String> brokerServerAddresses = new HashSet<String>();

      @SuppressWarnings("unchecked")
      Map<String, List<Object>> targetData = (Map<String, List<Object>>) data;

      for (String region : targetData.keySet()) {

        List<Object> targetList = targetData.get(region);

        for (Object target : targetList) {
          if (target instanceof String) {
            brokerServerAddresses.add((String) target);
          }
        }
      }

      log.info("Fresh group members " + StringUtils.join(brokerServerAddresses.toArray()));

      groupChangeCallback.updateBrokerGroup(brokerServerAddresses);
    }
  }
}
