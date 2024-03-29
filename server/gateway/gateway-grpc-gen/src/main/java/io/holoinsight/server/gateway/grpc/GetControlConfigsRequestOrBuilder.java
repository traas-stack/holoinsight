/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: gateway-for-agent.proto

package io.holoinsight.server.gateway.grpc;

public interface GetControlConfigsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.gateway.grpc.GetControlConfigsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.io.holoinsight.server.gateway.grpc.common.CommonRequestHeader header = 1;</code>
   * 
   * @return Whether the header field is set.
   */
  boolean hasHeader();

  /**
   * <code>.io.holoinsight.server.gateway.grpc.common.CommonRequestHeader header = 1;</code>
   * 
   * @return The header.
   */
  io.holoinsight.server.gateway.grpc.common.CommonRequestHeader getHeader();

  /**
   * <code>.io.holoinsight.server.gateway.grpc.common.CommonRequestHeader header = 1;</code>
   */
  io.holoinsight.server.gateway.grpc.common.CommonRequestHeaderOrBuilder getHeaderOrBuilder();

  /**
   * <code>string agent_id = 2;</code>
   * 
   * @return The agentId.
   */
  java.lang.String getAgentId();

  /**
   * <code>string agent_id = 2;</code>
   * 
   * @return The bytes for agentId.
   */
  com.google.protobuf.ByteString getAgentIdBytes();
}
