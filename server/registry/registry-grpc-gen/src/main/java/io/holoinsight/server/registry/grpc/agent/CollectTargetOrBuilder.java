/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-agent.proto

package io.holoinsight.server.registry.grpc.agent;

public interface CollectTargetOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.registry.grpc.agent.CollectTarget)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * TODO 元数据内容是否也可能变化?
   * </pre>
   *
   * <code>string key = 1;</code>
   */
  java.lang.String getKey();

  /**
   * <pre>
   * TODO 元数据内容是否也可能变化?
   * </pre>
   *
   * <code>string key = 1;</code>
   */
  com.google.protobuf.ByteString getKeyBytes();

  /**
   * <code>string type = 2;</code>
   */
  java.lang.String getType();

  /**
   * <code>string type = 2;</code>
   */
  com.google.protobuf.ByteString getTypeBytes();

  /**
   * <code>string version = 3;</code>
   */
  java.lang.String getVersion();

  /**
   * <code>string version = 3;</code>
   */
  com.google.protobuf.ByteString getVersionBytes();

  /**
   * <code>map&lt;string, string&gt; meta = 4;</code>
   */
  int getMetaCount();

  /**
   * <code>map&lt;string, string&gt; meta = 4;</code>
   */
  boolean containsMeta(java.lang.String key);

  /**
   * Use {@link #getMetaMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String> getMeta();

  /**
   * <code>map&lt;string, string&gt; meta = 4;</code>
   */
  java.util.Map<java.lang.String, java.lang.String> getMetaMap();

  /**
   * <code>map&lt;string, string&gt; meta = 4;</code>
   */

  java.lang.String getMetaOrDefault(java.lang.String key, java.lang.String defaultValue);

  /**
   * <code>map&lt;string, string&gt; meta = 4;</code>
   */

  java.lang.String getMetaOrThrow(java.lang.String key);
}
