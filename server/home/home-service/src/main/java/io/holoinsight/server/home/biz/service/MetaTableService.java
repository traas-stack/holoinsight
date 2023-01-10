/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.MetaTable;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableService.java, v 0.1 2022年03月22日 11:37 上午 jinsong.yjs Exp $
 */
public interface MetaTableService extends IService<MetaTable> {

  MetaTableDTO queryById(Long id, String tenant);

  List<MetaTableDTO> findByTenant(String tenant);

  List<MetaTableDTO> findAll();

  List<MetaTableDTO> findByName(String name);

  MetaTableDTO create(MetaTableDTO metaTableDTO);

  void deleteById(Long id);

  MetaTableDTO update(MetaTableDTO metaTableDTO);

  MetaTableDTO insertOrUpate(MetaTableDTO metaTableDTO);

}
