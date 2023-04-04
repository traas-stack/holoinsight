/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author masaimu
 * @version 2023-04-03 17:12:00
 */
public class ParaCheckUtilTest {

  @Test
  public void testInvalidCheck() {
    Exception exception = null;
    try {
      ParaCheckUtil.checkInvalidCharacter("<a href=http://www.baidu.com>点击查看详情</a>",
          "invalid name");
    } catch (Exception e) {
      exception = e;
    }
    Assert.isTrue(exception != null, exception.getMessage());
  }

  @Test
  public void testValidCheck() {
    Exception exception = null;
    try {
      ParaCheckUtil.checkInvalidCharacter("ValidRule_1234-汉字", "invalid name");
    } catch (Exception e) {
      exception = e;
    }
    Assert.isTrue(exception == null, "exception should be null");
  }
}
