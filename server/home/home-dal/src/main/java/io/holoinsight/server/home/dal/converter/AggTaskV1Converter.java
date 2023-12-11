/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AggTaskV1;
import io.holoinsight.server.home.dal.model.dto.AggTaskV1DTO;
import io.holoinsight.server.home.dal.transformer.AggTaskV1ConfigMapper;
import io.holoinsight.server.home.dal.transformer.BooleanIntegerMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AggTaskV1Converter.java, Date: 2023-12-06 Time: 15:42
 */
@Mapper(componentModel = "spring", uses = {BooleanIntegerMapper.class, AggTaskV1ConfigMapper.class})
public interface AggTaskV1Converter {
  AggTaskV1DTO doToDTO(AggTaskV1 aggTaskV1);

  AggTaskV1 dtoToDO(AggTaskV1DTO aggTaskV1DTO);

  List<AggTaskV1DTO> dosToDTOs(Iterable<AggTaskV1> aggTaskV1s);
}
