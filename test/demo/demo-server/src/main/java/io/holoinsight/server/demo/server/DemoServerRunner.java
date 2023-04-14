package io.holoinsight.server.demo.server;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

/**
 * <p>
 * created at 2023/4/5
 *
 * @author xzchaoo
 */
@Component
public class DemoServerRunner implements ApplicationRunner {
  @Override
  public void run(ApplicationArguments args) throws Exception {
    Server server = NettyServerBuilder.forPort(8001) //
        .addService(new DemoServiceImpl()) //
        .build(); //
    server.start();
  }
}
