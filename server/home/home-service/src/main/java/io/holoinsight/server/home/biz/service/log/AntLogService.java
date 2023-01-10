/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.holoinsight.server.home.common.util.MonitorException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zanghaibo
 * @time 2022-08-30 4:07 下午
 */
@Service
@Slf4j
public class AntLogService {

  @Autowired
  private LogClientHolder holder;

  // todo: 超时，容量需要调整一下
  private volatile LoadingCache<String, AntLogInfo> infoCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).expireAfterAccess(20, TimeUnit.MINUTES)
          .maximumSize(1024).build(new CacheLoader<String, AntLogInfo>() {
            @Override
            public AntLogInfo load(String key) throws URISyntaxException, HttpException {
              String[] ele = key.split(LogConstants.CACHE_SPLITTER);
              if (ele.length != 3) {
                throw new MonitorException("invalid key input" + key);
              }
              String region = ele[0];
              String app = ele[1];

              // 830默认固定region, 固定fullPath
              String fullPath = ele[2];
              ArrayList<AntLogInfo> infos = queryLogInfo(region, app);
              for (AntLogInfo info : infos) {
                if (fullPath.equals(info.getFullPath())) {
                  return info;
                }
              }
              throw new MonitorException("can not find info from antlog:" + key);
            }
          });

  /**
   * 从缓存读取日志元数据，Miss会原地同步
   * 
   * @param cacheKey
   * @return
   * @throws ExecutionException
   */
  public AntLogInfo queryLogInfo(String cacheKey) throws ExecutionException {
    return infoCache.get(cacheKey);
  }

  /**
   * 查询antlog日志基本信息
   * 
   * @param region
   * @param app
   * @return
   */
  public ArrayList<AntLogInfo> queryLogInfo(String region, String app)
      throws URISyntaxException, HttpException {
    StringBuilder sb = new StringBuilder();
    String url = sb.append(holder.getAntLogUrl()).append("/openapi/v1/dtm/regions/").append(region)
        .append("/apps/").append(app).append("/logs").toString();

    Map<String, String> headers = AntLogUtils.DefaultAntLogHeaderForCloudRun();
    headers.putAll(holder.getSigner().getHawkSignHeader("GET", new URI(url)));
    String responseStr = HttpClient.get(url, headers);
    log.info("queryLogInfo:" + responseStr);
    AntLogApi<ArrayList<AntLogInfo>> response =
        JSON.parseObject(responseStr, new TypeReference<AntLogApi<ArrayList<AntLogInfo>>>() {});
    if (response.getSuccess()) {
      return response.getData();
    }
    return null;
  }

  public AntLogInfo queryLogInfo(String region, String app, String logPath)
      throws URISyntaxException, HttpException {
    ArrayList<AntLogInfo> infos = queryLogInfo(region, app);
    for (AntLogInfo info : infos) {
      if (logPath.equals(info.getFullPath())) {
        return info;
      }
    }
    return null;
  }

  public AntLogInfo queryCustomLogInfo(String region, String app)
      throws URISyntaxException, HttpException {
    return queryLogInfo(region, app, LogConstants.MINI_PROGRAM_LOG_CUSTOM_PATH);
  }

  public AntLogInfo queryStdoutLogInfo(String region, String app)
      throws URISyntaxException, HttpException {
    return queryLogInfo(region, app, LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT);
  }

  public ArrayList<AntLogInfo> queryLogInfos(String region, String app)
      throws URISyntaxException, HttpException {
    return queryLogInfo(region, app);
  }

}
