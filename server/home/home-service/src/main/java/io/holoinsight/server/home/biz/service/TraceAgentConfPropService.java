/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.TraceAgentConfProp;

import java.util.List;

public interface TraceAgentConfPropService extends IService<TraceAgentConfProp> {

  List<TraceAgentConfProp> get(String type, String language);

}
