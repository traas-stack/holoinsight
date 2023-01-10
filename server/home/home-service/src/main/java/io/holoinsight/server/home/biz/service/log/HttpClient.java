/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author zanghaibo
 * @time 2022-08-07 10:18 上午
 */

@Slf4j
public class HttpClient {

    private static final RequestConfig requestConfig;

    static {
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(1000 * 60)
                .setConnectionRequestTimeout(1000 * 6)
                .setSocketTimeout(1000 * 60 * 3)
                .build();
    }

    public static String get(String url, Map<String, String> headers) throws HttpException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();

            HttpGet httpGet = new HttpGet(uri);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpGet.addHeader(key, headers.get(key));
                }
            }
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }else{
                throw new HttpException("get fail with code:" + response.getStatusLine().getStatusCode());
            }

        } catch (IOException | URISyntaxException | HttpException e) {
            throw new HttpException("get fail url:" + response.getStatusLine().getStatusCode());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error("Http close fail",e);
            }
        }
        return resultString;
    }

    public static String post(String url, Map<String, String> param, Map<String, String> headers) throws HttpException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.addHeader(key, headers.get(key));
                }
            }
            httpPost.setConfig(requestConfig);
            if (param != null) {
                StringEntity entity = new StringEntity(JSON.toJSONString(param),
                        ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), "utf-8"));
                httpPost.setEntity(entity);
            }
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }else{
                throw new HttpException("post fail with code：" + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            throw new HttpException("post fail url:" + url);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("Http close fail",e);
            }
        }
        return resultString;
    }
}
