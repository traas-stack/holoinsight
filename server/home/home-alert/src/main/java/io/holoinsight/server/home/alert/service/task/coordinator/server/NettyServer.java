/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator.server;


import io.holoinsight.server.home.alert.service.task.coordinator.CoordinatorReceiver;
import io.holoinsight.server.home.alert.service.task.coordinator.CoordinatorService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * {@link NettyServer} is the TCP server class.
 */
public class NettyServer {

  private final EventLoopGroup bossLoopGroup;

  private final EventLoopGroup workerLoopGroup;

  private final ChannelGroup channelGroup;

  private CoordinatorService service;

  /**
   * Initialize the netty server class
   *
   * @param service
   */
  public NettyServer(CoordinatorService service) {
    // Initialization private members

    this.bossLoopGroup = new NioEventLoopGroup();

    this.workerLoopGroup = new NioEventLoopGroup();

    this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    this.service = service;
  }


  /**
   * Startup the TCP server
   *
   * @param port port of the server
   * @throws Exception if any {@link Exception}
   */
  public final void startup(int port) throws Exception {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossLoopGroup, workerLoopGroup).channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.AUTO_CLOSE, true)
        .option(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            // LineBasedFrameDecoder按行分割消息
            pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
            // 再按UTF-8编码转成字符串
            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

            pipeline.addLast(new CoordinatorReceiver(service));
          }
        });

    try {
      ChannelFuture channelFuture = bootstrap.bind(port).sync();
      channelGroup.add(channelFuture.channel());
    } catch (Exception e) {
      shutdown();
      throw e;
    }
  }

  /**
   * Shutdown the server
   *
   * @throws Exception
   */
  public final void shutdown() throws Exception {
    channelGroup.close();
    bossLoopGroup.shutdownGracefully();
    workerLoopGroup.shutdownGracefully();
  }

}
