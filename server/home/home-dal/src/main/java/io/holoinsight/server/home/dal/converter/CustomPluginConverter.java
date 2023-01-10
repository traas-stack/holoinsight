/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.transformer.CustomPluginConfMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomPluginConfMapper.class})
public interface CustomPluginConverter {

  CustomPluginDTO doToDTO(CustomPlugin customPlugin);

  CustomPlugin dtoToDO(CustomPluginDTO customPluginDTO);

  List<CustomPluginDTO> dosToDTOs(Iterable<CustomPlugin> customPlugins);

}
