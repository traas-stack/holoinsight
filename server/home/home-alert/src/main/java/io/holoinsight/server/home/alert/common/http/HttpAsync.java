/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common.http;

import lombok.Getter;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.LinkedList;
import java.util.List;

/**
 * 异步的HTTP请求对象，可设置代理
 */
public class HttpAsync {

  @Getter
  public static class HttpConfig {

    public static int DEFALUT_CONNECTION_TIMEOUT = 2000;

    public static int DEFALUT_SOCKET_TIMEOUT = 5000;

    private int socketTimeout = DEFALUT_SOCKET_TIMEOUT; // 两个报文之间的间隔时间

    private int connectTimeout = DEFALUT_CONNECTION_TIMEOUT; // 连接超时

    private int connectRequestTimeout = DEFALUT_CONNECTION_TIMEOUT; // 连接池获取连接的时间

    private int poolSize = 3000; // 连接池最大连接数

    private int maxPerRoute = 1500; // 每个主机的并发最多只有1500

    private String username;

    private String password;

    private boolean auth = false;

    private String proxyHost;

    private int proxyPort;

    private boolean proxy = false;

    public void proxy(String host, int port) {
      proxy = true;
      this.proxyHost = host;
      this.proxyPort = port;
    }

    public void auth(String username, String password) {
      auth = true;
      this.username = username;
      this.password = password;
    }

    public void connectionRequestTimeout(int timeout) {
      this.connectRequestTimeout = timeout;
    }

    public void socketTimeout(int socketTimeout) {
      this.socketTimeout = socketTimeout;
    }

    public void connectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
    }

    public void poolSize(int poolSize) {
      this.poolSize = poolSize;
    }

    public void maxPerRoute(int maxPerRoute) {
      this.maxPerRoute = maxPerRoute;
    }

    public HttpAsyncClient build() throws Exception {
      return new HttpAsyncClient(this, createAsyncClient());
    }

    private CloseableHttpAsyncClient createAsyncClient()
        throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
        KeyStoreException, MalformedChallengeException, IOReactorException {
      RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
          .setSocketTimeout(socketTimeout).build();
      SSLContext sslcontext = SSLContexts.createDefault();
      // 设置协议http和https对应的处理socket链接工厂的对象
      Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder
          .<SchemeIOSessionStrategy>create().register("http", NoopIOSessionStrategy.INSTANCE)
          .register("https", new SSLIOSessionStrategy(sslcontext)).build();

      // 配置io线程
      IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
          .setIoThreadCount(Runtime.getRuntime().availableProcessors()).build();
      // 设置连接池大小
      ConnectingIOReactor ioReactor;
      ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
      PoolingNHttpClientConnectionManager conMgr = new PoolingNHttpClientConnectionManager(
          ioReactor, null, sessionStrategyRegistry, (DnsResolver) null);
      if (poolSize > 0) {
        conMgr.setMaxTotal(poolSize);
      }
      if (maxPerRoute > 0) {
        conMgr.setDefaultMaxPerRoute(maxPerRoute);
      } else {
        conMgr.setDefaultMaxPerRoute(10);
      }
      ConnectionConfig connectionConfig =
          ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
              .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();

      Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
          .register(AuthSchemes.BASIC, new BasicSchemeFactory())
          .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
          .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
          .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
          .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory()).build();
      conMgr.setDefaultConnectionConfig(connectionConfig);
      HttpAsyncClientBuilder builder = HttpAsyncClients.custom().setConnectionManager(conMgr)
          .setDefaultAuthSchemeRegistry(authSchemeRegistry)
          .setDefaultCookieStore(new BasicCookieStore()).setDefaultRequestConfig(requestConfig)
          .setRedirectStrategy(new LaxRedirectStrategy());

      if (auth) {
        UsernamePasswordCredentials credentials =
            new UsernamePasswordCredentials(username, password);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        builder.setDefaultCredentialsProvider(credentialsProvider);
      }
      if (proxy) {
        builder.setProxy(new HttpHost(proxyHost, proxyPort));
      }
      return builder.build();
    }
  }

  public static interface HttpAsyncHandler {

    public void handle(XHttpResponse rep);

    public void onFail(String msg, Exception e);

  }

  public static interface HttpAsyncRetryHandler {

    public void handle(XHttpResponse rep);

    public void onFail(String msg, Exception e, int retryCnt, int maxRetryTimes);
  }

  public static class HttpAsyncClient {
    private HttpAsyncClient(HttpConfig httpConfig, CloseableHttpAsyncClient asyncHttpClient) {
      this.config = httpConfig;
      this.asyncHttpClient = asyncHttpClient;
      this.asyncHttpClient.start();
    }

    private CloseableHttpAsyncClient asyncHttpClient;
    @Getter
    private HttpConfig config;

    public void request(XHttpRequest req, HttpAsyncHandler handler) {
      try {
        HttpRequestBase requestBase = buildHttpRequestBase(req);
        HttpClientContext localContext = HttpClientContext.create();
        BasicCookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        asyncHttpClient.execute(requestBase, localContext, new FutureCallback<HttpResponse>() {
          @Override
          public void failed(Exception ex) {
            try {
              handler.onFail(req.toString(), ex);
            } finally {
              requestBase.releaseConnection();
            }
          }

          @Override
          public void completed(HttpResponse response) {
            try {
              HttpEntity entity = response.getEntity();
              String headerEtag = null;
              if (response.containsHeader("ETag")) {
                Header etagHeader = response.getFirstHeader("ETag");
                if (etagHeader != null) {
                  headerEtag = etagHeader.getValue();
                }
              }
              String headerLastModify = null;
              if (response.containsHeader("Last-Modified")) {
                Header lastModifyHeader = response.getFirstHeader("Last-Modified");
                if (lastModifyHeader != null) {
                  headerLastModify = lastModifyHeader.getValue();
                }
              }
              byte[] byteResponse = EntityUtils.toByteArray(entity);
              XHttpResponse rep = new XHttpResponse(response.getStatusLine().getStatusCode(),
                  byteResponse == null ? EntityUtils.toString(entity) : null, headerEtag,
                  headerLastModify);
              rep.byteResponse = byteResponse;
              handler.handle(rep);
            } catch (Exception e) {
              failed(e);
            } finally {
              requestBase.releaseConnection();
            }
          }

          @Override
          public void cancelled() {
            failed(new RuntimeException("The reques is cancelled."));
          }
        });

      } catch (Exception e) {
        handler.onFail(e.getMessage(), e);
      }
    }

    public void request(XHttpRequest req, HttpAsyncRetryHandler handler, int retryCnt,
        int maxRetryTimes) {
      try {
        HttpRequestBase requestBase = buildHttpRequestBase(req);
        HttpClientContext localContext = HttpClientContext.create();
        BasicCookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        asyncHttpClient.execute(requestBase, localContext, new FutureCallback<HttpResponse>() {
          @Override
          public void failed(Exception ex) {
            try {
              handler.onFail(req.toString(), ex, retryCnt, maxRetryTimes);
            } finally {
              requestBase.releaseConnection();
            }
          }

          @Override
          public void completed(HttpResponse response) {
            try {
              HttpEntity entity = response.getEntity();
              String headerEtag = null;
              if (response.containsHeader("ETag")) {
                Header etagHeader = response.getFirstHeader("ETag");
                if (etagHeader != null) {
                  headerEtag = etagHeader.getValue();
                }
              }
              String headerLastModify = null;
              if (response.containsHeader("Last-Modified")) {
                Header lastModifyHeader = response.getFirstHeader("Last-Modified");
                if (lastModifyHeader != null) {
                  headerLastModify = lastModifyHeader.getValue();
                }
              }
              byte[] byteResponse = EntityUtils.toByteArray(entity);
              XHttpResponse rep = new XHttpResponse(response.getStatusLine().getStatusCode(),
                  byteResponse == null ? EntityUtils.toString(entity) : null, headerEtag,
                  headerLastModify);
              rep.byteResponse = byteResponse;
              handler.handle(rep);
            } catch (Exception e) {
              failed(e);
            } finally {
              requestBase.releaseConnection();
            }
          }

          @Override
          public void cancelled() {
            failed(new RuntimeException("The reques is cancelled."));
          }
        });

      } catch (Exception e) {
        handler.onFail(e.getMessage(), e, retryCnt, maxRetryTimes);
      }
    }

    private byte[] download(HttpEntity entity) throws Exception {
      long length = entity.getContentLength();
      if (length <= 0) {
        return null;
      }
      InputStream in = entity.getContent();
      return ByteUtil.readStream(in);
    }

    private HttpRequestBase buildHttpRequestBase(XHttpRequest req) throws Exception {
      HttpRequestBase base = doBuildHttpRequestBase(req);
      base.setConfig(buildTimeOutConfig(req.timeoutMillisecond));
      if (req.inHeaders != null) {
        base.setHeaders(Util.buildHeaders(req.inHeaders));
      }
      return base;
    }

    /**
     * 构建一个自定义的超时参数
     */
    private RequestConfig buildTimeOutConfig(int timeoutMillisecond) {
      Builder builder = RequestConfig.custom();
      if (timeoutMillisecond <= 0) {
        timeoutMillisecond = config.getSocketTimeout();
      }
      builder.setConnectionRequestTimeout(config.getConnectRequestTimeout());
      builder.setConnectTimeout(timeoutMillisecond);
      builder.setSocketTimeout(timeoutMillisecond);
      RequestConfig requestConfig = builder.build();
      return requestConfig;
    }

    private static HttpRequestBase doBuildHttpRequestBase(XHttpRequest req) throws Exception {
      String method = req.method.toLowerCase();
      URI uri = URI.create(req.url);
      if (req.params != null && !req.params.isEmpty()) {
        List<NameValuePair> pairs = new LinkedList<>();
        req.params.forEach((k, v) -> {
          pairs.add(new BasicNameValuePair(k, v + ""));
        });
        uri = new URIBuilder(uri).addParameters(pairs).build();
      }
      if (method.equals("get")) {
        HttpGet get = new HttpGet(uri);
        return get;
      } else if (method.equals("post")) {
        HttpPost post = new HttpPost(uri);
        AbstractHttpEntity httpEntity = null;
        if (req.postBody != null) {
          httpEntity = new ByteArrayEntity(req.postBody);
        } else if (req.postForm != null) {
          httpEntity = new UrlEncodedFormEntity(Util.buildFormPostBody(req.postForm));
        } else if (req.raw != null) {
          httpEntity = new StringEntity(req.raw);
        }
        if (httpEntity != null) {
          if (req.contentType != null) {
            httpEntity.setContentType(req.contentType);
          }
          post.setEntity(httpEntity);
        }
        return post;
      } else if (method.equals("put")) {
        HttpPut put = new HttpPut(uri);
        AbstractHttpEntity httpEntity = null;
        if (req.raw != null) {
          httpEntity = new StringEntity(req.raw);
        }
        if (req.file != null) {
          File file = new File(req.file);
          httpEntity = new NFileEntity(file);
        }
        if (httpEntity != null) {
          if (req.contentType != null) {
            httpEntity.setContentType(req.contentType);
          }
          if (req.charset != null) {
            httpEntity.setContentEncoding(req.charset);
          }
          put.setEntity(httpEntity);
        }
        return put;
      } else if (method.equals("delete")) {
        HttpDelete delete = new HttpDelete(uri);
        return delete;
      } else {
        throw new HttpException("unsupport method");
      }
    }
  }
}
