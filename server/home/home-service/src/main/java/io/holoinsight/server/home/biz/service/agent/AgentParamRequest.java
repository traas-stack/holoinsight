/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.agent;

import lombok.Data;

import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentParamRequest.java, v 0.1 2022年07月26日 6:12 下午 jinsong.yjs Exp $
 */
@Data
public class AgentParamRequest {
  String ip;
  String hostname;
  String app;
  Map<String, String> label;
  String logpath;
}
