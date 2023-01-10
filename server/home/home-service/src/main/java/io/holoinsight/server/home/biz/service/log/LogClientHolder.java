/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import io.holoinsight.server.home.biz.common.MetaDictUtil;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author zanghaibo
 * @time 2022-08-30 7:37 下午
 */
@Component
@Slf4j
public class LogClientHolder {

  private Client slsClient;

  private HawkSigner signer;

  private String antLogUrl;

  public String getAntLogUrl() {
    if (StringUtils.isEmpty(antLogUrl)) {
      return MetaDictUtil.getMiniProgramAntLogUrl();
    }
    return antLogUrl;
  }

  public Client getSlsClient() {
    if (slsClient == null) {
      String accessId = MetaDictUtil.getMiniProgramSlsAccessID();
      String accessKey = MetaDictUtil.getMiniProgramSlsAccessKey();
      if (StringUtils.isEmpty(accessId) || StringUtils.isEmpty(accessKey)) {
        log.error("can not init sls client with empty accessId or accessKey");
        return null;
      }

      String endpoint = MetaDictUtil.getMiniProgramSlsEndPoint();
      ClientConfiguration configuration = new ClientConfiguration();
      configuration.setMaxConnections(2000);
      configuration.setConnectionTimeout(8000);
      configuration.setSocketTimeout(Consts.HTTP_SEND_TIME_OUT);
      slsClient = new Client(endpoint, accessId, accessKey, configuration);
    }
    return slsClient;
  }

  public HawkSigner getSigner() {
    if (signer == null) {
      String accessId = MetaDictUtil.getMiniProgramAntLogAccessID();
      String accessKey = MetaDictUtil.getMiniProgramAntLogAccessKey();
      if (StringUtils.isEmpty(accessId) || StringUtils.isEmpty(accessKey)) {
        log.error("can not init hawk signer with empty keyId or key");
        return null;
      }
      signer = new HawkSigner(accessId, accessKey);
    }
    return signer;
  }

}
