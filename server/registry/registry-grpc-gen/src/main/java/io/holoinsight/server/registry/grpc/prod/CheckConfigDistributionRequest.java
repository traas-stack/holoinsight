/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: registry-for-prod.proto

package io.holoinsight.server.registry.grpc.prod;

/**
 * Protobuf type {@code io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest}
 */
public final class CheckConfigDistributionRequest extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest)
    CheckConfigDistributionRequestOrBuilder {
  private static final long serialVersionUID = 0L;

  // Use CheckConfigDistributionRequest.newBuilder() to construct.
  private CheckConfigDistributionRequest(
      com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private CheckConfigDistributionRequest() {
    tableName_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new CheckConfigDistributionRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private CheckConfigDistributionRequest(com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            io.holoinsight.server.common.grpc.CommonRequestHeader.Builder subBuilder = null;
            if (header_ != null) {
              subBuilder = header_.toBuilder();
            }
            header_ = input.readMessage(
                io.holoinsight.server.common.grpc.CommonRequestHeader.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(header_);
              header_ = subBuilder.buildPartial();
            }

            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            tableName_ = s;
            break;
          }
          case 24: {

            configId_ = input.readInt64();
            break;
          }
          default: {
            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return io.holoinsight.server.registry.grpc.prod.RegistryForProdProtos.internal_static_io_holoinsight_server_registry_grpc_prod_CheckConfigDistributionRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return io.holoinsight.server.registry.grpc.prod.RegistryForProdProtos.internal_static_io_holoinsight_server_registry_grpc_prod_CheckConfigDistributionRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest.class,
            io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest.Builder.class);
  }

  public static final int HEADER_FIELD_NUMBER = 1;
  private io.holoinsight.server.common.grpc.CommonRequestHeader header_;

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
   */
  public boolean hasHeader() {
    return header_ != null;
  }

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
   */
  public io.holoinsight.server.common.grpc.CommonRequestHeader getHeader() {
    return header_ == null
        ? io.holoinsight.server.common.grpc.CommonRequestHeader.getDefaultInstance()
        : header_;
  }

  /**
   * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
   */
  public io.holoinsight.server.common.grpc.CommonRequestHeaderOrBuilder getHeaderOrBuilder() {
    return getHeader();
  }

  public static final int TABLE_NAME_FIELD_NUMBER = 2;
  private volatile java.lang.Object tableName_;

  /**
   * <code>string table_name = 2;</code>
   */
  public java.lang.String getTableName() {
    java.lang.Object ref = tableName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      tableName_ = s;
      return s;
    }
  }

  /**
   * <code>string table_name = 2;</code>
   */
  public com.google.protobuf.ByteString getTableNameBytes() {
    java.lang.Object ref = tableName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      tableName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CONFIG_ID_FIELD_NUMBER = 3;
  private long configId_;

  /**
   * <code>int64 config_id = 3;</code>
   */
  public long getConfigId() {
    return configId_;
  }

  private byte memoizedIsInitialized = -1;

  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1)
      return true;
    if (isInitialized == 0)
      return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
    if (header_ != null) {
      output.writeMessage(1, getHeader());
    }
    if (!getTableNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, tableName_);
    }
    if (configId_ != 0L) {
      output.writeInt64(3, configId_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1)
      return size;

    size = 0;
    if (header_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getHeader());
    }
    if (!getTableNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, tableName_);
    }
    if (configId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(3, configId_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest)) {
      return super.equals(obj);
    }
    io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest other =
        (io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest) obj;

    if (hasHeader() != other.hasHeader())
      return false;
    if (hasHeader()) {
      if (!getHeader().equals(other.getHeader()))
        return false;
    }
    if (!getTableName().equals(other.getTableName()))
      return false;
    if (getConfigId() != other.getConfigId())
      return false;
    if (!unknownFields.equals(other.unknownFields))
      return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasHeader()) {
      hash = (37 * hash) + HEADER_FIELD_NUMBER;
      hash = (53 * hash) + getHeader().hashCode();
    }
    hash = (37 * hash) + TABLE_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getTableName().hashCode();
    hash = (37 * hash) + CONFIG_ID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getConfigId());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input,
        extensionRegistry);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input,
        extensionRegistry);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input,
        extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }

  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }

  public static Builder newBuilder(
      io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }

  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }

  /**
   * Protobuf type {@code io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest)
      io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return io.holoinsight.server.registry.grpc.prod.RegistryForProdProtos.internal_static_io_holoinsight_server_registry_grpc_prod_CheckConfigDistributionRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return io.holoinsight.server.registry.grpc.prod.RegistryForProdProtos.internal_static_io_holoinsight_server_registry_grpc_prod_CheckConfigDistributionRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest.class,
              io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest.Builder.class);
    }

    // Construct using
    // io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {}

    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (headerBuilder_ == null) {
        header_ = null;
      } else {
        header_ = null;
        headerBuilder_ = null;
      }
      tableName_ = "";

      configId_ = 0L;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return io.holoinsight.server.registry.grpc.prod.RegistryForProdProtos.internal_static_io_holoinsight_server_registry_grpc_prod_CheckConfigDistributionRequest_descriptor;
    }

    @java.lang.Override
    public io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest getDefaultInstanceForType() {
      return io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest
          .getDefaultInstance();
    }

    @java.lang.Override
    public io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest build() {
      io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest result =
          buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest buildPartial() {
      io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest result =
          new io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest(this);
      if (headerBuilder_ == null) {
        result.header_ = header_;
      } else {
        result.header_ = headerBuilder_.build();
      }
      result.tableName_ = tableName_;
      result.configId_ = configId_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }

    @java.lang.Override
    public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }

    @java.lang.Override
    public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }

    @java.lang.Override
    public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }

    @java.lang.Override
    public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }

    @java.lang.Override
    public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest) {
        return mergeFrom(
            (io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(
        io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest other) {
      if (other == io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest
          .getDefaultInstance())
        return this;
      if (other.hasHeader()) {
        mergeHeader(other.getHeader());
      }
      if (!other.getTableName().isEmpty()) {
        tableName_ = other.tableName_;
        onChanged();
      }
      if (other.getConfigId() != 0L) {
        setConfigId(other.getConfigId());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest) e
            .getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private io.holoinsight.server.common.grpc.CommonRequestHeader header_;
    private com.google.protobuf.SingleFieldBuilderV3<io.holoinsight.server.common.grpc.CommonRequestHeader, io.holoinsight.server.common.grpc.CommonRequestHeader.Builder, io.holoinsight.server.common.grpc.CommonRequestHeaderOrBuilder> headerBuilder_;

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public boolean hasHeader() {
      return headerBuilder_ != null || header_ != null;
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public io.holoinsight.server.common.grpc.CommonRequestHeader getHeader() {
      if (headerBuilder_ == null) {
        return header_ == null
            ? io.holoinsight.server.common.grpc.CommonRequestHeader.getDefaultInstance()
            : header_;
      } else {
        return headerBuilder_.getMessage();
      }
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public Builder setHeader(io.holoinsight.server.common.grpc.CommonRequestHeader value) {
      if (headerBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        header_ = value;
        onChanged();
      } else {
        headerBuilder_.setMessage(value);
      }

      return this;
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public Builder setHeader(
        io.holoinsight.server.common.grpc.CommonRequestHeader.Builder builderForValue) {
      if (headerBuilder_ == null) {
        header_ = builderForValue.build();
        onChanged();
      } else {
        headerBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public Builder mergeHeader(io.holoinsight.server.common.grpc.CommonRequestHeader value) {
      if (headerBuilder_ == null) {
        if (header_ != null) {
          header_ = io.holoinsight.server.common.grpc.CommonRequestHeader.newBuilder(header_)
              .mergeFrom(value).buildPartial();
        } else {
          header_ = value;
        }
        onChanged();
      } else {
        headerBuilder_.mergeFrom(value);
      }

      return this;
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public Builder clearHeader() {
      if (headerBuilder_ == null) {
        header_ = null;
        onChanged();
      } else {
        header_ = null;
        headerBuilder_ = null;
      }

      return this;
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public io.holoinsight.server.common.grpc.CommonRequestHeader.Builder getHeaderBuilder() {

      onChanged();
      return getHeaderFieldBuilder().getBuilder();
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    public io.holoinsight.server.common.grpc.CommonRequestHeaderOrBuilder getHeaderOrBuilder() {
      if (headerBuilder_ != null) {
        return headerBuilder_.getMessageOrBuilder();
      } else {
        return header_ == null
            ? io.holoinsight.server.common.grpc.CommonRequestHeader.getDefaultInstance()
            : header_;
      }
    }

    /**
     * <code>.io.holoinsight.server.common.grpc.CommonRequestHeader header = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<io.holoinsight.server.common.grpc.CommonRequestHeader, io.holoinsight.server.common.grpc.CommonRequestHeader.Builder, io.holoinsight.server.common.grpc.CommonRequestHeaderOrBuilder> getHeaderFieldBuilder() {
      if (headerBuilder_ == null) {
        headerBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<io.holoinsight.server.common.grpc.CommonRequestHeader, io.holoinsight.server.common.grpc.CommonRequestHeader.Builder, io.holoinsight.server.common.grpc.CommonRequestHeaderOrBuilder>(
                getHeader(), getParentForChildren(), isClean());
        header_ = null;
      }
      return headerBuilder_;
    }

    private java.lang.Object tableName_ = "";

    /**
     * <code>string table_name = 2;</code>
     */
    public java.lang.String getTableName() {
      java.lang.Object ref = tableName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        tableName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }

    /**
     * <code>string table_name = 2;</code>
     */
    public com.google.protobuf.ByteString getTableNameBytes() {
      java.lang.Object ref = tableName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        tableName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    /**
     * <code>string table_name = 2;</code>
     */
    public Builder setTableName(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }

      tableName_ = value;
      onChanged();
      return this;
    }

    /**
     * <code>string table_name = 2;</code>
     */
    public Builder clearTableName() {

      tableName_ = getDefaultInstance().getTableName();
      onChanged();
      return this;
    }

    /**
     * <code>string table_name = 2;</code>
     */
    public Builder setTableNameBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);

      tableName_ = value;
      onChanged();
      return this;
    }

    private long configId_;

    /**
     * <code>int64 config_id = 3;</code>
     */
    public long getConfigId() {
      return configId_;
    }

    /**
     * <code>int64 config_id = 3;</code>
     */
    public Builder setConfigId(long value) {

      configId_ = value;
      onChanged();
      return this;
    }

    /**
     * <code>int64 config_id = 3;</code>
     */
    public Builder clearConfigId() {

      configId_ = 0L;
      onChanged();
      return this;
    }

    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest)
  }

  // @@protoc_insertion_point(class_scope:io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest)
  private static final io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE =
        new io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest();
  }

  public static io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CheckConfigDistributionRequest> PARSER =
      new com.google.protobuf.AbstractParser<CheckConfigDistributionRequest>() {
        @java.lang.Override
        public CheckConfigDistributionRequest parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new CheckConfigDistributionRequest(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<CheckConfigDistributionRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CheckConfigDistributionRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
