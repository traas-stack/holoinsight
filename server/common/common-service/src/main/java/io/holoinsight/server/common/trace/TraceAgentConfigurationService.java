/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import io.holoinsight.server.common.dao.entity.TraceAgentConfigurationDO;
import io.holoinsight.server.common.dao.mapper.TraceAgentConfigurationDOMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * GetTraceAgentConfigurationService class.
 * </p>
 *
 * @author sw1136562366
 */
public class TraceAgentConfigurationService {
  @Autowired
  private TraceAgentConfigurationDOMapper traceAgentConfigurationDOMapper;

  /**
   * <p>
   * getALLFromDB.
   * </p>
   */
  public List<TraceAgentConfiguration> getALLFromDB() throws JsonProcessingException {
    List<TraceAgentConfigurationDO> traceAgentConfigurationDOList =
        traceAgentConfigurationDOMapper.selectByExampleWithBLOBs(null);
    List<TraceAgentConfiguration> result = new ArrayList<>(traceAgentConfigurationDOList.size());
    for (TraceAgentConfigurationDO traceAgentConfigurationDO : traceAgentConfigurationDOList) {
      Map<String, String> configuration =
          new ObjectMapper().readValue(traceAgentConfigurationDO.getValue(), HashMap.class);

      StringBuilder configStr = new StringBuilder();
      configuration.forEach((key, value) -> {
        configStr.append(key).append(":").append(value);
      });

      TraceAgentConfiguration TraceAgentConfiguration =
          new TraceAgentConfiguration(traceAgentConfigurationDO.getTenant(),
              traceAgentConfigurationDO.getService(), traceAgentConfigurationDO.getType(),
              traceAgentConfigurationDO.getLanguage(), configuration,
              Hashing.sha512().hashString(configStr.toString(), StandardCharsets.UTF_8).toString());

      result.add(TraceAgentConfiguration);
    }

    return result;
  }

}
