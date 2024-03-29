/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: BaseService.proto

package io.holoinsight.server.home.proto.base;

public interface DataBaseResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:DataBaseResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bool success = 1;</code>
   */
  boolean getSuccess();

  /**
   * <code>string errMsg = 2;</code>
   */
  String getErrMsg();

  /**
   * <code>string errMsg = 2;</code>
   */
  com.google.protobuf.ByteString getErrMsgBytes();

  /**
   * <code>string rowsJson = 3;</code>
   */
  String getRowsJson();

  /**
   * <code>string rowsJson = 3;</code>
   */
  com.google.protobuf.ByteString getRowsJsonBytes();

  /**
   * <code>string traceId = 4;</code>
   */
  String getTraceId();

  /**
   * <code>string traceId = 4;</code>
   */
  com.google.protobuf.ByteString getTraceIdBytes();
}
