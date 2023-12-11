/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.IntegrationProduct;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.dal.transformer.FormJsonMapper;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import io.holoinsight.server.home.dal.transformer.MetricJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = {MetricJsonMapper.class, MapJsonMapper.class, FormJsonMapper.class})
public interface IntegrationProductConverter {

  IntegrationProductDTO doToDTO(IntegrationProduct IntegrationProduct);

  IntegrationProduct dtoToDO(IntegrationProductDTO IntegrationProductDTO);

  List<IntegrationProductDTO> dosToDTOs(Iterable<IntegrationProduct> IntegrationProducts);

}
