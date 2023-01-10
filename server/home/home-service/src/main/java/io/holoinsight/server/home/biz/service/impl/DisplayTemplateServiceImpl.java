/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.DisplayTemplateService;
import io.holoinsight.server.home.dal.converter.DisplayTemplateConverter;
import io.holoinsight.server.home.dal.mapper.DisplayTemplateMapper;
import io.holoinsight.server.home.dal.model.DisplayTemplate;
import io.holoinsight.server.home.dal.model.dto.DisplayTemplateDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayTemplateServiceImpl.java, v 0.1 2022年12月06日 上午10:31 jinsong.yjs Exp $
 */
@Service
public class DisplayTemplateServiceImpl extends ServiceImpl<DisplayTemplateMapper, DisplayTemplate>
    implements DisplayTemplateService {

  @Autowired
  private DisplayTemplateConverter displayTemplateConverter;

  @Override
  public DisplayTemplateDTO queryById(Long id) {
    return displayTemplateConverter.doToDTO(getById(id));
  }

  @Override
  public DisplayTemplateDTO queryById(Long id, String tenant) {
    QueryWrapper<DisplayTemplate> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    return displayTemplateConverter.doToDTO(this.getOne(wrapper));
  }

  @Override
  public List<DisplayTemplateDTO> findByMap(Map<String, Object> columnMap) {
    return displayTemplateConverter.dosToDTOs(listByMap(columnMap));
  }
}
