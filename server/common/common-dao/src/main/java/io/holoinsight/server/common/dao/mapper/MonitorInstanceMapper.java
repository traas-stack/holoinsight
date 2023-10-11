/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.holoinsight.server.common.dao.entity.MonitorInstance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MonitorInstanceMapper extends BaseMapper<MonitorInstance> {
}
