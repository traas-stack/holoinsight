/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-prod.proto

package io.holoinsight.server.registry.grpc.prod;

public interface CheckConfigDistributionRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest)
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
   * <code>string table_name = 2;</code>
   */
  java.lang.String getTableName();

  /**
   * <code>string table_name = 2;</code>
   */
  com.google.protobuf.ByteString getTableNameBytes();

  /**
   * <code>int64 config_id = 3;</code>
   */
  long getConfigId();
}
