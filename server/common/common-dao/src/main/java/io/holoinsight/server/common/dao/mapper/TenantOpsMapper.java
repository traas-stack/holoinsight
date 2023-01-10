/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.holoinsight.server.common.dao.entity.TenantOps;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author xzchaoo
 * @version 1.0: TenantOpsMapper.java, v 0.1 2022年06月07日 11:52 上午 jinsong.yjs Exp $
 */
@Mapper
@Repository
public interface TenantOpsMapper extends BaseMapper<TenantOps> {
}
