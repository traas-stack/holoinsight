/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import java.util.List;

import org.mapstruct.Mapper;

import io.holoinsight.server.home.dal.model.MarketplacePlugin;
import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.dal.transformer.JsonObjectMapper;

@Mapper(componentModel = "spring", uses = {JsonObjectMapper.class})
public interface MarketplacePluginConverter {

  MarketplacePluginDTO doToDTO(MarketplacePlugin integrationPlugin);

  MarketplacePlugin dtoToDO(MarketplacePluginDTO integrationPluginDTO);

  List<MarketplacePluginDTO> dosToDTOs(Iterable<MarketplacePlugin> integrationPlugins);

}
