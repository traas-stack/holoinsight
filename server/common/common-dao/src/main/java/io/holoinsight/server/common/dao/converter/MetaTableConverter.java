/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.MetaTable;
import io.holoinsight.server.common.dao.entity.dto.MetaTableDTO;
import io.holoinsight.server.common.dao.transformer.MetaTableConfigMapper;
import io.holoinsight.server.common.dao.transformer.MetaTableSchemaMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = {MetaTableSchemaMapper.class, MetaTableConfigMapper.class})
public interface MetaTableConverter {
  MetaTableDTO doToDTO(MetaTable metaTable);

  MetaTable dtoToDO(MetaTableDTO metaTableDTO);

  List<MetaTableDTO> dosToDTOs(Iterable<MetaTable> metaTables);
}
