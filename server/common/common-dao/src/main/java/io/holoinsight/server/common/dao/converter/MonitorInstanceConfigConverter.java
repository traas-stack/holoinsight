/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.MonitorInstance;
import io.holoinsight.server.common.dao.entity.dto.MonitorInstanceDTO;
import io.holoinsight.server.common.dao.transformer.MonitorInstanceCfgMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MonitorInstanceConfigConverter.java, Date: 2024-06-03 Time: 11:05
 */
@Mapper(componentModel = "spring", uses = {MonitorInstanceCfgMapper.class})
public interface MonitorInstanceConfigConverter {
  MonitorInstanceDTO doToDTO(MonitorInstance monitorInstance);

  MonitorInstance dtoToDO(MonitorInstanceDTO monitorInstanceDTO);

  List<MonitorInstanceDTO> dosToDTOs(Iterable<MonitorInstance> monitorInstances);
}
