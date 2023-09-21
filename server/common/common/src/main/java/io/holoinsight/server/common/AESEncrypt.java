/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author jsy1001de
 * @version 1.0: AESEncrypt.java, Date: 2023-07-05 Time: 15:15
 */
@Slf4j
public class AESEncrypt {

  private static final String aesKey = "holoinsight12345";

  public static String encrypt(String input) {
    try {
      Cipher cipher = Cipher.getInstance("AES");
      SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      byte[] encrypted = cipher.doFinal(input.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      return null;
    }
  }
}
