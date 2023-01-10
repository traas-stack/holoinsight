/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.entity.dto.TenantOpsDTO;
import io.holoinsight.server.common.dao.transformer.TenantOpsStorageMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TenantOpsStorageMapper.class})
public interface TenantOpsConverter {

  TenantOpsDTO doToDTO(TenantOps tenantOps);

  TenantOps dtoToDO(TenantOpsDTO tenantOpsDTO);

  List<TenantOpsDTO> dosToDTOs(Iterable<TenantOps> tenantOps);

}
