/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: DataService.proto

package io.holoinsight.server.meta.proto.data;

public interface DeleteDataByExampleRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:scheduler.DeleteDataByExampleRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string tableName = 1;</code>
   */
  java.lang.String getTableName();

  /**
   * <code>string tableName = 1;</code>
   */
  com.google.protobuf.ByteString getTableNameBytes();

  /**
   * <code>string exampleJson = 2;</code>
   */
  java.lang.String getExampleJson();

  /**
   * <code>string exampleJson = 2;</code>
   */
  com.google.protobuf.ByteString getExampleJsonBytes();

  /**
   * <code>string fromApp = 3;</code>
   */
  java.lang.String getFromApp();

  /**
   * <code>string fromApp = 3;</code>
   */
  com.google.protobuf.ByteString getFromAppBytes();

  /**
   * <code>string fromIp = 4;</code>
   */
  java.lang.String getFromIp();

  /**
   * <code>string fromIp = 4;</code>
   */
  com.google.protobuf.ByteString getFromIpBytes();
}
