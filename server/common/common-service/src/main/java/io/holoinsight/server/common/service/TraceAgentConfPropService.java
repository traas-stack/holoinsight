/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.TraceAgentConfProp;

import java.util.List;

public interface TraceAgentConfPropService extends IService<TraceAgentConfProp> {

  List<TraceAgentConfProp> get(String type, String language);

}
