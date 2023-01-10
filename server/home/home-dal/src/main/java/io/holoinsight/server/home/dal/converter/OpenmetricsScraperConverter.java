/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.OpenmetricsScraper;
import io.holoinsight.server.home.dal.model.dto.OpenmetricsScraperDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OpenmetricsScraperConverter {

    OpenmetricsScraperDTO doToDTO(OpenmetricsScraper model);

    OpenmetricsScraper dtoToDO(OpenmetricsScraperDTO dto);

    List<OpenmetricsScraperDTO> dosToDTOs(Iterable<OpenmetricsScraper> models);

}
