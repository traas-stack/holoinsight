/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.GaeaCollectConfig;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import io.holoinsight.server.home.dal.transformer.BooleanIntegerMapper;
import io.holoinsight.server.home.dal.transformer.GaeaCollectConfigJsonMapper;
import io.holoinsight.server.home.dal.transformer.GaeaCollectRangeMapper;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaCollectConfigMapper.java, v 0.1 2022年03月31日 8:50 下午 jinsong.yjs Exp $
 */
@Mapper(componentModel = "spring", uses = {BooleanIntegerMapper.class,
    GaeaCollectConfigJsonMapper.class, MapJsonMapper.class, GaeaCollectRangeMapper.class})
public interface GaeaCollectConfigConverter {
  GaeaCollectConfigDTO doToDTO(GaeaCollectConfig gaeaCollectConfig);

  GaeaCollectConfig dtoToDO(GaeaCollectConfigDTO gaeaCollectConfigDTO);

  List<GaeaCollectConfigDTO> dosToDTOs(Iterable<GaeaCollectConfig> gaeaCollectConfigs);
}
