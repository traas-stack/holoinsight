/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.DisplayMenu;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuDTO;
import io.holoinsight.server.home.dal.transformer.DisplayMenuConfigMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuConverter.java, v 0.1 2022年12月06日 上午10:52 jinsong.yjs Exp $
 */
@Mapper(componentModel = "spring", uses = {DisplayMenuConfigMapper.class})
public interface DisplayMenuConverter {
  DisplayMenuDTO doToDTO(DisplayMenu displayMenu);

  DisplayMenu dtoToDO(DisplayMenuDTO displayMenuDTO);

  List<DisplayMenuDTO> dosToDTOs(Iterable<DisplayMenu> displayMenus);

}
