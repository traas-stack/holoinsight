/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.IntegrationPlugin;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.CollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.transformer.GaeaCollectRangeMapper;
import io.holoinsight.server.home.dal.transformer.JsonObjectMapper;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = {JsonObjectMapper.class, MapJsonMapper.class, GaeaCollectRangeMapper.class})
public interface IntegrationPluginConverter {

  IntegrationPluginDTO doToDTO(IntegrationPlugin integrationPlugin);

  IntegrationPlugin dtoToDO(IntegrationPluginDTO integrationPluginDTO);

  List<IntegrationPluginDTO> dosToDTOs(Iterable<IntegrationPlugin> integrationPlugins);

  default String map(CollectRange value) {
    if (value == null) {
      return StringUtils.EMPTY;
    }
    return J.toJson(value);
  }

}
