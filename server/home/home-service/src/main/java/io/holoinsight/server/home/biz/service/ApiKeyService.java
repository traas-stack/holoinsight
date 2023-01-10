/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.ApiKey;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author jsy1001de
 * @version 1.0: ApiKeyService.java, v 0.1 2022年05月31日 11:27 上午 jinsong.yjs Exp $
 */
public interface ApiKeyService extends IService<ApiKey> {
  ApiKey queryById(Long id, String tenant);

}
