/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import java.util.List;

import io.holoinsight.server.common.dao.transformer.JsonObjectMapper;
import org.mapstruct.Mapper;

import io.holoinsight.server.common.dao.entity.MarketplacePlugin;
import io.holoinsight.server.common.dao.entity.dto.MarketplacePluginDTO;

@Mapper(componentModel = "spring", uses = {JsonObjectMapper.class})
public interface MarketplacePluginConverter {

  MarketplacePluginDTO doToDTO(MarketplacePlugin integrationPlugin);

  MarketplacePlugin dtoToDO(MarketplacePluginDTO integrationPluginDTO);

  List<MarketplacePluginDTO> dosToDTOs(Iterable<MarketplacePlugin> integrationPlugins);

}
