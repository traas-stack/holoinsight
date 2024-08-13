/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event.element.impl;

import io.holoinsight.server.home.alert.common.AlarmConstant;
import io.holoinsight.server.home.alert.common.DocumentUtil;
import io.holoinsight.server.home.alert.model.event.ElementSpiEnum;
import io.holoinsight.server.home.alert.service.event.element.ElementSpiServiceFactory;
import io.holoinsight.server.home.alert.service.event.element.IElementHandler;
import org.apache.ibatis.ognl.Ognl;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * @description: if条件判断
 * @author: jianyu.wl
 * @date: 2021/2/23 10:26 上午
 * @version: 1.0
 */
@Component
public class IfElementHandler implements IElementHandler, InitializingBean {

  @Override
  public String handler(String oldElement, Map<String, String> requestMap) {
    StringBuffer newElement = new StringBuffer();
    Boolean conditionResult = false;
    if (!oldElement.contains(AlarmConstant.IF_START_ELEMENT)) {
      return oldElement;
    }
    String[] elements = oldElement.split(AlarmConstant.IF_END_ELEMENT);
    synchronized (IfElementHandler.class) {
      for (String element : elements) {
        try {
          if (StringUtils.isEmpty(element) || !element.contains(AlarmConstant.IF_START_ELEMENT)) {
            newElement.append(element);
            continue;
          }
          if (!element.startsWith(AlarmConstant.IF_START_ELEMENT)) {
            newElement
                .append(element.substring(0, element.indexOf(AlarmConstant.IF_START_ELEMENT)));
          }
          String tmpElement = element.substring(element.indexOf(AlarmConstant.IF_START_ELEMENT))
              + AlarmConstant.IF_END_ELEMENT;
          Document document = DocumentUtil.generateSaxReader()
              .read(new ByteArrayInputStream(tmpElement.getBytes("UTF-8")));
          // 将If 条件转成 root节点
          Element root = document.getRootElement();
          // ognl表达式判断
          String condition = root.attribute(AlarmConstant.TEST_PROPERTY).getValue();
          Object condObj = Ognl.parseExpression(condition);
          Object value = Ognl.getValue(condObj, requestMap);

          if (value instanceof Boolean) {
            conditionResult = (Boolean) value;
          } else {
            throw new RuntimeException();
          }
          if (conditionResult) {
            String content = root.getText();
            newElement.append(content);
          }
        } catch (Exception e) {
          // This catch statement is intentionally empty
        }
      }
    }
    return newElement.toString();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    ElementSpiServiceFactory.register(ElementSpiEnum.IF_ELEMENT.getName(), this);
  }
}
