/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.SpanMetricEsStorage;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * TODO make some special implementation of tatris
 * 
 * @author jiwliu
 * @version : MetricEsServiceImpl.java, v 0.1 2022年09月29日 16:58 xiangwanpeng Exp $
 */
public class SpanMetricTatrisStorage extends SpanMetricEsStorage {

    @Autowired
    @Qualifier("tatrisClient")
    private RestHighLevelClient tatrisClient;

    @Override
    protected RestHighLevelClient client() {
        return tatrisClient;
    }

    @Override
    public String timeField() {
        return "_timestamp";
    }

}
