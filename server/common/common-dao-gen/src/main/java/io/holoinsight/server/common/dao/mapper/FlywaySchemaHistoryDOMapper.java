/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.FlywaySchemaHistoryDO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FlywaySchemaHistoryDOMapper {

  List<FlywaySchemaHistoryDO> selectAll();

  int skipAll();

  int skipLatest();

}
