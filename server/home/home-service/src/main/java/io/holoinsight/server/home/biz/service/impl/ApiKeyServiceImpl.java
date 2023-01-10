/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.dal.mapper.ApiKeyMapper;
import io.holoinsight.server.home.dal.model.ApiKey;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * @author jsy1001de
 * @version 1.0: ApiKeyServiceImpl.java, v 0.1 2022年05月31日 11:27 上午 jinsong.yjs Exp $
 */
@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey> implements ApiKeyService {
  @Override
  public ApiKey queryById(Long id, String tenant) {
    QueryWrapper<ApiKey> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    return this.getOne(wrapper);
  }
}
