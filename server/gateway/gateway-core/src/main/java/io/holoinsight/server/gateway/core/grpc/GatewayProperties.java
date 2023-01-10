/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import lombok.Data;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
@ConfigurationProperties(prefix = "gateway")
@Data
public class GatewayProperties {
  private Server server = new Server();

  @Data
  public static class Server {
    private Grpc grpc = new Grpc();
  }

  @Data
  public static class Grpc {
    private int port = 19610;
    private boolean sslEnabled;
    private Resource serverCert;
    private Resource serverKey;
    private Resource caCert;
  }
}
