/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.openai;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.IntegrationPluginMapper;
import io.holoinsight.server.home.dal.model.IntegrationPlugin;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author masaimu
 * @version 2023-06-20 12:42:00
 */
@Slf4j
@Service
public class OpenAiService {

  private Map<String /* tenant */, OpenAiStreamClient> streamClientMap = new ConcurrentHashMap<>();
  private Map<String /* tenant */, OpenAiClient> clientMap = new ConcurrentHashMap<>();

  @Resource
  private IntegrationPluginMapper integrationPluginMapper;


  public OpenAiStreamClient getStreamClient() {
    MonitorScope ms = RequestContext.getContext().ms;
    if (ms == null) {
      throw new MonitorException("can not find MonitorScope.");
    }
    if (StringUtils.isBlank(ms.tenant)) {
      throw new MonitorException("can not find tenant.");
    }
    return getStreamClientByTenant(ms.tenant);
  }

  public OpenAiClient getClient() {
    MonitorScope ms = RequestContext.getContext().ms;
    if (ms == null) {
      throw new MonitorException("can not find MonitorScope.");
    }
    if (StringUtils.isBlank(ms.tenant)) {
      throw new MonitorException("can not find tenant.");
    }
    return getClientByTenant(ms.tenant);
  }

  private OpenAiClient getClientByTenant(String tenant) {
    OpenAiClient openAiClient = this.clientMap.get(tenant);
    if (openAiClient != null) {
      return openAiClient;
    }

    String token = getOpenaiApiKey(tenant);
    if (StringUtils.isBlank(token)) {
      throw new MonitorException("OPENAI_API_KEY can not be empty.");
    }
    String apiHost = getOpenAiHost(tenant);

    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    OkHttpClient okHttpClient = new OkHttpClient.Builder() //
        .addInterceptor(httpLoggingInterceptor) //
        .addInterceptor(new OpenAiResponseInterceptor()) //
        .connectTimeout(60, TimeUnit.SECONDS) //
        .writeTimeout(120, TimeUnit.SECONDS) //
        .readTimeout(120, TimeUnit.SECONDS) //
        .build();

    OpenAiClient client = OpenAiClient.builder().apiKey(Collections.singletonList(token))
        .apiHost(apiHost).okHttpClient(okHttpClient).build();
    this.clientMap.put(tenant, client);
    return client;
  }

  private String getOpenAiHost(String tenant) {
    QueryWrapper<IntegrationPlugin> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", "io.holoinsight.plugin.OpenAiPlugin");
    queryWrapper.eq("status", true);
    queryWrapper.eq("tenant", tenant);
    List<IntegrationPlugin> plugins = integrationPluginMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(plugins)) {
      throw new MonitorException("The OpenAiPlugin of " + tenant + " has not been opened yet.");
    }
    IntegrationPlugin plugin = plugins.get(0);
    String conf = plugin.json;
    if (StringUtils.isEmpty(conf)) {
      throw new MonitorException("The OpenAiPlugin conf of " + tenant + " is empty.");
    }
    Map<String, Object> confMap = J.toMap(conf);
    Map<String, Object> reqConf = (Map<String, Object>) confMap.get("conf");
    return (String) reqConf.get("OpenAiHost");
  }

  private String getOpenaiApiKey(String tenant) {
    QueryWrapper<IntegrationPlugin> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", "io.holoinsight.plugin.OpenAiPlugin");
    queryWrapper.eq("status", true);
    queryWrapper.eq("tenant", tenant);
    List<IntegrationPlugin> plugins = integrationPluginMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(plugins)) {
      throw new MonitorException("The OpenAiPlugin of " + tenant + " has not been opened yet.");
    }
    IntegrationPlugin plugin = plugins.get(0);
    String conf = plugin.json;
    if (StringUtils.isEmpty(conf)) {
      throw new MonitorException("The OpenAiPlugin conf of " + tenant + " is empty.");
    }
    Map<String, Object> confMap = J.toMap(conf);
    Map<String, Object> reqConf = (Map<String, Object>) confMap.get("conf");
    return (String) reqConf.get("OPENAI_API_KEY");
  }

  private OpenAiStreamClient getStreamClientByTenant(String tenant) {
    OpenAiStreamClient streamClient = this.streamClientMap.get(tenant);
    if (streamClient != null) {
      return streamClient;
    }

    String token = getOpenaiApiKey(tenant);
    if (StringUtils.isBlank(token)) {
      throw new MonitorException("OPENAI_API_KEY can not be empty.");
    }
    String apiHost = getOpenAiHost(tenant);

    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    OkHttpClient okHttpClient = new OkHttpClient.Builder() //
        .addInterceptor(httpLoggingInterceptor) //
        .connectTimeout(60, TimeUnit.SECONDS) //
        .writeTimeout(120, TimeUnit.SECONDS) //
        .readTimeout(120, TimeUnit.SECONDS) //
        .build();

    OpenAiStreamClient client =
        OpenAiStreamClient.builder().apiKey(Collections.singletonList(token)).apiHost(apiHost)
            .okHttpClient(okHttpClient).build();
    this.streamClientMap.put(tenant, client);
    return client;
  }

  public String getSimilarKey(String original, List<String> candidate, String requestId) {
    StringBuilder prompt = new StringBuilder();
    prompt.append(
        "Find the combination from the list below that is most similar to the given combination. If none of the combinations are similar, return '[]'.\n"
            + "Format:\n" + "\"\"\"\n" + "Given combination:[行业访问量@行业访问量告警]\n"
            + "Candidate list: [行业访问量],[雏形访问量],[雏形2访问量],[老残游记]\n" + "Result:[行业访问量]\n"
            + "Given combination:[电影@变形金刚]\n"
            + "Candidate list: [失败率],[spm thing],[刷卡收单总量],[变形金刚5]\n" + "Result:[变形金刚5]\n"
            + "\"\"\"\n") //
        .append("Given combination:")//
        .append(original) //
        .append("\n Candidate list:") //
        .append(String.join(",", candidate)) //
        .append("\n Result:");
    Message message = Message.builder().role(Message.Role.USER).content(prompt.toString()).build();
    ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message))
        .model(ChatCompletion.Model.GPT_3_5_TURBO.getName()).build();
    log.info("{} find similar chatCompletion: {}", requestId, J.toJson(chatCompletion));
    ChatCompletionResponse chatCompletionResponse = getClient().chatCompletion(chatCompletion);
    List<ChatChoice> list = chatCompletionResponse.getChoices();
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    ChatChoice chatChoice = list.get(0);
    log.info("{} find similar result {}", requestId, chatChoice.getMessage());
    return chatChoice.getMessage().getContent();
  }

  public void unload(String tenant) {
    if (StringUtils.isEmpty(tenant)) {
      return;
    }
    this.clientMap.remove(tenant);
    this.streamClientMap.remove(tenant);
  }
}
