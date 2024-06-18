/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import java.util.List;

import io.holoinsight.server.common.dao.transformer.IntegrationFormJsonMapper;
import org.mapstruct.Mapper;

import io.holoinsight.server.common.dao.entity.MarketplaceProduct;
import io.holoinsight.server.common.dao.entity.dto.MarketplaceProductDTO;
import io.holoinsight.server.common.dao.transformer.MetricJsonMapper;

@Mapper(componentModel = "spring", uses = {MetricJsonMapper.class, IntegrationFormJsonMapper.class})
public interface MarketplaceProductConverter {

  MarketplaceProductDTO doToDTO(MarketplaceProduct MarketplaceProduct);

  MarketplaceProduct dtoToDO(MarketplaceProductDTO MarketplaceProductDTO);

  List<MarketplaceProductDTO> dosToDTOs(Iterable<MarketplaceProduct> MarketplaceProducts);

}
