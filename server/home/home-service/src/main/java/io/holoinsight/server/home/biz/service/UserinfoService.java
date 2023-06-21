/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.facade.UserinfoDTO;

import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-21 15:09:00
 */
public interface UserinfoService {

  UserinfoDTO queryByUid(String uid);

  Map<String/* uid */, UserinfoDTO> queryByUid(List<String> uidList);
}
