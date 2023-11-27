/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access;

import com.alibaba.fastjson.JSON;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.access.model.MonitorTokenData;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.common.util.scope.RequestContext.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorAccessService.java, v 0.1 2022年06月10日 10:23 上午 jinsong.yjs Exp $
 */
@Slf4j
@Component
public class MonitorAccessService {

  @Autowired
  private AccessConfigService accessConfigService;

  /**
   * 申请token，如果入参非法报错
   *
   * @param accessId
   * @param accessKey
   * @return
   */
  public String apply(String accessId, String accessKey) {

    final MonitorAccessConfig accessConfig =
        accessConfigService.getAccessConfigDOMap().get(accessKey);

    if (accessConfig == null) {
      throw new IllegalArgumentException("accessId is illegal, " + accessId);
    }

    if (!StringUtils.equals(accessKey, accessConfig.getAccessKey())) {
      throw new IllegalArgumentException("accessKey is invalid");
    }

    try {
      // AES专用密钥
      final String aesKey = accessConfigService.getTokenAesKey();
      final SecretKeySpec key = new SecretKeySpec(Hex.decodeHex(aesKey.toCharArray()), "AES");

      // 密码器
      final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key);

      final MonitorTokenData tokenData =
          new MonitorTokenData().setAccessId(accessId).setAccessKey(accessConfig.getAccessKey())
              .setTenant(accessConfig.getTenant()).setTime(System.currentTimeMillis());

      final byte[] token =
          cipher.doFinal(JSON.toJSONString(tokenData).getBytes(StandardCharsets.UTF_8));

      return new String(Hex.encodeHex(token));
    } catch (Exception e) {
      throw new RuntimeException("apply token fail", e);
    }
  }

  /**
   * 解析token，并检验是否过期
   *
   * @param token
   * @return
   */
  public MonitorTokenData checkWithExpire(String token, long expireInSecond) {
    final MonitorTokenData tokenData = check(token);

    if (System.currentTimeMillis() - tokenData.time > expireInSecond * 1000) {
      throw new RuntimeException("token expired, " + tokenData);
    }

    return tokenData;
  }

  public Boolean tokenExpire(HttpServletRequest req, String token, long expireInSecond) {
    final MonitorTokenData tokenData = check(token);

    if (System.currentTimeMillis() - tokenData.time <= expireInSecond * 1000) {
      MonitorAccessConfig monitorAccessConfig =
          accessConfigService.getAccessConfigDOMap().get(tokenData.accessKey);

      MonitorScope monitorScope = new MonitorScope();
      monitorScope.tenant = tokenData.tenant;
      monitorScope.accessId = tokenData.accessId;
      monitorScope.accessKey = tokenData.accessKey;
      monitorScope.accessConfig = monitorAccessConfig;
      Context c = new Context(monitorScope);
      RequestContext.setContext(c);
      req.setAttribute(MonitorUser.MONITOR_USER, MonitorUser.newTokenUser(tokenData.accessKey));
      return false;
    }
    return true;
  }

  public Boolean accessCheck(String accessKey) {

    MonitorAccessConfig monitorAccessConfig =
        accessConfigService.getAccessConfigDOMap().get(accessKey);

    if (null == monitorAccessConfig) {
      return false;
    }

    MonitorScope monitorScope = new MonitorScope();
    monitorScope.tenant = monitorAccessConfig.getTenant();
    monitorScope.workspace = monitorAccessConfig.getWorkspace();
    monitorScope.accessId = monitorAccessConfig.getAccessId();
    monitorScope.accessKey = monitorAccessConfig.getAccessKey();
    monitorScope.accessConfig = monitorAccessConfig;
    Context c = new Context(monitorScope);
    RequestContext.setContext(c);

    return true;
  }

  /**
   * 解析token，并检验是否过期
   *
   * @param token
   * @return
   */
  public MonitorTokenData check(String token) {
    if (StringUtils.isBlank(token)) {
      throw new IllegalArgumentException("token is empty");
    }

    try {
      // AES专用密钥
      final String aesKey = accessConfigService.getTokenAesKey();
      final SecretKeySpec key = new SecretKeySpec(Hex.decodeHex(aesKey.toCharArray()), "AES");

      // 密码器
      final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key);

      final byte[] tokenData = cipher.doFinal(Hex.decodeHex(token.toCharArray()));

      return JSON.parseObject(new String(tokenData, StandardCharsets.UTF_8),
          MonitorTokenData.class);
    } catch (Exception e) {
      throw new RuntimeException("token invalid", e);
    }
  }
}
