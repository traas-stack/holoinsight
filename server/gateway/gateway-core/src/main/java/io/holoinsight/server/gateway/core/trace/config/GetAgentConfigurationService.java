/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.dao.entity.AgentConfigurationDO;
import io.holoinsight.server.common.dao.entity.AgentConfigurationDOExample;
import io.holoinsight.server.common.dao.mapper.AgentConfigurationDOMapper;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

/**
 * <p>GetAgentConfigurationService class.</p>
 *
 * @author sw1136562366
 */
public class GetAgentConfigurationService {
    @Autowired
    private AgentConfigurationDOMapper agentConfigurationDOMapper;

    /**
     * <p>getAgentConfiguration.</p>
     */
    public AgentConfiguration getAgentConfiguration(String tenant, String service, String appId, String envId) throws JsonProcessingException {
        AgentConfiguration serviceConfiguration = getOneFromDB(tenant, service, appId, envId);

        if (serviceConfiguration == null) {
            serviceConfiguration = getOneFromDB(tenant, "*", appId, envId);
        }

        if (serviceConfiguration == null) {
            serviceConfiguration = getOneFromDB(tenant, "*", appId, "*");
        }

        if (serviceConfiguration == null) {
            serviceConfiguration = getOneFromDB(tenant, "*", "*", "*");
        }

        return serviceConfiguration;
    }

    /**
     * <p>getALLFromDB.</p>
     */
    public List<AgentConfiguration> getALLFromDB() throws JsonProcessingException {
        List<AgentConfigurationDO> agentConfigurationDOList = agentConfigurationDOMapper.selectByExampleWithBLOBs(null);
        List<AgentConfiguration> result = new ArrayList<>(agentConfigurationDOList.size());
        for (AgentConfigurationDO agentConfigurationDO : agentConfigurationDOList) {
            Map<String, String> configuration = new ObjectMapper().readValue(agentConfigurationDO.getValue(), HashMap.class);

            StringBuilder configStr = new StringBuilder();
            configuration.forEach((key, value) -> {
                configStr.append(key).append(":").append(value);
            });

            AgentConfiguration agentConfiguration = new AgentConfiguration(
                agentConfigurationDO.getTenant(),
                agentConfigurationDO.getService(),
                agentConfigurationDO.getAppId(),
                agentConfigurationDO.getEnvId(),
                configuration,
                Hashing.sha512().hashString(
                    configStr.toString(), StandardCharsets.UTF_8).toString()
            );

            result.add(agentConfiguration);
        }

        return result;
    }

    private AgentConfiguration getOneFromDB(String tenant, String service, String appId, String envId) throws JsonProcessingException {
        AgentConfigurationDOExample example = AgentConfigurationDOExample.newAndCreateCriteria()
            .andTenantEqualTo(tenant) //
            .andServiceEqualTo(service) //
            .andAppIdEqualTo(appId) //
            .andEnvIdEqualTo(envId) //
            .example(); //

        AgentConfigurationDO agentConfigurationDO = agentConfigurationDOMapper.selectOneByExampleWithBLOBs(example);
        if (agentConfigurationDO != null) {
            Map<String, String> configuration = JsonUtils.fromJson(agentConfigurationDO.getValue(), HashMap.class);

            StringBuilder configStr = new StringBuilder();
            configuration.forEach((key, value) -> {
                configStr.append(key).append(":").append(value);
            });

            AgentConfiguration agentConfiguration = new AgentConfiguration(
                agentConfigurationDO.getTenant(),
                agentConfigurationDO.getService(),
                agentConfigurationDO.getAppId(),
                agentConfigurationDO.getEnvId(),
                configuration,
                Hashing.sha512().hashString(
                    configStr.toString(), StandardCharsets.UTF_8).toString()
            );

            return agentConfiguration;
        }

        return null;
    }

}
