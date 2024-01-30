/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.retrofit;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author jsy1001de
 * @version 1.0: RetrofitProxy.java, v 0.1 2022年02月23日 3:05 下午 jinsong.yjs Exp $
 */
@Slf4j
public class RetrofitProxy<T> implements InvocationHandler {
  private T retrofitService;

  public RetrofitProxy(T retrofitService) {
    super();
    this.retrofitService = retrofitService;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    long start = System.currentTimeMillis();
    printBefore(method, args);
    Method retrofitMethod =
        retrofitService.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
    Object ret = retrofitMethod.invoke(retrofitService, args);
    if (ret instanceof Call) {
      Call<?> call = (Call<?>) ret;

      // 同步
      Response<?> response = call.execute();

      printAfter(start, call.request().url().uri().toString(), method, response);
      if (response.code() == 404 || response.code() == 403) {
        throw new MonitorException(ResultCodeEnum.DOWNSTREAM_AUTH_SYSTEM_ERROR,
            "Authenticate failed, " + response);
      }
      if (response.isSuccessful()) {
        return response.body();
      }

      // 其他都是异常处理, 可以根据不同错误码单独处理
      String body = J.toJson(response.body());
      log.error("Response of {}, error: {}", call.request().url().uri().toString(), body);
      throw new MonitorException(ResultCodeEnum.DOWNSTREAM_SYSTEM_ERROR,
          "call failed, " + response.code() + ", " + body);
    } else {
      return ret;
    }
  }

  protected void printBefore(Method method, Object[] args) {
    StringBuilder invokeInfo =
        new StringBuilder("Invoke INFO: method=[" + method.getName() + "], ");

    if (null != args && args.length > 0) {
      invokeInfo.append("Invoke with ").append(args.length).append(" params: ");
      for (Object param : args) {
        invokeInfo.append("[").append(param).append("] ");
      }
    } else {
      invokeInfo.append("Invoke with 0 params.");
    }
    log.warn(invokeInfo.toString());
  }

  protected void printAfter(long start, String url, Method method, Response response) {

    log.warn("Invoke INFO: method={}, url={}, cost={}, " + "result={}, code={}", method.getName(),
        url, (System.currentTimeMillis() - start), response.isSuccessful(), response.code());
  }

}
