/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-agent.proto

package io.holoinsight.server.registry.grpc.agent;

public interface GetControlConfigsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
   */
  boolean hasHeader();

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
   */
  io.holoinsight.server.common.grpc.CommonRequestHeader getHeader();

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
   */
  io.holoinsight.server.common.grpc.CommonRequestHeaderOrBuilder getHeaderOrBuilder();

  /**
   * <code>string agent_id = 2;</code>
   */
  java.lang.String getAgentId();

  /**
   * <code>string agent_id = 2;</code>
   */
  com.google.protobuf.ByteString getAgentIdBytes();
}
