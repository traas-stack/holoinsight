/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.biz.service.UserinfoService;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.UserinfoConverter;
import io.holoinsight.server.home.dal.mapper.UserinfoMapper;
import io.holoinsight.server.home.dal.model.Userinfo;
import io.holoinsight.server.home.facade.UserinfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-21 15:10:00
 */
@Service
public class UserinfoServiceImpl implements UserinfoService {

  @Resource
  private UserinfoMapper userinfoMapper;
  @Autowired
  private UserinfoConverter userinfoConverter;

  @Override
  public UserinfoDTO queryByUid(String uid) {
    if (StringUtils.isBlank(uid)) {
      return null;
    }
    MonitorScope ms = RequestContext.getContext().ms;
    if (ms == null || StringUtils.isEmpty(ms.tenant)) {
      return null;
    }


    QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("uid", uid);
    queryWrapper.eq("tenant", ms.getTenant());
    List<Userinfo> userinfoList = this.userinfoMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(userinfoList)) {
      return null;
    }

    return this.userinfoConverter.doToDTO(userinfoList.get(0));
  }

  @Override
  public Map<String, UserinfoDTO> queryByUid(List<String> uidList) {
    MonitorScope ms = RequestContext.getContext().ms;
    if (ms == null || StringUtils.isEmpty(ms.tenant)) {
      return Collections.emptyMap();
    }
    QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("tenant", ms.getTenant());
    queryWrapper.in("uid", uidList);
    List<Userinfo> userinfoList = this.userinfoMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(userinfoList)) {
      return Collections.emptyMap();
    }
    Map<String, UserinfoDTO> list = new HashMap<>();
    for (Userinfo userinfo : userinfoList) {
      list.put(userinfo.getUid(), this.userinfoConverter.doToDTO(userinfo));
    }
    return list;
  }
}
