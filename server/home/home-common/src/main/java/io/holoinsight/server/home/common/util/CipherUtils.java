/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程安全的编解码工具类
 * 
 * @author jsy1001de
 * @version 1.0: CipherUtils.java, v 0.1 2022年05月19日 5:58 下午 jinsong.yjs Exp $
 */
@Slf4j
public class CipherUtils {

  private static final String SHA1PRNG = "SHA1PRNG";
  private static final String AES = "AES";
  public static ThreadLocal<Cipher> enc = new ThreadLocal<Cipher>();
  public static ThreadLocal<Cipher> dec = new ThreadLocal<Cipher>();
  private static String seed;

  public static void setSeed(String str) {
    seed = str;
  }

  public static Cipher getEnc() {
    Cipher cipher = enc.get();
    if (cipher == null) {
      cipher = getCipher(seed, true);
      enc.set(cipher);
    }
    return cipher;
  }

  public static Cipher getDec() {
    Cipher cipher = dec.get();
    if (cipher == null) {
      cipher = getCipher(seed, false);
      dec.set(cipher);
    }
    return cipher;
  }

  private static Cipher getCipher(String seed, boolean isEncrypt) {
    Cipher cipher = null;
    try {
      KeyGenerator clientkgen = KeyGenerator.getInstance(AES);
      SecureRandom clientSecureRandom = SecureRandom.getInstance(SHA1PRNG);
      clientSecureRandom.setSeed(seed.getBytes());
      clientkgen.init(128, clientSecureRandom);

      SecretKey clientSecretKey = clientkgen.generateKey();
      byte[] clientEnCodeFormat = clientSecretKey.getEncoded();
      SecretKeySpec clientKeySpec = new SecretKeySpec(clientEnCodeFormat, AES);
      cipher = Cipher.getInstance(AES);
      if (isEncrypt)
        cipher.init(Cipher.ENCRYPT_MODE, clientKeySpec);
      else
        cipher.init(Cipher.DECRYPT_MODE, clientKeySpec);
    } catch (Exception e) {
      log.error("happens error when getCipher", e);
    }
    return cipher;
  }
}
