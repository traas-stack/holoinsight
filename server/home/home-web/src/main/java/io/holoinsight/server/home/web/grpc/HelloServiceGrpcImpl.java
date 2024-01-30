/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.grpc;

import io.holoinsight.server.home.proto.hello.HelloRequest;
import io.holoinsight.server.home.proto.hello.HelloResponse;
import io.holoinsight.server.home.proto.hello.HelloServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 *
 * @author jsy1001de
 * @version 1.0: HelloServiceGrpcImpl.java, v 0.1 2022年06月13日 5:42 下午 jinsong.yjs Exp $
 */
@GrpcService
@Slf4j
public class HelloServiceGrpcImpl extends HelloServiceGrpc.HelloServiceImplBase {

  public void hello(HelloRequest request,
      io.grpc.stub.StreamObserver<HelloResponse> responseObserver) {
    // System.out.println(request);

    String greeting = "Hi " + request.getName() + " you are " + request.getAge() + " years old"
        + " your hoby is " + (request.getHobbiesList()) + " your tags " + request.getTagsMap();

    HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();

    log.info("end add");
  }
}
