/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

/**
 * This class is used for trace agent authentication encryption, holoinsight backend encryption,
 * holoinsight collector decryption, holoinsight backend and collector need to configure the same
 * secretKey and iv
 */
@Slf4j
public class AesUtil {

  public static final String aesTest = "holoinsight12345";
  /**
   * 加密解密算法/加密模式/填充方式
   */
  private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final String AES = "AES";

  public static String aesEncryptHex(String value, String secretKey, String iv) throws Exception {
    if (value == null || value.length() == 0) {
      return "";
    }
    Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
    byte[] buffer = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    return Hex.encodeHexString(buffer);
  }

  public static String aesDecryptHex(String value, String secretKey, String iv) throws Exception {
    if (value == null || value.length() == 0)
      return "";

    byte[] buffer = Hex.decodeHex(value);
    Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);
    buffer = cipher.doFinal(buffer);
    return new String(buffer, StandardCharsets.UTF_8);
  }

  private static Cipher initCipher(int mode, String secretKey, String iv) throws Exception {
    byte[] secretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    byte[] keyBytes = new byte[16];
    System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
    Key key = new SecretKeySpec(keyBytes, AES);

    if (iv == null || iv.length() == 0) {
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(mode, key);
      return cipher;
    } else {
      byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
      byte[] ivKeyBytes = new byte[16];
      System.arraycopy(ivBytes, 0, ivKeyBytes, 0, Math.min(ivBytes.length, ivKeyBytes.length));
      IvParameterSpec ivp = new IvParameterSpec(ivKeyBytes);
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(mode, key, ivp);
      return cipher;
    }
  }

  /**
   * AES加密，密文编码 Base64
   */
  public static String encodeBase64(String key, String iv, String content) {
    try {
      SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));

      // 获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
      byte[] byteEncode = content.getBytes(StandardCharsets.UTF_8);

      // 根据密码器的初始化方式加密
      byte[] byteAES = cipher.doFinal(byteEncode);

      // 将加密后的数据转换为字符串
      return Base64.getEncoder().encodeToString(byteAES);
    } catch (Exception e) {
      log.error("aes encode failed with err:{}", e.getMessage());
    }
    return null;
  }

  /**
   * AES解密，密文编码 Base64
   */
  public static String decodeBase64(String key, String iv, String content) {
    try {
      SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));

      // 将加密并编码后的内容解码成字节数组
      byte[] byteContent = Base64.getDecoder().decode(content);
      // 解密
      byte[] byteDecode = cipher.doFinal(byteContent);
      return new String(byteDecode, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("aes decode failed with err:{}", e.getMessage());
    }
    return null;
  }
}
