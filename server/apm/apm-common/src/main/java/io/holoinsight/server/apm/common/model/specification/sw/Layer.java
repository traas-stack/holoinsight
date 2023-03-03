/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.exception.UnexpectedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Layer represents an abstract framework in computer science, such as Operating System(OS_LINUX
 * layer), Kubernetes(k8s layer). This kind of layer would be owners of different services detected
 * from different technology.
 */
public enum Layer {
  /**
   * Default Layer if the layer is not defined
   */
  UNDEFINED(0, false),

  /**
   * Envoy Access Log Service
   */
  MESH(1, true),

  /**
   * Agent-installed Service
   */
  GENERAL(2, true),

  /**
   * Linux Machine
   */
  OS_LINUX(3, true),

  /**
   * Kubernetes cluster
   */
  K8S(4, true),

  /**
   * Function as a Service
   */
  FAAS(5, true),

  /**
   * Mesh control plane, eg. Istio control plane
   */
  MESH_CP(6, true),

  /**
   * Mesh data plane, eg. Envoy
   */
  MESH_DP(7, true),

  /**
   * Telemetry from real database
   */
  DATABASE(8, true),

  /**
   * Cache service eg. ehcache, guava-cache, memcache
   */
  CACHE(9, true),

  /**
   * Telemetry from the Browser eg. Apache SkyWalking Client JS
   */
  BROWSER(10, true),

  /**
   * Self Observability of OAP
   */
  SO11Y_OAP(11, true),

  /**
   * Self Observability of Satellite
   */
  SO11Y_SATELLITE(12, true),

  /**
   * Telemetry from the real MQ
   */
  MQ(13, true),

  /**
   * Database conjectured by client side plugin
   */
  VIRTUAL_DATABASE(14, false),

  /**
   * MQ conjectured by client side plugin
   */
  VIRTUAL_MQ(15, false),

  /**
   * The uninstrumented gateways configured in OAP
   */
  VIRTUAL_GATEWAY(16, false),

  /**
   * Kubernetes service
   */
  K8S_SERVICE(17, true), VIRTUAL_CACHE(18, false);

  private final int value;
  /**
   * The `normal` status represents this service is detected by an agent. The `un-normal` service is
   * conjectured by telemetry data collected from agents on/in the `normal` service.
   */
  private final boolean isNormal;
  private static final Map<Integer, Layer> DICTIONARY = new HashMap<>();
  private static final Map<String, Layer> DICTIONARY_NAME = new HashMap<>();

  static {
    Arrays.stream(Layer.values()).forEach(l -> {
      DICTIONARY.put(l.value, l);
      DICTIONARY_NAME.put(l.name(), l);
    });
  }

  Layer(int value, boolean isNormal) {
    this.value = value;
    this.isNormal = isNormal;
  }

  public int value() {
    return value;
  }

  public static Layer valueOf(int value) {
    Layer layer = DICTIONARY.get(value);
    if (layer == null) {
      throw new UnexpectedException("Unknown Layer value");
    }
    return layer;
  }

  public static Layer nameOf(String name) {
    Layer layer = DICTIONARY_NAME.get(name);
    if (layer == null) {
      throw new UnexpectedException("Unknown Layer name");
    }
    return layer;
  }

  public boolean isNormal() {
    return isNormal;
  }
}
