/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.converter;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.dao.transformer.ListJsonMapper;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: MetricInfoConverter.java, Date: 2023-04-24 Time: 20:33
 */
@Mapper(componentModel = "spring", uses = {ListJsonMapper.class, MapJsonMapper.class})
public interface MetricInfoConverter {
  MetricInfoDTO doToDTO(MetricInfo metricInfo);

  MetricInfo dtoToDO(MetricInfoDTO metricInfoDTO);

  List<MetricInfoDTO> dosToDTOs(Iterable<MetricInfo> metricInfos);
}
