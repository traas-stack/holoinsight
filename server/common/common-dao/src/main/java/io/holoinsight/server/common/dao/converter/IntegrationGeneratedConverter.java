/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.IntegrationGenerated;
import io.holoinsight.server.common.dao.entity.dto.IntegrationGeneratedDTO;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface IntegrationGeneratedConverter {

  IntegrationGeneratedDTO doToDTO(IntegrationGenerated integrationGenerated);

  IntegrationGenerated dtoToDO(IntegrationGeneratedDTO integrationGeneratedDTO);

  List<IntegrationGeneratedDTO> dosToDTOs(Iterable<IntegrationGenerated> integrationGenerates);

}
