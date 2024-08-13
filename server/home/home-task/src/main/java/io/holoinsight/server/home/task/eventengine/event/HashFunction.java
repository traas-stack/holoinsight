/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.event;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希算法模型
 * 
 * @author jsy1001de
 * @version 1.0: HashFunction.java, Date: 2024-03-14 Time: 15:15
 */
public class HashFunction {
  private MessageDigest md5 = null;

  /**
   *
   */
  public long hash(String key) {
    if (md5 == null) {
      try {
        md5 = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("no md5 algorythm found");
      }
    }

    md5.reset();
    md5.update(key.getBytes());
    byte[] bKey = md5.digest();
    long res = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16)
        | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
    return res & 0xffffffffL;
  }
}
