/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-internal.proto

package io.holoinsight.server.registry.grpc.internal;

public interface BiStreamProxyRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string agent_id = 1;</code>
   */
  java.lang.String getAgentId();

  /**
   * <code>string agent_id = 1;</code>
   */
  com.google.protobuf.ByteString getAgentIdBytes();

  /**
   * <code>int32 biz_type = 2;</code>
   */
  int getBizType();

  /**
   * <code>bytes payload = 3;</code>
   */
  com.google.protobuf.ByteString getPayload();
}
