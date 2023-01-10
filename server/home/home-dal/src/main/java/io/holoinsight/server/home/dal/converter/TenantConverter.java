/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.Tenant;
import io.holoinsight.server.home.dal.model.dto.TenantDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantConverter {

    TenantDTO doToDTO(Tenant tenant);

    Tenant dtoToDO(TenantDTO tenantDTO);

    List<TenantDTO> dosToDTOs(Iterable<Tenant> tenants);

}
