/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: TableService.proto

package io.holoinsight.server.meta.proto.table;

/**
 * Protobuf type {@code scheduler.TableDataResponse}
 */
public  final class TableDataResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:scheduler.TableDataResponse)
    TableDataResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use TableDataResponse.newBuilder() to construct.
  private TableDataResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private TableDataResponse() {
    success_ = false;
    errMsg_ = "";
    rowsJson_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private TableDataResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
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
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 8: {

            success_ = input.readBool();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            errMsg_ = s;
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            rowsJson_ = s;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return io.holoinsight.server.meta.proto.table.TableServiceProto.internal_static_scheduler_TableDataResponse_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return io.holoinsight.server.meta.proto.table.TableServiceProto.internal_static_scheduler_TableDataResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            io.holoinsight.server.meta.proto.table.TableDataResponse.class, io.holoinsight.server.meta.proto.table.TableDataResponse.Builder.class);
  }

  public static final int SUCCESS_FIELD_NUMBER = 1;
  private boolean success_;
  /**
   * <code>bool success = 1;</code>
   */
  public boolean getSuccess() {
    return success_;
  }

  public static final int ERRMSG_FIELD_NUMBER = 2;
  private volatile java.lang.Object errMsg_;
  /**
   * <code>string errMsg = 2;</code>
   */
  public java.lang.String getErrMsg() {
    java.lang.Object ref = errMsg_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      errMsg_ = s;
      return s;
    }
  }
  /**
   * <code>string errMsg = 2;</code>
   */
  public com.google.protobuf.ByteString
      getErrMsgBytes() {
    java.lang.Object ref = errMsg_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      errMsg_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ROWSJSON_FIELD_NUMBER = 3;
  private volatile java.lang.Object rowsJson_;
  /**
   * <code>string rowsJson = 3;</code>
   */
  public java.lang.String getRowsJson() {
    java.lang.Object ref = rowsJson_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      rowsJson_ = s;
      return s;
    }
  }
  /**
   * <code>string rowsJson = 3;</code>
   */
  public com.google.protobuf.ByteString
      getRowsJsonBytes() {
    java.lang.Object ref = rowsJson_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      rowsJson_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (success_ != false) {
      output.writeBool(1, success_);
    }
    if (!getErrMsgBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, errMsg_);
    }
    if (!getRowsJsonBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, rowsJson_);
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (success_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(1, success_);
    }
    if (!getErrMsgBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, errMsg_);
    }
    if (!getRowsJsonBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, rowsJson_);
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
    if (!(obj instanceof io.holoinsight.server.meta.proto.table.TableDataResponse)) {
      return super.equals(obj);
    }
    io.holoinsight.server.meta.proto.table.TableDataResponse other = (io.holoinsight.server.meta.proto.table.TableDataResponse) obj;

    boolean result = true;
    result = result && (getSuccess()
        == other.getSuccess());
    result = result && getErrMsg()
        .equals(other.getErrMsg());
    result = result && getRowsJson()
        .equals(other.getRowsJson());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SUCCESS_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getSuccess());
    hash = (37 * hash) + ERRMSG_FIELD_NUMBER;
    hash = (53 * hash) + getErrMsg().hashCode();
    hash = (37 * hash) + ROWSJSON_FIELD_NUMBER;
    hash = (53 * hash) + getRowsJson().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static io.holoinsight.server.meta.proto.table.TableDataResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(io.holoinsight.server.meta.proto.table.TableDataResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code scheduler.TableDataResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:scheduler.TableDataResponse)
      io.holoinsight.server.meta.proto.table.TableDataResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return io.holoinsight.server.meta.proto.table.TableServiceProto.internal_static_scheduler_TableDataResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return io.holoinsight.server.meta.proto.table.TableServiceProto.internal_static_scheduler_TableDataResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              io.holoinsight.server.meta.proto.table.TableDataResponse.class, io.holoinsight.server.meta.proto.table.TableDataResponse.Builder.class);
    }

    // Construct using io.holoinsight.server.meta.proto.table.TableDataResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      success_ = false;

      errMsg_ = "";

      rowsJson_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return io.holoinsight.server.meta.proto.table.TableServiceProto.internal_static_scheduler_TableDataResponse_descriptor;
    }

    public io.holoinsight.server.meta.proto.table.TableDataResponse getDefaultInstanceForType() {
      return io.holoinsight.server.meta.proto.table.TableDataResponse.getDefaultInstance();
    }

    public io.holoinsight.server.meta.proto.table.TableDataResponse build() {
      io.holoinsight.server.meta.proto.table.TableDataResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public io.holoinsight.server.meta.proto.table.TableDataResponse buildPartial() {
      io.holoinsight.server.meta.proto.table.TableDataResponse result = new io.holoinsight.server.meta.proto.table.TableDataResponse(this);
      result.success_ = success_;
      result.errMsg_ = errMsg_;
      result.rowsJson_ = rowsJson_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof io.holoinsight.server.meta.proto.table.TableDataResponse) {
        return mergeFrom((io.holoinsight.server.meta.proto.table.TableDataResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.holoinsight.server.meta.proto.table.TableDataResponse other) {
      if (other == io.holoinsight.server.meta.proto.table.TableDataResponse.getDefaultInstance()) return this;
      if (other.getSuccess() != false) {
        setSuccess(other.getSuccess());
      }
      if (!other.getErrMsg().isEmpty()) {
        errMsg_ = other.errMsg_;
        onChanged();
      }
      if (!other.getRowsJson().isEmpty()) {
        rowsJson_ = other.rowsJson_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      io.holoinsight.server.meta.proto.table.TableDataResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (io.holoinsight.server.meta.proto.table.TableDataResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private boolean success_ ;
    /**
     * <code>bool success = 1;</code>
     */
    public boolean getSuccess() {
      return success_;
    }
    /**
     * <code>bool success = 1;</code>
     */
    public Builder setSuccess(boolean value) {
      
      success_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bool success = 1;</code>
     */
    public Builder clearSuccess() {
      
      success_ = false;
      onChanged();
      return this;
    }

    private java.lang.Object errMsg_ = "";
    /**
     * <code>string errMsg = 2;</code>
     */
    public java.lang.String getErrMsg() {
      java.lang.Object ref = errMsg_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errMsg_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string errMsg = 2;</code>
     */
    public com.google.protobuf.ByteString
        getErrMsgBytes() {
      java.lang.Object ref = errMsg_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errMsg_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string errMsg = 2;</code>
     */
    public Builder setErrMsg(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      errMsg_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string errMsg = 2;</code>
     */
    public Builder clearErrMsg() {
      
      errMsg_ = getDefaultInstance().getErrMsg();
      onChanged();
      return this;
    }
    /**
     * <code>string errMsg = 2;</code>
     */
    public Builder setErrMsgBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      errMsg_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object rowsJson_ = "";
    /**
     * <code>string rowsJson = 3;</code>
     */
    public java.lang.String getRowsJson() {
      java.lang.Object ref = rowsJson_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        rowsJson_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string rowsJson = 3;</code>
     */
    public com.google.protobuf.ByteString
        getRowsJsonBytes() {
      java.lang.Object ref = rowsJson_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        rowsJson_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string rowsJson = 3;</code>
     */
    public Builder setRowsJson(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      rowsJson_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string rowsJson = 3;</code>
     */
    public Builder clearRowsJson() {
      
      rowsJson_ = getDefaultInstance().getRowsJson();
      onChanged();
      return this;
    }
    /**
     * <code>string rowsJson = 3;</code>
     */
    public Builder setRowsJsonBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      rowsJson_ = value;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:scheduler.TableDataResponse)
  }

  // @@protoc_insertion_point(class_scope:scheduler.TableDataResponse)
  private static final io.holoinsight.server.meta.proto.table.TableDataResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.holoinsight.server.meta.proto.table.TableDataResponse();
  }

  public static io.holoinsight.server.meta.proto.table.TableDataResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<TableDataResponse>
      PARSER = new com.google.protobuf.AbstractParser<TableDataResponse>() {
    public TableDataResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new TableDataResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<TableDataResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<TableDataResponse> getParserForType() {
    return PARSER;
  }

  public io.holoinsight.server.meta.proto.table.TableDataResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

