/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.tatris.installer;

import io.holoinsight.server.apm.engine.elasticsearch.installer.EsModelInstaller;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author xiangwanpeng
 * @version : TatrisModelInstaller.java, v 0.1 2023年02月26日 21:26 xiangwanpeng Exp $
 */
@Slf4j
public class TatrisModelInstaller extends EsModelInstaller {

    @Autowired
    @Qualifier("tatrisClient")
    private RestHighLevelClient tatrisClient;

    @Override
    protected RestHighLevelClient client() {
        return tatrisClient;
    }

}
