/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-prod.proto

package io.holoinsight.server.registry.grpc.prod;

public interface ListFilesResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.prod.ListFilesResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonResponseHeader header = 1;</code>
   */
  boolean hasHeader();

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonResponseHeader header = 1;</code>
   */
  io.holoinsight.server.common.grpc.CommonResponseHeader getHeader();

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonResponseHeader header = 1;</code>
   */
  io.holoinsight.server.common.grpc.CommonResponseHeaderOrBuilder getHeaderOrBuilder();

  /**
   * <code>repeated .io.holoinsight.server.common.grpc.FileNode nodes = 2;</code>
   */
  java.util.List<io.holoinsight.server.common.grpc.FileNode> getNodesList();

  /**
   * <code>repeated .io.holoinsight.server.common.grpc.FileNode nodes = 2;</code>
   */
  io.holoinsight.server.common.grpc.FileNode getNodes(int index);

  /**
   * <code>repeated .io.holoinsight.server.common.grpc.FileNode nodes = 2;</code>
   */
  int getNodesCount();

  /**
   * <code>repeated .io.holoinsight.server.common.grpc.FileNode nodes = 2;</code>
   */
  java.util.List<? extends io.holoinsight.server.common.grpc.FileNodeOrBuilder> getNodesOrBuilderList();

  /**
   * <code>repeated .io.holoinsight.server.common.grpc.FileNode nodes = 2;</code>
   */
  io.holoinsight.server.common.grpc.FileNodeOrBuilder getNodesOrBuilder(int index);
}
