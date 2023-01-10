/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.DisplayTemplate;
import io.holoinsight.server.home.dal.model.dto.DisplayTemplateDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayDisplayService.java, v 0.1 2022年12月06日 上午10:31 jinsong.yjs Exp $
 */
public interface DisplayTemplateService extends IService<DisplayTemplate> {
  DisplayTemplateDTO queryById(Long id);

  DisplayTemplateDTO queryById(Long id, String tenant);

  List<DisplayTemplateDTO> findByMap(Map<String, Object> columnMap);

}
