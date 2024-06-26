/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.IntegrationProduct;
import io.holoinsight.server.common.dao.entity.dto.IntegrationProductDTO;
import io.holoinsight.server.common.dao.transformer.IntegrationFormJsonMapper;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import io.holoinsight.server.common.dao.transformer.MetricJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = {MetricJsonMapper.class, MapJsonMapper.class, IntegrationFormJsonMapper.class})
public interface IntegrationProductConverter {

  IntegrationProductDTO doToDTO(IntegrationProduct IntegrationProduct);

  IntegrationProduct dtoToDO(IntegrationProductDTO IntegrationProductDTO);

  List<IntegrationProductDTO> dosToDTOs(Iterable<IntegrationProduct> IntegrationProducts);

}
