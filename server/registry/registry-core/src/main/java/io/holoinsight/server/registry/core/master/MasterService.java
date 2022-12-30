/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.master;

import io.holoinsight.server.common.dao.mapper.GaeaMasterDOMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>created at 2022/3/12
 *
 * @author zzhb101
 */
@Service
public class MasterService {
    @Autowired
    private GaeaMasterDOMapper mapper;

    public void acquire(String tenant, String name, String json) {
    }
}
