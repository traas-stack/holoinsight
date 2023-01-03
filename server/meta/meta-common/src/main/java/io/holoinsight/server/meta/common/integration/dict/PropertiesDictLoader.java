/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

import io.holoinsight.server.meta.common.util.ConstPool;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: PropertiesDictLoader.java, v 0.1 2022年03月14日 10:28 上午 jinsong.yjs Exp $
 */
public class PropertiesDictLoader implements DictLoader {

    public PropertiesDictLoader(int priority) {
        this.priority = priority;
    }


    private int priority;

    @Override
    public List<DictData> load() {

        List<DictData> datas = new LinkedList<>();

        Map<String, String> allProperty = PropertiesListenerConfig.getAllProperty();

        for (String key : allProperty.keySet()) {
            datas.add(new DictData(priority, ConstPool.COMMON_DICT_DOMAIN, ConstPool.COMMON_DICT_DOMAIN, key, null,
                    allProperty.get(key)));
        }
        return datas;
    }

    @Override
    public int level() {
        return priority;
    }

    @Override
    public int timerRefresh() {
        return -1;
    }
}