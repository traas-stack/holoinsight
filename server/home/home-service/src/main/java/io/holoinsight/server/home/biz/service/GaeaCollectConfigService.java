/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.GaeaCollectConfig;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: GaeaCollectConfigService.java, v 0.1 2022年03月31日 8:41 下午 jinsong.yjs Exp $
 */

public interface GaeaCollectConfigService extends IService<GaeaCollectConfig> {

  GaeaCollectConfigDTO findById(Long id);

  List<GaeaCollectConfigDTO> findByRefId(String refId);

  GaeaCollectConfigDTO upsert(GaeaCollectConfigDTO gaeaCollectConfigDTO);

  GaeaCollectConfigDTO create(GaeaCollectConfigDTO gaeaCollectConfigDTO);

  void deleteById(Long id);

  void updateDeleted(Long id);

  Long updateDeleted(String tableName);

  Long updateDeletedByRefId(String refId);

  GaeaCollectConfigDTO update(GaeaCollectConfigDTO gaeaCollectConfigDTO);
}
