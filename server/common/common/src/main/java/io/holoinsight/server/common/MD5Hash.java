/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * MD5Hash class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: MD5Hash.java, v 0.1 2022年02月23日 11:57 上午 jinsong.yjs Exp $
 */
public class MD5Hash {

  /**
   * <p>
   * getMD5.
   * </p>
   */
  public static String getMD5(String source) {
    if (source == null) {
      return null;
    }
    return getMD5AsHex(source.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * <p>
   * getMD5AsHex.
   * </p>
   */
  public static String getMD5AsHex(byte[] key) {
    return getMD5AsHex(key, 0, key.length);
  }

  /**
   * <p>
   * getMD5AsHex.
   * </p>
   */
  public static String getMD5AsHex(byte[] key, int offset, int length) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(key, offset, length);
      byte[] digest = md.digest();
      return new String(Hex.encodeHex(digest));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error computing MD5 hash", e);
    }
  }
}
