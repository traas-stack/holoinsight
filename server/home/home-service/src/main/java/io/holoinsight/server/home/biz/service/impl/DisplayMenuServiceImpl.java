/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.DisplayMenuService;
import io.holoinsight.server.home.dal.converter.DisplayMenuConverter;
import io.holoinsight.server.home.dal.mapper.DisplayMenuMapper;
import io.holoinsight.server.home.dal.model.DisplayMenu;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuServiceImpl.java, v 0.1 2022年12月06日 上午10:31 jinsong.yjs Exp $
 */
@Service
public class DisplayMenuServiceImpl extends ServiceImpl<DisplayMenuMapper, DisplayMenu>
    implements DisplayMenuService {

  @Autowired
  private DisplayMenuConverter displayMenuConverter;

  @Override
  public DisplayMenuDTO queryById(Long id, String tenant) {
    QueryWrapper<DisplayMenu> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");

    return displayMenuConverter.doToDTO(this.getOne(wrapper));
  }

  @Override
  public List<DisplayMenuDTO> findByMap(Map<String, Object> columnMap) {
    return displayMenuConverter.dosToDTOs(listByMap(columnMap));
  }
}
