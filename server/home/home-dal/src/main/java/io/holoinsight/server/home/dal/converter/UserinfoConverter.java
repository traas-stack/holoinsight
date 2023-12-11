/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.Userinfo;
import io.holoinsight.server.common.dao.transformer.MapJsonMapper;
import io.holoinsight.server.home.facade.UserinfoDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author masaimu
 * @version 2023-06-07 20:41:00
 */
@Mapper(componentModel = "spring", uses = {MapJsonMapper.class})
public interface UserinfoConverter {

  UserinfoDTO doToDTO(Userinfo userinfo);

  Userinfo dtoToDO(UserinfoDTO userinfoDTO);

  List<UserinfoDTO> dosToDTOs(Iterable<Userinfo> userinfos);
}
