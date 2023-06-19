/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.openai;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author masaimu
 * @version 2023-06-20 12:42:00
 */
@Slf4j
public class OpenAiService {

  private OpenAiClient openAiClient;

  private OpenAiService() {}

  private static class OpenAiServiceHolder {
    private static final OpenAiService INSTANCE = new OpenAiService();
  }

  public static OpenAiService getInstance() {
    return OpenAiServiceHolder.INSTANCE;
  }

  public OpenAiClient getClient() {
    if (openAiClient != null) {
      return openAiClient;
    }
    String token = MetaDictUtil.getStringValue("openai", "token");
    if (StringUtils.isBlank(token)) {
      return OpenAiClient.builder().build();
    }
    String apiHost = MetaDictUtil.getStringValue("openai", "apiHost");
    if (StringUtils.isBlank(apiHost)) {
      apiHost = "https://api.openai.com/";
    }
    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
        .addInterceptor(new OpenAiResponseInterceptor()).connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
    openAiClient = OpenAiClient.builder().apiKey(Collections.singletonList(token))
        .okHttpClient(okHttpClient).apiHost(apiHost).build();
    return openAiClient;
  }

  public String getSimilarKey(String original, List<String> candidate) {
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
    ChatCompletionResponse chatCompletionResponse =
        OpenAiService.getInstance().getClient().chatCompletion(chatCompletion);
    List<ChatChoice> list = chatCompletionResponse.getChoices();
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    ChatChoice chatChoice = list.get(0);
    log.info("find similar prompt: {}", prompt);
    log.info("find similar result {}", chatChoice.getMessage());
    return chatChoice.getMessage().getContent();
  }
}
