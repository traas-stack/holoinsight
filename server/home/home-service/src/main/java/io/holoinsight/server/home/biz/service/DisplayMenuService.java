/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.DisplayMenu;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuDTO;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuService.java, v 0.1 2022年12月06日 上午10:31 jinsong.yjs Exp $
 */
public interface DisplayMenuService extends IService<DisplayMenu> {

  DisplayMenuDTO queryById(Long id, String tenant);

  List<DisplayMenuDTO> findByMap(Map<String, Object> columnMap);
}
