/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.proto.base.DataBaseResponse;
import io.holoinsight.server.common.AddressUtil;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

/**
 *
 * @author jsy1001de
 * @version 1.0: GrpcFacadeTemplateImpl.java, v 0.1 2022年06月15日 3:00 下午 jinsong.yjs Exp $
 */
@Component
@Slf4j
public class GrpcFacadeTemplate {

  public void manage(StreamObserver<DataBaseResponse> responseObserver, GrpcManageCallback callback,
      DataBaseResponse.Builder builder) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      // 检验参数
      callback.checkParameter();
      // 执行管理方法
      callback.doManage();

    } catch (Throwable t) {
      log.error(builder.getTraceId() + ", catch exception:" + t.getMessage(), t);
      builder.setSuccess(false);
      builder.setErrMsg(t.getMessage());
    } finally {
      stopWatch.stop();
      DataBaseResponse build = builder.build();
      String trace = builder.getTraceId() + ", serverResult=[" + build.getSuccess()
          + "], serverCost=[" + stopWatch.getTime() + "]";
      log.info(trace);
      builder.setTraceId(
          trace + ", targetIp=[" + AddressUtil.getLocalHostIPV4() + "], " + build.getErrMsg());
      responseObserver.onNext(build);
      responseObserver.onCompleted();
    }
  }
}
