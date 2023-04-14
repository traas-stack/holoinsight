/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-07 11:33
 */
public interface LogService {

  /**
   * sometimes get log count for page query
   * 
   * @param request
   * @return
   */
  LogCountResponse count(LogCountRequest request);

  /**
   * multi apps query
   * 
   * @param request
   * @return
   */
  LogMultiQueryResponse mutiQuery(LogMultiQueryRequest request);

  /**
   * get log paths for one app
   * 
   * @param request
   * @return
   */
  List<String> path(LogPathRequest request);

  /**
   * context query
   * 
   * @param request
   * @return
   */
  LogContextResponse contextQuery(LogContextRequest request);

  /**
   * one app query
   * 
   * @param request
   * @return
   */
  LogQueryResponse query(LogQueryRequest request);

  /**
   * get log index config
   * 
   * @return
   */
  Map<String, Object> getIndexConfig(LogKey key);

  /**
   * get log agent config
   * 
   * @param key
   * @return
   */
  Map<String, Object> getLogAgentConfig(LogKey key);

  /**
   * get log storage config
   * 
   * @param key
   * @return
   */
  Map<String, Object> getStorageConfig(LogKey key);

  /**
   * config log agent
   * 
   * @param key
   * @param config
   * @return
   */
  Boolean configAgent(LogKey key, Map<String, Object> config);

  /**
   * config log storage
   * 
   * @param key
   * @param config
   * @return
   */
  Boolean configStorage(LogKey key, Map<String, Object> config);

  /**
   * config log index
   * 
   * @param key
   * @param config
   * @return
   */
  Boolean configIndex(LogKey key, Map<String, Object> config);

}
