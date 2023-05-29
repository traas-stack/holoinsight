/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.RecordEsStorage;
import io.holoinsight.server.apm.engine.model.RecordDO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author jiwliu
 * @version : RecordEsService.java, v 0.1 2022年10月12日 11:52 xiangwanpeng Exp $
 */
@Slf4j
public class RecordTatrisStorage<T extends RecordDO> extends RecordEsStorage<T> {

    @Autowired
    @Qualifier("tatrisClient")
    private RestHighLevelClient tatrisClient;

    @Override
    protected RestHighLevelClient client() {
        return tatrisClient;
    }

}
