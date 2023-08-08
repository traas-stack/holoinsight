/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * This class is used for trace agent authentication encryption, holoinsight backend encryption,
 * holoinsight collector decryption, holoinsight backend and collector need to configure the same
 * secretKey and iv
 */
public class AesUtil {

  public static String aesEncrypt(String value, String secretKey, String iv) throws Exception {
    if (value == null || value.length() == 0) {
      return "";
    }
    Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
    byte[] buffer = cipher.doFinal(value.getBytes("utf-8"));
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < buffer.length; i++) {
      result.append(String.format("%02x", buffer[i]));
    }
    return result.toString();
  }

  public static String aesDecrypt(String value, String secretKey, String iv) throws Exception {
    if (value == null || value.length() == 0)
      return "";

    int length = value.length();
    byte[] buffer = new byte[length / 2];
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
    }

    Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);
    buffer = cipher.doFinal(buffer);
    return new String(buffer, "utf-8");
  }

  private static Cipher initCipher(int mode, String secretKey, String iv) throws Exception {
    byte[] secretBytes = secretKey.getBytes("utf-8");
    byte[] keyBytes = new byte[16];
    System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
    Key key = new SecretKeySpec(keyBytes, "AES");

    if (iv == null || iv.length() == 0) {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(mode, key);
      return cipher;
    } else {
      byte[] ivBytes = iv.getBytes("utf-8");
      byte[] ivKeyBytes = new byte[16];
      System.arraycopy(ivBytes, 0, ivKeyBytes, 0, Math.min(ivBytes.length, ivKeyBytes.length));
      IvParameterSpec ivp = new IvParameterSpec(ivKeyBytes);
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(mode, key, ivp);
      return cipher;
    }
  }
}
