/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.utils;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.exception.UnexpectedException;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * IDManager includes all ID encode/decode functions for service, service instance and endpoint.
 */
public class IDManager {
  /**
   * Service ID related functions.
   */
  public static class ServiceID {
    /**
     * @param name service name
     * @param isNormal `true` represents this service is detected by an agent. `false` represents
     *        this service is conjectured by telemetry data collected from agents on/in the `normal`
     *        service.
     */
    public static String buildId(String name, boolean isNormal) {
      return encode(name) + Const.SERVICE_ID_CONNECTOR + BooleanUtils.booleanToValue(isNormal);
    }

    /**
     * @return service ID object decoded from {@link #buildId(String, boolean)} result
     */
    public static ServiceIDDefinition analysisId(String id) {
      final String[] strings = id.split(Const.SERVICE_ID_PARSER_SPLIT);
      if (strings.length != 2) {
        throw new UnexpectedException("Can't split service id into 2 parts, " + id);
      }
      return new ServiceIDDefinition(decode(strings[0]),
          BooleanUtils.valueToBoolean(Integer.parseInt(strings[1])));
    }

    /**
     * @return encoded service relation id
     */
    public static String buildRelationId(ServiceRelationDefine define) {
      return define.sourceId + Const.RELATION_ID_CONNECTOR + define.destId;
    }

    /**
     * @return service relation ID object decoded from
     *         {@link #buildRelationId(ServiceRelationDefine)} result
     */
    public static ServiceRelationDefine analysisRelationId(String entityId) {
      String[] parts = entityId.split(Const.RELATION_ID_PARSER_SPLIT);
      if (parts.length != 2) {
        throw new RuntimeException("Illegal Service Relation entity id");
      }
      return new ServiceRelationDefine(parts[0], parts[1]);
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ServiceIDDefinition {
      private final String name;
      /**
       * TRUE means an agent installed or directly detected service. FALSE means a conjectural
       * service
       */
      private final boolean isReal;
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ServiceRelationDefine {
      private final String sourceId;
      private final String destId;
    }
  }

  /**
   * Service Instance ID related functions.
   */
  public static class ServiceInstanceID {
    /**
     * @param serviceId built by {@link ServiceID#buildId(String, boolean)}
     * @return service instance id
     */
    public static String buildId(String serviceId, String instanceName) {
      return serviceId + Const.ID_CONNECTOR + encode(instanceName);
    }

    /**
     * @return service instance id object decoded from {@link #buildId(String, String)} result
     */
    public static InstanceIDDefinition analysisId(String id) {
      final String[] strings = id.split(Const.ID_PARSER_SPLIT);
      if (strings.length != 2) {
        throw new UnexpectedException("Can't split instance id into 2 parts, " + id);
      }
      return new InstanceIDDefinition(strings[0], decode(strings[1]));
    }

    /**
     * @return encoded service instance relation id
     */
    public static String buildRelationId(ServiceInstanceRelationDefine define) {
      return define.sourceId + Const.RELATION_ID_CONNECTOR + define.destId;
    }

    /**
     * @return service instance relation ID object decoded from
     *         {@link #buildRelationId(ServiceInstanceRelationDefine)} result
     */
    public static ServiceInstanceRelationDefine analysisRelationId(String entityId) {
      String[] parts = entityId.split(Const.RELATION_ID_PARSER_SPLIT);
      if (parts.length != 2) {
        throw new RuntimeException("Illegal Service Instance Relation entity id");
      }
      return new ServiceInstanceRelationDefine(parts[0], parts[1]);
    }

    @RequiredArgsConstructor
    @Getter
    public static class InstanceIDDefinition {
      /**
       * Built by {@link ServiceID#buildId(String, boolean)}
       */
      private final String serviceId;
      private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ServiceInstanceRelationDefine {
      /**
       * Built by {@link ServiceInstanceID#buildId(String, String)}
       */
      private final String sourceId;
      /**
       * Built by {@link ServiceInstanceID#buildId(String, String)}
       */
      private final String destId;
    }
  }

  /**
   * Endpoint ID related functions.
   */
  public static class EndpointID {
    /**
     * @param serviceId built by {@link ServiceID#buildId(String, boolean)}
     * @return endpoint id
     */
    public static String buildId(String serviceId, String endpointName) {
      return serviceId + Const.ID_CONNECTOR + encode(endpointName);
    }

    /**
     * @return Endpoint id object decoded from {@link #buildId(String, String)} result.
     */
    public static EndpointIDDefinition analysisId(String id) {
      final String[] strings = id.split(Const.ID_PARSER_SPLIT);
      if (strings.length != 2) {
        throw new UnexpectedException("Can't split endpoint id into 2 parts, " + id);
      }
      return new EndpointIDDefinition(strings[0], decode(strings[1]));
    }

    /**
     * @return the endpoint relationship string id.
     */
    public static String buildRelationId(EndpointRelationDefine define) {
      return define.sourceServiceId + Const.RELATION_ID_CONNECTOR + encode(define.source)
          + Const.RELATION_ID_CONNECTOR + define.destServiceId + Const.RELATION_ID_CONNECTOR
          + encode(define.dest);
    }

    /**
     * @return endpoint relation ID object decoded from
     *         {@link #buildRelationId(EndpointRelationDefine)} result
     */
    public static EndpointRelationDefine analysisRelationId(String entityId) {
      String[] parts = entityId.split(Const.RELATION_ID_PARSER_SPLIT);
      if (parts.length != 4) {
        throw new UnexpectedException("Illegal endpoint Relation entity id, " + entityId);
      }
      return new EndpointRelationDefine(parts[0], decode(parts[1]), parts[2], decode(parts[3]));
    }

    @RequiredArgsConstructor
    @Getter
    public static class EndpointIDDefinition {
      /**
       * Built by {@link ServiceID#buildId(String, boolean)}
       */
      private final String serviceId;
      private final String endpointName;
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class EndpointRelationDefine {
      /**
       * Built by {@link ServiceID#buildId(String, boolean)}
       */
      private final String sourceServiceId;
      private final String source;
      /**
       * Built by {@link ServiceID#buildId(String, boolean)}
       */
      private final String destServiceId;
      private final String dest;
    }
  }

  /**
   * Process ID related functions.
   */
  public static class ProcessID {
    /**
     * @param instanceId built by {@link ServiceInstanceID#buildId(String, String)}
     * @param name process name
     * @return process id
     */
    public static String buildId(String instanceId, String name) {
      return Hashing.sha256().newHasher()
          .putString(String.format("%s_%s", name, instanceId), Charsets.UTF_8).hash().toString();
    }
  }

  /**
   * Network Address Alias ID related functions.
   */
  public static class NetworkAddressAliasDefine {
    /**
     * @return encoded network address id
     */
    public static String buildId(String name) {
      return encode(name);
    }

    /**
     * @return network address id object decoded from {@link #buildId(String)} result
     */
    public static String analysisId(String id) {
      return decode(id);
    }
  }

  /**
   * @param text normal literal string
   * @return Base64 encoded UTF-8 string
   */
  private static String encode(String text) {
    return new String(Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8);
  }

  /**
   * @param base64text Base64 encoded UTF-8 string
   * @return normal literal string
   */
  private static String decode(String base64text) {
    return new String(Base64.getDecoder().decode(base64text), StandardCharsets.UTF_8);
  }
}
