/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris.ttl;

import io.holoinsight.server.apm.engine.elasticsearch.ttl.EsDataCleaner;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author xiangwanpeng
 * @version : TatrisDataCleaner.java, v 0.1 2022年10月12日 20:38 xiangwanpeng Exp $
 */
@Slf4j
public class TatrisDataCleaner extends EsDataCleaner {

    @Autowired
    @Qualifier("tatrisClient")
    private RestHighLevelClient tatrisClient;

    @Override
    protected RestHighLevelClient client() {
        return tatrisClient;
    }

}
