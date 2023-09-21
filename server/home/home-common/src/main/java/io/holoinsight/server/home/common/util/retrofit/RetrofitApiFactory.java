/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.retrofit;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import io.holoinsight.server.home.common.util.SSLSocketClient;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;

/**
 * 需要对每一个baseUrl做缓存
 *
 * @author jsy1001de
 * @version 1.0: DynamicApiFactory.java, v 0.1 2022年02月23日 3:12 下午 jinsong.yjs Exp $
 */
public class RetrofitApiFactory {

  private static volatile OkHttpClient mOkHttpClient;

  public static OkHttpClient getOkHttpClient() {
    if (mOkHttpClient == null) {
      synchronized (RetrofitApiFactory.class) {
        if (mOkHttpClient == null) {
          mOkHttpClient = new OkHttpClient.Builder()//
              // .authenticator(new Authenticator(token))//
              // .addInterceptor
              .readTimeout(10, TimeUnit.SECONDS) //
              .connectTimeout(10, TimeUnit.SECONDS) //
              .writeTimeout(10, TimeUnit.SECONDS)
              .connectionPool(new ConnectionPool(10, 30, TimeUnit.SECONDS))
              .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(),
                  SSLSocketClient.getX509TrustManager())
              .hostnameVerifier(SSLSocketClient.getHostnameVerifier()).build();
        }
      }
    }
    return mOkHttpClient;
  }

  @SuppressWarnings("unchecked")
  public <T, R> T create(String baseUrl, Class<T> apiClass, Class<R> retrofitServiceClass) {
    R retrofitService = createRetrofitInstance(baseUrl, retrofitServiceClass);
    return (T) Proxy.newProxyInstance(retrofitServiceClass.getClassLoader(), new Class[] {apiClass},
        new RetrofitProxy<R>(retrofitService));
  }

  public <T> T createRetrofitInstance(String baseUrl, Class<T> retrofitServiceClass) {
    Retrofit retrofit = createRetrofit(baseUrl);
    return retrofit.create(retrofitServiceClass);
  }

  protected Retrofit createRetrofit(String baseUrl) {
    OkHttpClient client = getOkHttpClient();
    return new Retrofit.Builder() //
        .baseUrl(baseUrl).client(client).addConverterFactory(GsonConverterFactory.create()) //
        .build();
  }

  @SuppressWarnings("unchecked")
  public <T, R> T create(String baseUrl, Supplier<Gson> gsonConsumer, Class<T> apiClass,
      Class<R> retrofitServiceClass) {
    R retrofitService = createRetrofitInstance(baseUrl, gsonConsumer, retrofitServiceClass);
    return (T) Proxy.newProxyInstance(retrofitServiceClass.getClassLoader(), new Class[] {apiClass},
        new RetrofitProxy<R>(retrofitService));
  }

  public <T> T createRetrofitInstance(String baseUrl, Supplier<Gson> gsonConsumer,
      Class<T> retrofitServiceClass) {
    Retrofit retrofit = createRetrofit(baseUrl, gsonConsumer);
    return retrofit.create(retrofitServiceClass);
  }

  protected Retrofit createRetrofit(String baseUrl, Supplier<Gson> gsonConsumer) {
    OkHttpClient client = getOkHttpClient();
    return new Retrofit.Builder() //
        .baseUrl(baseUrl).client(client)
        .addConverterFactory(GsonConverterFactory.create(gsonConsumer.get())) //
        .build();
  }
}
