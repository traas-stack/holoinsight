/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.mapper;

import io.holoinsight.server.common.dao.entity.AgentConfigurationDO;
import io.holoinsight.server.common.dao.entity.AgentConfigurationDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AgentConfigurationDOMapper {
    long countByExample(AgentConfigurationDOExample example);

    int deleteByExample(AgentConfigurationDOExample example);

    int deleteByPrimaryKey(@Param("tenant") String tenant, @Param("service") String service, @Param("appId") String appId, @Param("envId") String envId);

    int insert(AgentConfigurationDO record);

    int insertSelective(@Param("record") AgentConfigurationDO record, @Param("selective") AgentConfigurationDO.Column ... selective);

    AgentConfigurationDO selectOneByExample(AgentConfigurationDOExample example);

    AgentConfigurationDO selectOneByExampleSelective(@Param("example") AgentConfigurationDOExample example, @Param("selective") AgentConfigurationDO.Column ... selective);

    AgentConfigurationDO selectOneByExampleWithBLOBs(AgentConfigurationDOExample example);

    List<AgentConfigurationDO> selectByExampleSelective(@Param("example") AgentConfigurationDOExample example, @Param("selective") AgentConfigurationDO.Column ... selective);

    List<AgentConfigurationDO> selectByExampleWithBLOBs(AgentConfigurationDOExample example);

    List<AgentConfigurationDO> selectByExample(AgentConfigurationDOExample example);

    AgentConfigurationDO selectByPrimaryKeySelective(@Param("tenant") String tenant, @Param("service") String service, @Param("appId") String appId, @Param("envId") String envId, @Param("selective") AgentConfigurationDO.Column ... selective);

    AgentConfigurationDO selectByPrimaryKey(@Param("tenant") String tenant, @Param("service") String service, @Param("appId") String appId, @Param("envId") String envId);

    int updateByExampleSelective(@Param("record") AgentConfigurationDO record, @Param("example") AgentConfigurationDOExample example, @Param("selective") AgentConfigurationDO.Column ... selective);

    int updateByExampleWithBLOBs(@Param("record") AgentConfigurationDO record, @Param("example") AgentConfigurationDOExample example);

    int updateByExample(@Param("record") AgentConfigurationDO record, @Param("example") AgentConfigurationDOExample example);

    int updateByPrimaryKeySelective(@Param("record") AgentConfigurationDO record, @Param("selective") AgentConfigurationDO.Column ... selective);

    int updateByPrimaryKeyWithBLOBs(AgentConfigurationDO record);

    int updateByPrimaryKey(AgentConfigurationDO record);

    int batchInsert(@Param("list") List<AgentConfigurationDO> list);

    int batchInsertSelective(@Param("list") List<AgentConfigurationDO> list, @Param("selective") AgentConfigurationDO.Column ... selective);

    int upsert(AgentConfigurationDO record);

    int upsertSelective(@Param("record") AgentConfigurationDO record, @Param("selective") AgentConfigurationDO.Column ... selective);

    int upsertWithBLOBs(AgentConfigurationDO record);
}