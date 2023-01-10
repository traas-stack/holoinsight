/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.TenantOpsDO;
import io.holoinsight.server.common.dao.entity.TenantOpsDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TenantOpsDOMapper {
  long countByExample(TenantOpsDOExample example);

  int deleteByExample(TenantOpsDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(TenantOpsDO record);

  int insertSelective(@Param("record") TenantOpsDO record,
      @Param("selective") TenantOpsDO.Column... selective);

  TenantOpsDO selectOneByExample(TenantOpsDOExample example);

  TenantOpsDO selectOneByExampleSelective(@Param("example") TenantOpsDOExample example,
      @Param("selective") TenantOpsDO.Column... selective);

  List<TenantOpsDO> selectByExampleSelective(@Param("example") TenantOpsDOExample example,
      @Param("selective") TenantOpsDO.Column... selective);

  List<TenantOpsDO> selectByExample(TenantOpsDOExample example);

  TenantOpsDO selectByPrimaryKeySelective(@Param("id") Long id,
      @Param("selective") TenantOpsDO.Column... selective);

  TenantOpsDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(@Param("record") TenantOpsDO record,
      @Param("example") TenantOpsDOExample example,
      @Param("selective") TenantOpsDO.Column... selective);

  int updateByExample(@Param("record") TenantOpsDO record,
      @Param("example") TenantOpsDOExample example);

  int updateByPrimaryKeySelective(@Param("record") TenantOpsDO record,
      @Param("selective") TenantOpsDO.Column... selective);

  int updateByPrimaryKey(TenantOpsDO record);

  int batchInsert(@Param("list") List<TenantOpsDO> list);

  int batchInsertSelective(@Param("list") List<TenantOpsDO> list,
      @Param("selective") TenantOpsDO.Column... selective);

  int upsert(TenantOpsDO record);

  int upsertSelective(@Param("record") TenantOpsDO record,
      @Param("selective") TenantOpsDO.Column... selective);
}
