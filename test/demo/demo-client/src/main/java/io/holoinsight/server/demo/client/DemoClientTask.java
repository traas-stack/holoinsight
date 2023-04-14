package io.holoinsight.server.demo.client;

import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.holoinsight.server.demo.server.grpc.BarRequest;
import io.holoinsight.server.demo.server.grpc.DemoServiceGrpc;
import io.holoinsight.server.demo.server.grpc.FooRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import redis.clients.jedis.Jedis;

/**
 * <p>
 * created at 2023/3/30
 *
 * @author xzchaoo
 */
@Component
@Slf4j
public class DemoClientTask {

  @Scheduled(fixedDelay = 1000L)
  public void callDemoServer() {
    OkHttpClient c = new OkHttpClient.Builder().build();
    try {
      Request request = new Request.Builder() //
          .get() //
          .url("http://demo-server:8080/demo-server") //
          .build(); //
      try (Response resp = c.newCall(request).execute()) {
        log.info("okhttp code=[{}] content=[{}]", resp.code(), resp.body().string());
      }

      // Disabled now. Because this will lead to confusing in ApmCallLinkDetailIT.
      if (false) {
        try (CloseableHttpClient chc = HttpClients.createDefault()) {
          HttpUriRequest hur = RequestBuilder.get("http://demo-server:8080/demo-server").build();
          try (CloseableHttpResponse resp = chc.execute(hur)) {
            int statusCode = resp.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            log.info("httpclient code=[{}] content=[{}]", statusCode, content);
          }
        }
      }

      try (Jedis jedis = new Jedis("demo-redis", 6379)) {
        Long value = jedis.incr("demo.client.calls");
        log.info("demo.client.calls=[{}]", value);
      }

      ManagedChannel channel =
          ManagedChannelBuilder.forAddress("demo-server", 8001).usePlaintext().build();
      try {
        DemoServiceGrpc.newBlockingStub(channel).foo(FooRequest.getDefaultInstance());
        DemoServiceGrpc.newBlockingStub(channel).bar(BarRequest.getDefaultInstance());
      } finally {
        channel.shutdown();
      }

    } catch (Exception e) {
      log.error("fail to call demo server", e);
    }
  }
}
