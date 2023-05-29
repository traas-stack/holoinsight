/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.ServiceErrorEsStorage;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ServiceErrorTatrisStorage extends ServiceErrorEsStorage {
    @Autowired
    @Qualifier("tatrisClient")
    private RestHighLevelClient tatrisClient;

    @Override
    protected RestHighLevelClient client() {
        return tatrisClient;
    }

}
