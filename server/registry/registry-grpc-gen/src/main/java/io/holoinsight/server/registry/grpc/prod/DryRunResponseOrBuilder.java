/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-prod.proto

package io.holoinsight.server.registry.grpc.prod;

public interface DryRunResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.prod.DryRunResponse)
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
   * <code>.io.holoinsight.server.registry.grpc.prod.DryRunResponse.Event event = 2;</code>
   */
  boolean hasEvent();

  /**
   * <code>.io.holoinsight.server.registry.grpc.prod.DryRunResponse.Event event = 2;</code>
   */
  io.holoinsight.server.registry.grpc.prod.DryRunResponse.Event getEvent();

  /**
   * <code>.io.holoinsight.server.registry.grpc.prod.DryRunResponse.Event event = 2;</code>
   */
  io.holoinsight.server.registry.grpc.prod.DryRunResponse.EventOrBuilder getEventOrBuilder();

  /**
   * <code>repeated .io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult group_results = 3;</code>
   */
  java.util.List<io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult> getGroupResultsList();

  /**
   * <code>repeated .io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult group_results = 3;</code>
   */
  io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult getGroupResults(int index);

  /**
   * <code>repeated .io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult group_results = 3;</code>
   */
  int getGroupResultsCount();

  /**
   * <code>repeated .io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult group_results = 3;</code>
   */
  java.util.List<? extends io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResultOrBuilder> getGroupResultsOrBuilderList();

  /**
   * <code>repeated .io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResult group_results = 3;</code>
   */
  io.holoinsight.server.registry.grpc.prod.DryRunResponse.GroupResultOrBuilder getGroupResultsOrBuilder(
      int index);
}
