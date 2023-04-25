/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author jsy1001de
 * @version 1.0: MetricInfoMapper.java, Date: 2023-04-24 Time: 20:26
 */
@Mapper
@Repository
public interface MetricInfoMapper extends BaseMapper<MetricInfo> {
}
