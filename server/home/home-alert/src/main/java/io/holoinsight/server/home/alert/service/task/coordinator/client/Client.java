/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * Client is the Netty TCP client.
 * 
 */
@Slf4j
public class Client {
  int port;
  String ip;
  Channel channel;
  EventLoopGroup workGroup = new NioEventLoopGroup();

  /**
   * Constructor
   * 
   * @param port {@link Integer} port of server
   */
  public Client(int port, String ip) {
    this.port = port;
    this.ip = ip;
  }

  /**
   * Startup the client
   * 
   * @return {@link ChannelFuture}
   * @throws Exception
   */
  public ChannelFuture startup() throws Exception {
    try {
      Bootstrap b = new Bootstrap();
      b.group(workGroup);
      b.channel(NioSocketChannel.class);
      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.handler(new ChannelInitializer<SocketChannel>() {
        protected void initChannel(SocketChannel socketChannel) throws Exception {
          socketChannel.pipeline().addLast(new NettyHandler());
        }
      });
      ChannelFuture channelFuture = b.connect(ip, this.port).sync();
      this.channel = channelFuture.channel();

      return channelFuture;
    } finally {
      log.info("client startup.");
    }
  }

  /**
   * Shutdown a client
   */
  public void shutdown() {
    workGroup.shutdownGracefully();
  }
}
