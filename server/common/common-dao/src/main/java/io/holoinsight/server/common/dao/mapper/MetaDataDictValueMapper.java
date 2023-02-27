/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaDataDictValueMapper.java, v 0.1 2022年11月03日 下午9:16 jinsong.yjs Exp $
 */
@Mapper
@Repository
public interface MetaDataDictValueMapper extends BaseMapper<MetaDataDictValue> {
}
