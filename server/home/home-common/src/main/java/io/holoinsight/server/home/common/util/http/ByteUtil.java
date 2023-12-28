/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ByteUtil {

  public static byte[] getBytes(short data) {
    byte[] bytes = new byte[2];
    bytes[0] = (byte) (data & 0xff);
    bytes[1] = (byte) ((data & 0xff00) >> 8);
    return bytes;
  }

  public static byte[] getBytes(char data) {
    byte[] bytes = new byte[2];
    bytes[0] = (byte) (data);
    bytes[1] = (byte) (data >> 8);
    return bytes;
  }

  public static byte[] getBytes(int data) {
    byte[] bytes = new byte[4];
    getBytes(data, bytes, 0);
    return bytes;
  }

  public static byte[] getBytes(long data) {
    byte[] bytes = new byte[8];
    bytes[0] = (byte) (data & 0xff);
    bytes[1] = (byte) ((data >> 8) & 0xff);
    bytes[2] = (byte) ((data >> 16) & 0xff);
    bytes[3] = (byte) ((data >> 24) & 0xff);
    bytes[4] = (byte) ((data >> 32) & 0xff);
    bytes[5] = (byte) ((data >> 40) & 0xff);
    bytes[6] = (byte) ((data >> 48) & 0xff);
    bytes[7] = (byte) ((data >> 56) & 0xff);
    return bytes;
  }

  public static byte[] getBytes(byte[] bytes, int offset, int length) {
    byte[] ret = new byte[length];
    System.arraycopy(bytes, offset, ret, 0, length);
    return ret;
  }

  public static void getBytes(long data, byte[] bytes, int offset) {
    bytes[0 + offset] = (byte) (data & 0xff);
    bytes[1 + offset] = (byte) ((data >> 8) & 0xff);
    bytes[2 + offset] = (byte) ((data >> 16) & 0xff);
    bytes[3 + offset] = (byte) ((data >> 24) & 0xff);
    bytes[4 + offset] = (byte) ((data >> 32) & 0xff);
    bytes[5 + offset] = (byte) ((data >> 40) & 0xff);
    bytes[6 + offset] = (byte) ((data >> 48) & 0xff);
    bytes[7 + offset] = (byte) ((data >> 56) & 0xff);
  }

  public static void getBytes(int data, byte[] bytes, int offset) {
    bytes[0 + offset] = (byte) (data & 0xff);
    bytes[1 + offset] = (byte) ((data >> 8) & 0xff);
    bytes[2 + offset] = (byte) ((data >> 16) & 0xff);
    bytes[3 + offset] = (byte) ((data >> 24) & 0xff);
  }

  public static byte[] getBytes(float data) {
    int intBits = Float.floatToIntBits(data);
    return getBytes(intBits);
  }

  public static void getBytes(float data, byte[] bytes, int offset) {
    int intBits = Float.floatToIntBits(data);
    getBytes(intBits, bytes, offset);
  }

  public static byte[] getBytes(double data) {
    long intBits = Double.doubleToLongBits(data);
    return getBytes(intBits);
  }

  public static void main(String[] args) {
    // System.err.println(getDouble(new byte[] {0, 0, 0, 0, 0, -32, 127, 64}, 0));
    // System.err.println(getDouble(new byte[] {0, 0, 0, 0, 0, 48, 112, 64}, 0));
  }

  public static void getBytes(double data, byte[] bs, int offset) {
    long intBits = Double.doubleToLongBits(data);
    getBytes(intBits, bs, offset);
  }

  public static void getBytes(byte[] from, byte[] bs, int offset) {
    System.arraycopy(from, 0, bs, offset, from.length);
  }

  public static void copyBytes(byte[] from, int fromOffset, int fromLength, byte[] to,
      int toOffset) {
    // for (int k = 0; k < fromLength; k++) {
    // to[toOffset + k] = from[fromOffset + k];
    // }
    System.arraycopy(from, fromOffset, to, toOffset, fromLength);
  }

  public static byte[] getBytes(String data, String charsetName) {
    Charset charset = Charset.forName(charsetName);
    return data.getBytes(charset);
  }

  public static byte[] getBytes(String data) {
    return getBytes(data, "GBK");
  }

  public static short getShort(byte[] bytes, int offset) {
    return (short) ((0xff & bytes[offset + 0]) | (0xff00 & (bytes[offset + 1] << 8)));
  }

  public static char getChar(byte[] bytes) {
    return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
  }

  public static int getInt(byte[] bytes, int offset) {
    return (0xff & bytes[offset + 0]) | (0xff00 & (bytes[offset + 1] << 8))
        | (0xff0000 & (bytes[offset + 2] << 16)) | (0xff000000 & (bytes[offset + 3] << 24));
  }

  public static long getLong(byte[] bytes, int offset) {
    return (0xffL & (long) bytes[offset + 0]) | (0xff00L & ((long) bytes[offset + 1] << 8))
        | (0xff0000L & ((long) bytes[offset + 2] << 16))
        | (0xff000000L & ((long) bytes[offset + 3] << 24))
        | (0xff00000000L & ((long) bytes[offset + 4] << 32))
        | (0xff0000000000L & ((long) bytes[offset + 5] << 40))
        | (0xff000000000000L & ((long) bytes[offset + 6] << 48))
        | (0xff00000000000000L & ((long) bytes[offset + 7] << 56));
  }

  public static float getFloat(byte[] bytes, int offset) {
    return Float.intBitsToFloat(getInt(bytes, offset));
  }

  public static double getDouble(byte[] bytes, int offset) {
    long l = getLong(bytes, offset);
    return Double.longBitsToDouble(l);
  }

  public static String getString(byte[] bytes, int offset, int len) {
    return new String(bytes, offset, len);
  }

  public static byte[] cloneBytes(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    byte[] ret = new byte[bytes.length];
    System.arraycopy(bytes, 0, ret, 0, bytes.length);
    return ret;
  }

  // 鎵惧埌绗竴娆″嚭鐜扮殑浣嶇疆
  public static int findIndex(byte[] bs, byte b) {
    int index = 0;
    for (byte by : bs) {
      if (by == b) {
        return index;
      }
      index++;
    }
    return -1;
  }

  public static boolean equals(byte[] b1, byte[] b2) {
    if (b1 == b2)
      return true;
    if (b1 == null)
      return false;

    if (b1.length != b2.length)
      return false;
    for (int i = 0; i < b1.length; i++) {
      if (b1[i] != b2[i])
        return false;
    }
    return true;
  }

  public static byte[] readStream(InputStream inStream) throws Exception {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = -1;
    while ((len = inStream.read(buffer)) != -1) {
      outStream.write(buffer, 0, len);
    }
    outStream.close();
    return outStream.toByteArray();
  }
}
