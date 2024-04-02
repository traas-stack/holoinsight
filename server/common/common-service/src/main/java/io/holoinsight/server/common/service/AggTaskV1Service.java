/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.AggTaskV1;
import io.holoinsight.server.common.dao.entity.dto.AggTaskV1DTO;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AggTaskV1Service.java, Date: 2023-12-06 Time: 15:37
 */
public interface AggTaskV1Service extends IService<AggTaskV1> {

  List<AggTaskV1DTO> findByRefId(String refId);

  AggTaskV1DTO create(AggTaskV1DTO aggTaskV1DTO);

  Long updateDeleted(String aggId);

  void updateDeleted(Long id);

  AggTaskV1DTO upsert(AggTaskV1DTO aggTaskV1DTO);
}
