/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.facade;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.facade.AlarmRuleDTO.getApmLink;
import static org.junit.Assert.*;

/**
 * @author masaimu
 * @version 2023-03-14 18:30:00
 */
public class AlarmRuleDTOTest {

  @Test
  public void testGetApmLink() {
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRule(buildRule("test_metric"));
    alarmRuleDTO.setSourceType("apm_appName");
    String domain = "http://testdomain/";
    Map<String /* metric */, Map<String /* type */, String /* page */>> systemMetrics =
        new HashMap<>();
    systemMetrics.put("test_metric", Collections.singletonMap("server", "page"));
    String link = getApmLink(alarmRuleDTO, domain, systemMetrics);
    Assert.assertTrue(StringUtils.equals("http://testdomain/page&app=appName", link));

    alarmRuleDTO.setRule(buildRule("fake_test_metric"));
    link = getApmLink(alarmRuleDTO, domain, systemMetrics);
    Assert.assertTrue(StringUtils.isEmpty(link));
  }

  private Map<String, Object> buildRule(String metric) {
    Rule rule = new Rule();
    rule.setTriggers(Arrays.asList(new Trigger()));
    Trigger trigger = rule.getTriggers().get(0);
    trigger.setDatasources(Arrays.asList(new DataSource()));
    DataSource dataSource = trigger.getDatasources().get(0);
    dataSource.setMetric(metric);
    return J.toMap(J.toJson(rule));
  }
}
