/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.home.biz.service.MetaDataDictValueService;
import io.holoinsight.server.home.dal.mapper.MetaDataDictValueMapper;
import io.holoinsight.server.home.dal.model.MetaDataDictValue;
import org.springframework.stereotype.Service;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaDictValueServiceImpl.java, v 0.1 2022年11月03日 下午9:15 jinsong.yjs Exp $
 */
@Service
public class MetaDataDictValueServiceImpl extends ServiceImpl<MetaDataDictValueMapper, MetaDataDictValue>
        implements MetaDataDictValueService {
}