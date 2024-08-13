/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: common/Common.proto

package io.holoinsight.server.apm.grpc.common;

/**
 * Protobuf type {@code io.holoinsight.server.apm.grpc.common.CommonResponse}
 */
public final class CommonResponse extends com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:io.holoinsight.server.apm.grpc.common.CommonResponse)
    CommonResponseOrBuilder {
  private static final long serialVersionUID = 0L;

  // Use CommonResponse.newBuilder() to construct.
  private CommonResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private CommonResponse() {
    message_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new CommonResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private CommonResponse(com.google.protobuf.CodedInputStream input,
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
          case 8: {

            code_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            message_ = s;
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
    return io.holoinsight.server.apm.grpc.common.Common.internal_static_io_holoinsight_server_apm_grpc_common_CommonResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return io.holoinsight.server.apm.grpc.common.Common.internal_static_io_holoinsight_server_apm_grpc_common_CommonResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(io.holoinsight.server.apm.grpc.common.CommonResponse.class,
            io.holoinsight.server.apm.grpc.common.CommonResponse.Builder.class);
  }

  public static final int CODE_FIELD_NUMBER = 1;
  private int code_;

  /**
   * <pre>
   * 具体每个code是什么含义由每个业务自己定
   * 但规定 code == 0 表示业务成功
   * </pre>
   *
   * <code>int32 code = 1;</code>
   */
  public int getCode() {
    return code_;
  }

  public static final int MESSAGE_FIELD_NUMBER = 2;
  private volatile java.lang.Object message_;

  /**
   * <code>string message = 2;</code>
   */
  public java.lang.String getMessage() {
    java.lang.Object ref = message_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      message_ = s;
      return s;
    }
  }

  /**
   * <code>string message = 2;</code>
   */
  public com.google.protobuf.ByteString getMessageBytes() {
    java.lang.Object ref = message_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      message_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (code_ != 0) {
      output.writeInt32(1, code_);
    }
    if (!getMessageBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, message_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1)
      return size;

    size = 0;
    if (code_ != 0) {
      size += com.google.protobuf.CodedOutputStream.computeInt32Size(1, code_);
    }
    if (!getMessageBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, message_);
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
    if (!(obj instanceof io.holoinsight.server.apm.grpc.common.CommonResponse)) {
      return super.equals(obj);
    }
    io.holoinsight.server.apm.grpc.common.CommonResponse other =
        (io.holoinsight.server.apm.grpc.common.CommonResponse) obj;

    if (getCode() != other.getCode())
      return false;
    if (!getMessage().equals(other.getMessage()))
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
    hash = (37 * hash) + CODE_FIELD_NUMBER;
    hash = (53 * hash) + getCode();
    hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
    hash = (53 * hash) + getMessage().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input,
        extensionRegistry);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input,
        extensionRegistry);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse parseFrom(
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

  public static Builder newBuilder(io.holoinsight.server.apm.grpc.common.CommonResponse prototype) {
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
   * Protobuf type {@code io.holoinsight.server.apm.grpc.common.CommonResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:io.holoinsight.server.apm.grpc.common.CommonResponse)
      io.holoinsight.server.apm.grpc.common.CommonResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return io.holoinsight.server.apm.grpc.common.Common.internal_static_io_holoinsight_server_apm_grpc_common_CommonResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return io.holoinsight.server.apm.grpc.common.Common.internal_static_io_holoinsight_server_apm_grpc_common_CommonResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              io.holoinsight.server.apm.grpc.common.CommonResponse.class,
              io.holoinsight.server.apm.grpc.common.CommonResponse.Builder.class);
    }

    // Construct using io.holoinsight.server.apm.grpc.common.CommonResponse.newBuilder()
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
      code_ = 0;

      message_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return io.holoinsight.server.apm.grpc.common.Common.internal_static_io_holoinsight_server_apm_grpc_common_CommonResponse_descriptor;
    }

    @java.lang.Override
    public io.holoinsight.server.apm.grpc.common.CommonResponse getDefaultInstanceForType() {
      return io.holoinsight.server.apm.grpc.common.CommonResponse.getDefaultInstance();
    }

    @java.lang.Override
    public io.holoinsight.server.apm.grpc.common.CommonResponse build() {
      io.holoinsight.server.apm.grpc.common.CommonResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public io.holoinsight.server.apm.grpc.common.CommonResponse buildPartial() {
      io.holoinsight.server.apm.grpc.common.CommonResponse result =
          new io.holoinsight.server.apm.grpc.common.CommonResponse(this);
      result.code_ = code_;
      result.message_ = message_;
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
      if (other instanceof io.holoinsight.server.apm.grpc.common.CommonResponse) {
        return mergeFrom((io.holoinsight.server.apm.grpc.common.CommonResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.holoinsight.server.apm.grpc.common.CommonResponse other) {
      if (other == io.holoinsight.server.apm.grpc.common.CommonResponse.getDefaultInstance())
        return this;
      if (other.getCode() != 0) {
        setCode(other.getCode());
      }
      if (!other.getMessage().isEmpty()) {
        message_ = other.message_;
        onChanged();
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
      io.holoinsight.server.apm.grpc.common.CommonResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage =
            (io.holoinsight.server.apm.grpc.common.CommonResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int code_;

    /**
     * <pre>
     * 具体每个code是什么含义由每个业务自己定
     * 但规定 code == 0 表示业务成功
     * </pre>
     *
     * <code>int32 code = 1;</code>
     */
    public int getCode() {
      return code_;
    }

    /**
     * <pre>
     * 具体每个code是什么含义由每个业务自己定
     * 但规定 code == 0 表示业务成功
     * </pre>
     *
     * <code>int32 code = 1;</code>
     */
    public Builder setCode(int value) {

      code_ = value;
      onChanged();
      return this;
    }

    /**
     * <pre>
     * 具体每个code是什么含义由每个业务自己定
     * 但规定 code == 0 表示业务成功
     * </pre>
     *
     * <code>int32 code = 1;</code>
     */
    public Builder clearCode() {

      code_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object message_ = "";

    /**
     * <code>string message = 2;</code>
     */
    public java.lang.String getMessage() {
      java.lang.Object ref = message_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        message_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }

    /**
     * <code>string message = 2;</code>
     */
    public com.google.protobuf.ByteString getMessageBytes() {
      java.lang.Object ref = message_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        message_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    /**
     * <code>string message = 2;</code>
     */
    public Builder setMessage(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }

      message_ = value;
      onChanged();
      return this;
    }

    /**
     * <code>string message = 2;</code>
     */
    public Builder clearMessage() {

      message_ = getDefaultInstance().getMessage();
      onChanged();
      return this;
    }

    /**
     * <code>string message = 2;</code>
     */
    public Builder setMessageBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);

      message_ = value;
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


    // @@protoc_insertion_point(builder_scope:io.holoinsight.server.apm.grpc.common.CommonResponse)
  }

  // @@protoc_insertion_point(class_scope:io.holoinsight.server.apm.grpc.common.CommonResponse)
  private static final io.holoinsight.server.apm.grpc.common.CommonResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.holoinsight.server.apm.grpc.common.CommonResponse();
  }

  public static io.holoinsight.server.apm.grpc.common.CommonResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CommonResponse> PARSER =
      new com.google.protobuf.AbstractParser<CommonResponse>() {
        @java.lang.Override
        public CommonResponse parsePartialFrom(com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new CommonResponse(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<CommonResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CommonResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public io.holoinsight.server.apm.grpc.common.CommonResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
