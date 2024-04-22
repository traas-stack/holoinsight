/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.holoinsight.server.common.dao.entity.MetaDimData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author jsy1001de
 * @version 1.0: MetaDimDataMapper.java, Date: 2024-04-18 Time: 11:56
 */
@Mapper
@Repository
public interface MetaDimDataMapper extends BaseMapper<MetaDimData> {
}
