/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;


import io.holoinsight.server.home.dal.model.MetaDataDictValue;

import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: SuperCache.java, v 0.1 2022年03月21日 8:26 下午 jinsong.yjs Exp $
 */
public class SuperCache {

    public Map<String /* type */, Map<String /* k */, MetaDataDictValue>> metaDataDictValueMap;
}