/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jinyan.ljw
 * @date 2023/3/8
 */
public class Endpoint {
  private String host;
  private int port;

  public static Endpoint of(final String host, final int port) {
    return new Endpoint(host, port);
  }

  public static Endpoint parse(final String s) {
    if (StringUtils.isBlank(s)) {
      return null;
    }

    final String[] arr = StringUtils.split(s, ':');

    if (arr == null || arr.length < 2) {
      return null;
    }

    try {
      final int port = Integer.parseInt(arr[1]);
      return Endpoint.of(arr[0], port);
    } catch (final Exception ignored) {
      return null;
    }
  }

  public Endpoint() {
    super();
  }

  public Endpoint(String address, int port) {
    super();
    this.host = address;
    this.port = port;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return this.host + ":" + this.port;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.host == null ? 0 : this.host.hashCode());
    result = prime * result + this.port;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Endpoint other = (Endpoint) obj;
    if (this.host == null) {
      if (other.host != null) {
        return false;
      }
    } else if (!this.host.equals(other.host)) {
      return false;
    }
    return this.port == other.port;
  }
}
