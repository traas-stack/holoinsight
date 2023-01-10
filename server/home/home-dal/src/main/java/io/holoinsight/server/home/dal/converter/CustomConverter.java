/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.Cluster;
import io.holoinsight.server.home.dal.model.dto.ClusterDTO;
import io.holoinsight.server.home.dal.transformer.BooleanIntegerMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BooleanIntegerMapper.class})
public interface CustomConverter {

  ClusterDTO doToDTO(Cluster cluster);

  Cluster dtoToDO(ClusterDTO clusterDTO);

  List<ClusterDTO> dosToDTOs(Iterable<Cluster> clusters);

}
