/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.TraceAgentConfPropService;
import io.holoinsight.server.home.dal.mapper.TraceAgentConfPropMapper;
import io.holoinsight.server.home.dal.model.TraceAgentConfProp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author sw1136562366
 * @version 1.0: TraceAgentConfPropServiceImpl.java, v 0.1 2023年08月17日 3:17 下午 sw1136562366 Exp $
 */
@Service
public class TraceAgentConfPropServiceImpl extends
    ServiceImpl<TraceAgentConfPropMapper, TraceAgentConfProp> implements TraceAgentConfPropService {

  @Override
  public List<TraceAgentConfProp> get(String type, String language) {
    QueryWrapper<TraceAgentConfProp> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", type);
    queryWrapper.eq("language", language);

    return list(queryWrapper);
  }
}
