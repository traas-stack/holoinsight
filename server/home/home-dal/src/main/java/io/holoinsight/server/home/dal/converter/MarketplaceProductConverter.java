/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import java.util.List;

import org.mapstruct.Mapper;

import io.holoinsight.server.home.dal.model.MarketplaceProduct;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import io.holoinsight.server.home.dal.transformer.FormJsonMapper;
import io.holoinsight.server.home.dal.transformer.MetricJsonMapper;

@Mapper(componentModel = "spring", uses = {MetricJsonMapper.class, FormJsonMapper.class})
public interface MarketplaceProductConverter {

  MarketplaceProductDTO doToDTO(MarketplaceProduct MarketplaceProduct);

  MarketplaceProduct dtoToDO(MarketplaceProductDTO MarketplaceProductDTO);

  List<MarketplaceProductDTO> dosToDTOs(Iterable<MarketplaceProduct> MarketplaceProducts);

}
