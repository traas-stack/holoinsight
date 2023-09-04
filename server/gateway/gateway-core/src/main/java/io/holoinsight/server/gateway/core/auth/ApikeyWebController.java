/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.common.ctl.MonitorProductCode;
import io.holoinsight.server.common.ctl.ProductCtlService;
import io.holoinsight.server.common.web.InternalWebApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * created at 2022/10/12
 *
 * @author sw1136562366
 */
@RestController
@InternalWebApi
public class ApikeyWebController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApikeyWebController.class);
  @Autowired
  private ApikeyAuthService apikeyAuthService;
  @Autowired
  private ProductCtlService productCtlService;

  /**
   * <p>
   * checkApikey.
   * </p>
   *
   * @param apikey
   */
  @GetMapping({"/api/apikey/check", "/internal/api/gateway/apikey/check"})
  public Map<String, String> checkApikey(@RequestParam String apikey) {
    Map<String, String> result = new HashMap<>();

    apikey = getApikey(apikey, result);

    AuthInfo authInfo = apikeyAuthService.getFromCache(apikey);
    if (authInfo != null && StringUtils.isNotBlank(authInfo.getTenant())) {
      result.put("tenant", authInfo.getTenant());
    }

    return result;
  }

  public String getApikey(String apikey, Map<String, String> result) {
    try {
      apikey = URLDecoder.decode(apikey);

      if (apikey.startsWith("extend")) {
        String[] extendInfoSplit = apikey.split("extend");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> extendInfo = mapper.readValue(extendInfoSplit[1], Map.class);
        apikey = extendInfo.get("authentication");

        if (productCtlService.productClosed(MonitorProductCode.TRACE, extendInfo)) {
          result.put("traceStatus", "false");
        }
      }
    } catch (Exception e) {
      LOGGER.error("[gateway]checkApikey error: ", e.getMessage());
    }
    return apikey;
  }

}
