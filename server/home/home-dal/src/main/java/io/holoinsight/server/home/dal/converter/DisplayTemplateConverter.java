/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.DisplayTemplate;
import io.holoinsight.server.home.dal.model.dto.DisplayTemplateDTO;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;


/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayTemplateConverter.java, v 0.1 2022年12月06日 上午10:52 jinsong.yjs Exp $
 */
@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface DisplayTemplateConverter {
  DisplayTemplateDTO doToDTO(DisplayTemplate displayTemplate);

  DisplayTemplate dtoToDO(DisplayTemplateDTO displayTemplateDTO);

  List<DisplayTemplateDTO> dosToDTOs(Iterable<DisplayTemplate> displayTemplates);

}
