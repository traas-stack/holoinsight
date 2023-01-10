/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service;

import io.grpc.ManagedChannelBuilder;
import io.holoinsight.server.meta.common.integration.ClientService;
import io.holoinsight.server.meta.common.integration.impl.ClientServiceImpl;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author jsy1001de
 * @version 1.0: AbstractCacheInteractService.java, v 0.1 2022年03月07日 5:25 下午 jinsong.yjs Exp $
 */
public abstract class AbstractCacheInteractService {

  private static final Logger logger = LoggerFactory.getLogger(AbstractCacheInteractService.class);

  private static final int MAX_RETRY = 3;

  protected DimCookie cookie;

  public Set<String> domains = new HashSet<>();

  private ClientService clientService = new ClientServiceImpl();

  public AbstractCacheInteractService() {
    init();
  }


  protected void init() {
    logger.info("AbstractCacheInteractService init...");
    Runnable runnable = () -> {
      while (true) {
        try {
          DimCookie cookie = pickOneCookie();
          if (cookie == null) {
            logger.warn("no cookie available.");
          } else if (!healthCheck()) {
            logger.warn("heartBeat fail, cookie={}.", cookie);
          }
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        } finally {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
    };

    new Thread(runnable, "COOKIE-HEALTH-CHECK").start();
  }

  protected DimCookie pickOneCookie() {
    if (this.cookie == null || this.cookie.isExpired()) {
      resetCookie();
    }
    return this.cookie;
  }

  protected <T> T tryUntilSuccess(Function<DimCookie, T> function, String method, int i) {
    try {
      DimCookie cookie = pickOneCookie();
      T t = function.apply(cookie);
      cookie.updateLastAvailableTime();
      return t;
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
      if (i >= MAX_RETRY) {
        String message = String.format(
            "request server fail and reach the max retry times %s, server=%s, method=%s, msg=%s.",
            MAX_RETRY, cookie == null ? null : cookie.getServer(), method, e.getMessage());
        logger.error(message, e);
        throw new RuntimeException(message, e);
      }
      if (this.cookie != null) {
        this.cookie.setExpired(true);
      }
      try {
        Thread.sleep(new Random().nextInt(10000));
      } catch (InterruptedException e1) {
        logger.error(e1.getMessage(), e1);
      }
      return tryUntilSuccess(function, method, i + 1);
    }
  }

  protected synchronized void resetCookie() {
    logger.info("reset cookie start, channel authority={}",
        this.cookie == null ? null : this.cookie.getChannel().authority());
    if (this.cookie != null && this.cookie.isExpired()) {
      this.cookie.destory();
    }
    DimCookie newCookie = initCookie();
    this.cookie = newCookie;
    logger.info("reset cookie finish, channel authority={}",
        this.cookie == null ? null : this.cookie.getChannel().authority());
  }

  protected DimCookie initCookie() {
    List<DimCookie> cookies = new ArrayList<>();
    List<String> servers = getServers();
    if (CollectionUtils.isEmpty(servers)) {
      logger.error("init cookie, available servers={}.", servers);
    } else {
      logger.info("init cookie, available servers={}.", servers);
    }

    if (CollectionUtils.isEmpty(servers)) {
      return cookie;
    }
    for (String serverIp : servers) {
      try {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverIp, getPort()) //
            .usePlaintext() //
            .maxInboundMessageSize(10 * 10 * 1024 * 1024) //
            .build(); //

        // final ManagedChannel channel = NettyChannelBuilder.forAddress(serverIp, getPort())
        // .usePlaintext().withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
        // .maxInboundMessageSize(10 * 10 * 1024 * 1024)
        // .channelType(NioSocketChannel.class).build();

        cookies.add(new DimCookie(serverIp, getPort(), channel));
      } catch (Exception e) {
        throw e;
      }
    }
    DimCookie cookie = cookies.get(Math.abs(new Random().nextInt(cookies.size())));
    logger.info("init cookie, pick cookie={}.", cookie);
    // destroy other cookies
    for (DimCookie c : cookies) {
      if (c != cookie) {
        c.destory();
      }
    }
    return cookie;
  }

  private List<String> getServers() {
    if (!CollectionUtils.isEmpty(domains)) {
      return new ArrayList<>(domains);
    } else if (clientService.getDomain() != null) {
      return Collections.singletonList(clientService.getDomain());
    } else {
      return clientService.getCacheServers();
    }
  }

  public abstract int getPort();

  public abstract boolean healthCheck();
}
