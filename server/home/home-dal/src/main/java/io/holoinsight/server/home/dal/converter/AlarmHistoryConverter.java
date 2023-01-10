/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.dal.transformer.ListJsonMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:40 下午
 */
@Mapper(componentModel = "spring", uses = { ListJsonMapper.class })
public interface AlarmHistoryConverter {

    AlarmHistoryDTO doToDTO(AlarmHistory tenant);

    AlarmHistory dtoToDO(AlarmHistoryDTO tenantDTO);

    List<AlarmHistoryDTO> dosToDTOs(Iterable<AlarmHistory> tenants);
}
