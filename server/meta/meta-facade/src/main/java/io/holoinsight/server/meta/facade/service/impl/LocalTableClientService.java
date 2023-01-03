/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service.impl;

import java.util.List;

import io.holoinsight.server.meta.facade.service.TableClientService;

/**
 * <p>created at 2022/12/6
 *
 * @author jsy1001de
 */
public class LocalTableClientService implements TableClientService {
    @Override
    public void createTable(String tableName) {

    }

    @Override
    public void deleteTable(String tableName) {

    }

    @Override
    public void createIndex(String tableName, String indexKey, Boolean asc) {

    }

    @Override
    public void deleteIndex(String tableName, String indexKey) {

    }

    @Override
    public List<Object> getIndexInfo(String tableName) {
        return null;
    }
}
