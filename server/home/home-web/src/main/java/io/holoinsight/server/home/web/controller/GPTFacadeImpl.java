/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.FunctionCall;
import com.unfbx.chatgpt.entity.chat.Message;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.gpt.GptService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.openai.FunctionRegistry;
import io.holoinsight.server.home.biz.service.openai.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MethodInvoker;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author masaimu
 * @version 2023-06-15 20:37:00
 */
@Slf4j
@RestController
@RequestMapping("/webapi/gpt")
public class GPTFacadeImpl extends BaseFacade {

  @Autowired
  private FunctionRegistry functionRegistry;

  @Autowired
  private OpenAiService openAiService;

  @Autowired
  private GptService gptService;

  @PostMapping("/console")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<String> save(@RequestBody Map<String, Object> request)
      throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException,
      InvocationTargetException, IllegalAccessException {
    String requestId = UUID.randomUUID().toString();
    String content = (String) request.get("content");
    Message message = Message.builder().role(Message.Role.USER).content(content).build();

    ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message))
        .functions(this.functionRegistry.getFunctions()).functionCall("auto")
        .model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName()).build();
    log.info("{} First chatCompletion {}", requestId, J.toJson(chatCompletion));
    ChatCompletionResponse chatCompletionResponse =
        this.openAiService.getClient().chatCompletion(chatCompletion);

    ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);
    Message msg = chatChoice.getMessage();
    FunctionCall functionCallResult = msg.getFunctionCall();
    if (functionCallResult == null) {
      return JsonResult.createSuccessResult(msg.getContent());
    }
    log.info("{} FunctionCall {}", requestId, functionCallResult);
    String functionName = functionCallResult.getName();
    String arguments = functionCallResult.getArguments();

    log.info("{} functionName {} arguments {}", requestId, functionName,
        StringUtils.isBlank(arguments) ? arguments : arguments.replace("\n", ""));

    Map<String, Object> paramMap =
        J.toMap(chatChoice.getMessage().getFunctionCall().getArguments());
    paramMap.put("requestId", requestId);

    MethodInvoker invoker = new MethodInvoker();
    invoker.setTargetObject(this.functionRegistry);
    invoker.setTargetMethod(functionName);
    invoker.setArguments(paramMap);
    invoker.prepare();
    String result = (String) invoker.invoke();

    log.info("{} invoke result {}", requestId, result);

    if (StringUtils.isEmpty(result)) {
      result = "The execution of function calling failed because the execution result is empty.";
    }

    FunctionCall functionCall = FunctionCall.builder() //
        .arguments(arguments) //
        .name(functionName) //
        .build();
    Message message2 = Message.builder().role(Message.Role.ASSISTANT).content("Method arguments")
        .functionCall(functionCall).build();
    Message message3 =
        Message.builder().role(Message.Role.FUNCTION).name(functionName).content(result).build();
    List<Message> messageList = Arrays.asList(message, message2, message3);

    ChatCompletion chatCompletionV2 = ChatCompletion.builder().messages(messageList)
        .model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName()).build();

    ChatCompletionResponse response =
        this.openAiService.getClient().chatCompletion(chatCompletionV2);
    log.info("{} response {}", requestId, response.getChoices().get(0).getMessage().getContent());
    return JsonResult.createSuccessResult(response.getChoices().get(0).getMessage().getContent());

  }

  @PostMapping("/task/submit")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Map<String, Object>> submit(@RequestBody Map<String, Object> request) {

    final JsonResult<Map<String, Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(request, "request");
      }

      @Override
      public void doManage() {
        String str = gptService.submit(request, UUID.randomUUID().toString());
        Map<String, Object> ren = J.toMap(str);
        if (ren != null) {
          ren.remove("ip");
        }
        JsonResult.createSuccessResult(result, ren);
      }
    });

    return result;
  }

  @PostMapping("/qa/save")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<String> qaSave(@RequestBody List<Map<String, Object>> request) {
    final JsonResult<String> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(request, "request");
      }

      @Override
      public void doManage() {
        String str = gptService.save(request, UUID.randomUUID().toString());
        JsonResult.createSuccessResult(result, str);
      }
    });

    return result;
  }

}
