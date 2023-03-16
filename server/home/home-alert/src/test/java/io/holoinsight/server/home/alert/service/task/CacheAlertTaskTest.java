/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.service.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * @author masaimu
 * @version 2023-03-16 11:29:00
 */
public class CacheAlertTaskTest {

  @Test
  public void testSetMethod() {
    CacheAlertTask cacheAlertTask = new CacheAlertTask();

    cacheAlertTask.setAiPageNum(10);
    Assert.assertEquals(cacheAlertTask.aiPageNum.get(), 10);
    cacheAlertTask.setAiPageSize(11);
    Assert.assertEquals(cacheAlertTask.aiPageSize.get(), 11);

    cacheAlertTask.setRulePageNum(12);
    Assert.assertEquals(cacheAlertTask.rulePageNum.get(), 12);
    cacheAlertTask.setRulePageSize(13);
    Assert.assertEquals(cacheAlertTask.rulePageSize.get(), 13);

    cacheAlertTask.setPqlPageNum(14);
    Assert.assertEquals(cacheAlertTask.pqlPageNum.get(), 14);
    cacheAlertTask.setPqlPageSize(15);
    Assert.assertEquals(cacheAlertTask.pqlPageSize.get(), 15);
  }

  @Captor
  ArgumentCaptor<QueryWrapper<AlarmRule>> ruleArgument;

  @Test
  public void testGetAlarmRuleListByPage() {
    MockitoAnnotations.openMocks(this);
    CacheAlertTask cacheAlertTask = new CacheAlertTask();

    cacheAlertTask.setAiPageNum(10);
    cacheAlertTask.setAiPageSize(11);

    cacheAlertTask.setRulePageNum(12);
    cacheAlertTask.setRulePageSize(13);

    cacheAlertTask.setPqlPageNum(14);
    cacheAlertTask.setPqlPageSize(15);

    cacheAlertTask.alarmRuleDOMapper = Mockito.mock(AlarmRuleMapper.class);
    cacheAlertTask.getAlarmRuleListByPage();
    Mockito.verify(cacheAlertTask.alarmRuleDOMapper, Mockito.times(3))
        .selectList(ruleArgument.capture());

    List<QueryWrapper<AlarmRule>> list = ruleArgument.getAllValues();

    QueryWrapper<AlarmRule> rule = list.get(0);
    QueryWrapper<AlarmRule> ai = list.get(1);
    QueryWrapper<AlarmRule> pql = list.get(2);

    Assert.assertEquals("rule",
        "WHERE (rule_type = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY id DESC limit 13 offset 12",
        rule.getCustomSqlSegment());
    Assert.assertEquals("ai",
        "WHERE (rule_type = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY id DESC limit 11 offset 10",
        ai.getCustomSqlSegment());
    Assert.assertEquals("pql",
        "WHERE (rule_type = #{ew.paramNameValuePairs.MPGENVAL1}) ORDER BY id DESC limit 15 offset 14",
        pql.getCustomSqlSegment());
  }
}
