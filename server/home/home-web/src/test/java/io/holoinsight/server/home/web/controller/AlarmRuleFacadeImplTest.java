/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.biz.service.AlertGroupService;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.AlertSubscribeService;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.AlarmRuleConverterImpl;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-03-14 21:29:00
 */
public class AlarmRuleFacadeImplTest {

  AlarmRuleFacadeImpl facade;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);

    facade = new AlarmRuleFacadeImpl();
    facade.alarmGroupService = Mockito.mock(AlertGroupService.class);
    facade.alarmSubscribeService = Mockito.mock(AlertSubscribeService.class);
    facade.alarmRuleService = Mockito.mock(AlertRuleService.class);
    facade.alarmRuleConverter = new AlarmRuleConverterImpl();
    MonitorUser mu = new MonitorUser();
    mu.setLoginName("test_loginName");
    mu.setUserId("test_userId");
    MonitorScope ms = new MonitorScope();
    ms.tenant = "test_tenant";
    RequestContext.Context context = new RequestContext.Context(ms, mu, null);
    RequestContext.setContext(context);

    AlarmGroupDTO alarmGroupDTO = new AlarmGroupDTO();
    alarmGroupDTO.setId(111L);
    AlarmSubscribeInfo alarmSubscribeInfo = new AlarmSubscribeInfo();
    alarmSubscribeInfo.setUniqueId("rule_22222");

    AlarmRule alarmRule = new AlarmRule();
    alarmRule.setId(22222L);

    QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
    queryWrapper.in("id", Collections.singletonList("22222"));
    queryWrapper.and(
        wrapper -> wrapper.eq("creator", mu.getLoginName()).or().eq("modifier", mu.getLoginName()));

    Mockito.when(facade.alarmGroupService.getListByUserLike("test_userId", "test_tenant"))
        .thenReturn(Collections.singletonList(alarmGroupDTO));
    Mockito.when(facade.alarmSubscribeService.queryByMap(Mockito.any()))
        .thenReturn(Collections.singletonList(alarmSubscribeInfo));
    Mockito.when(facade.alarmRuleService.list(Mockito.any()))
        .thenReturn(Collections.singletonList(alarmRule));
  }

  @Captor
  ArgumentCaptor<QueryWrapper<AlarmRule>> argument;

  @Test
  public void testGetRuleListByGroup() {
    List<AlarmRuleDTO> alarmRuleDTOList = facade.getRuleListByGroup(true);

    Mockito.verify(facade.alarmRuleService).list(argument.capture());
    QueryWrapper<AlarmRule> queryWrapper = argument.getValue();
    Assert.assertEquals(
        "(id IN (#{ew.paramNameValuePairs.MPGENVAL1}) AND (creator = #{ew.paramNameValuePairs.MPGENVAL2} OR modifier = #{ew.paramNameValuePairs.MPGENVAL3}))",
        queryWrapper.getExpression().getSqlSegment());

    Assert.assertFalse(CollectionUtils.isEmpty(alarmRuleDTOList));

    AlarmRuleDTO alarmRuleDTO = alarmRuleDTOList.get(0);
    Assert.assertEquals(alarmRuleDTO.getId().longValue(), 22222L);
  }

  @Test
  public void testGetRuleListByGroup_false() {
    facade.getRuleListByGroup(false);
    Mockito.verify(facade.alarmRuleService).list(argument.capture());
    QueryWrapper<AlarmRule> queryWrapper = argument.getValue();
    Assert.assertEquals("(id IN (#{ew.paramNameValuePairs.MPGENVAL1}))",
        queryWrapper.getExpression().getSqlSegment());
  }

  @Test
  public void testGetRuleListBySubscribe() {
    List<AlarmRuleDTO> alarmRuleDTOList = facade.getRuleListBySubscribe(true);
    Mockito.verify(facade.alarmRuleService).list(argument.capture());
    QueryWrapper<AlarmRule> queryWrapper = argument.getValue();
    Assert.assertEquals(
        "(id IN (#{ew.paramNameValuePairs.MPGENVAL1}) AND (creator = #{ew.paramNameValuePairs.MPGENVAL2} OR modifier = #{ew.paramNameValuePairs.MPGENVAL3}))",
        queryWrapper.getExpression().getSqlSegment());

    Assert.assertFalse(CollectionUtils.isEmpty(alarmRuleDTOList));

    AlarmRuleDTO alarmRuleDTO = alarmRuleDTOList.get(0);
    Assert.assertEquals(alarmRuleDTO.getId().longValue(), 22222L);
  }

  @Test
  public void testGetRuleListBySubscribe_false() {
    List<AlarmRuleDTO> alarmRuleDTOList = facade.getRuleListBySubscribe(false);
    Mockito.verify(facade.alarmRuleService).list(argument.capture());
    QueryWrapper<AlarmRule> queryWrapper = argument.getValue();
    Assert.assertEquals("(id IN (#{ew.paramNameValuePairs.MPGENVAL1}))",
        queryWrapper.getExpression().getSqlSegment());
  }
}
