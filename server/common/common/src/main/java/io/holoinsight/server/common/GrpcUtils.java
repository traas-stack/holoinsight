/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * created at 2020-08-19
 *
 * @author xzchaoo
 */
public final class GrpcUtils {
  // public static final Metadata.Key<String> INTERNAL = Metadata.Key.of("INTERNAL",
  // Metadata.ASCII_STRING_MARSHALLER);
  //
  // public static final String INTERNAL_VALUE = "true";
  //
  // public static final Context.Key<Boolean> CTX_INTERNAL = Context.key("GAEA-INTERNAL");
  //
  // private static final DecompressorRegistry DECOMPRESSOR_REGISTRY;
  //
  // static {
  // Lz4BlockCodec lz4BlockCodec = new Lz4BlockCodec();
  // Lz4FrameCodec lz4FrameCodec = new Lz4FrameCodec();
  //
  // CompressorRegistry.getDefaultInstance().register(lz4FrameCodec);
  // CompressorRegistry.getDefaultInstance().register(lz4BlockCodec);
  //
  // DECOMPRESSOR_REGISTRY = DecompressorRegistry.getDefaultInstance() //
  // .with(new Lz4Decompressor(), true) //
  // .with(lz4BlockCodec, true); //
  // }
  //
  // private GrpcUtils() {
  //
  // }
  //
  // public static CompressorRegistry getCompressorRegistry() {
  // return CompressorRegistry.getDefaultInstance();
  // }
  //
  // public static DecompressorRegistry getDecompressorRegistry() {
  // return DECOMPRESSOR_REGISTRY;
  // }
  //
  // public static <T extends AbstractStub<T>> T markInternal(T stub) {
  // Metadata m = new Metadata();
  // m.put(INTERNAL, INTERNAL_VALUE);
  // return MetadataUtils.attachHeaders(stub, m);
  // }

  /**
   * <p>
   * setCompression.
   * </p>
   */
  public static void setCompression(StreamObserver<?> o, String compression) {
    if (o == null || compression == null) {
      return;
    }
    if (o instanceof ServerCallStreamObserver) {
      ((ServerCallStreamObserver<?>) o).setCompression(compression);
      ((ServerCallStreamObserver<?>) o).setMessageCompression(true);
    }
  }

  // public static void setCompression(StreamObserver<?> o) {
  // setCompression(o, "lz4-block");
  // }

  /**
   * <p>
   * setCompressionGzip.
   * </p>
   */
  public static void setCompressionGzip(StreamObserver<?> o) {
    setCompression(o, "gzip");
  }
  //
  // private static class Lz4FrameCodec implements Compressor, Decompressor {
  // @Override
  // public String getMessageEncoding() {
  // return "lz4";
  // }
  //
  // @Override
  // public InputStream decompress(InputStream is) throws IOException {
  // return new LZ4FrameInputStream(is);
  // }
  //
  // @Override
  // public OutputStream compress(OutputStream os) throws IOException {
  // return new LZ4FrameOutputStream(os, LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB);
  // }
  // }
  //
  // private static class Lz4BlockCodec implements Compressor, Decompressor {
  // @Override
  // public String getMessageEncoding() {
  // return "lz4-block";
  // }
  //
  // @Override
  // public OutputStream compress(OutputStream os) throws IOException {
  // return new LZ4BlockOutputStream(os);
  // }
  //
  // @Override
  // public InputStream decompress(InputStream is) throws IOException {
  // return new LZ4BlockInputStream(is);
  // }
  // }

  /**
   * <p>
   * rebind.
   * </p>
   */
  public static ServerServiceDefinition rebind(ServerServiceDefinition ssd, String oldPrefix,
      String newPrefix) {
    String newServiceName = replaceName(ssd.getServiceDescriptor().getName(), oldPrefix, newPrefix);

    ServerServiceDefinition.Builder oldSsdBuilder = ServerServiceDefinition.builder(newServiceName);
    for (ServerMethodDefinition<?, ?> smd : ssd.getMethods()) {
      MethodDescriptor<?, ?> md = smd.getMethodDescriptor();
      MethodDescriptor<?, ?> newMd = md.toBuilder()
          .setFullMethodName(replaceName(md.getFullMethodName(), oldPrefix, newPrefix)).build();
      oldSsdBuilder.addMethod(newMd, (ServerCallHandler) smd.getServerCallHandler());
    }
    return oldSsdBuilder.build();
  }

  private static String replaceName(String old, String oldPrefix, String newPrefix) {
    return StringUtils.replace(old, oldPrefix, newPrefix);
  }
}
