/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-agent.proto

package io.holoinsight.server.registry.grpc.agent;

public interface AgentK8sInfoOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.agent.AgentK8sInfo)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string host_ip = 1;</code>
   */
  java.lang.String getHostIp();

  /**
   * <code>string host_ip = 1;</code>
   */
  com.google.protobuf.ByteString getHostIpBytes();

  /**
   * <code>string namespace = 2;</code>
   */
  java.lang.String getNamespace();

  /**
   * <code>string namespace = 2;</code>
   */
  com.google.protobuf.ByteString getNamespaceBytes();

  /**
   * <code>string pod = 3;</code>
   */
  java.lang.String getPod();

  /**
   * <code>string pod = 3;</code>
   */
  com.google.protobuf.ByteString getPodBytes();

  /**
   * <code>string node_hostname = 4;</code>
   */
  java.lang.String getNodeHostname();

  /**
   * <code>string node_hostname = 4;</code>
   */
  com.google.protobuf.ByteString getNodeHostnameBytes();
}
