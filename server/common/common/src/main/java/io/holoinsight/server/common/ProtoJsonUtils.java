/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

/**
 * <p>
 * created at 2022/12/14
 *
 * @author xzchaoo
 */
public class ProtoJsonUtils {
  private static final JsonFormat.Printer PRINTER =
      JsonFormat.printer().omittingInsignificantWhitespace().includingDefaultValueFields();

  /**
   * <p>
   * toJson.
   * </p>
   */
  public static String toJson(MessageOrBuilder msg) {
    try {
      return PRINTER.print(msg);
    } catch (InvalidProtocolBufferException e) {
      throw new IllegalStateException("protobuf json printer error", e);
    }
  }
}
