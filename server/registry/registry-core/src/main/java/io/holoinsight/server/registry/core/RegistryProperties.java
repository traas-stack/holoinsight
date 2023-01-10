/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;


import lombok.Data;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@ConfigurationProperties(prefix = "holoinsight.registry")
@Data
public class RegistryProperties {
  private Server server = new Server();

  @Data
  public static class Server {
    private ForAgent forAgent = new ForAgent();
    private ForProd forProd = new ForProd();
    private ForInternal forInternal = new ForInternal();
  }

  @Data
  public static class ForInternal {
    /**
     * 内部通信端口
     */
    private int port = 7200;
  }

  /**
   * for内部产品层的端口
   */
  @Data
  public static class ForProd {
    private int port = 7201;
  }

  @Data
  public static class ForAgent {
    /**
     * for agent的端口
     */
    private int port = 7202;
    private boolean sslEnabled = false;
    private Resource serverCert;
    private Resource serverKey;
    private Resource caCert;
  }
}
