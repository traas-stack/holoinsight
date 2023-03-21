/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.initializer;

import io.holoinsight.server.apm.core.ModelCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author jiwliu
 * @version : ApmInitializer.java, v 0.1 2022年10月12日 19:31 xiangwanpeng Exp $
 */
@Slf4j
public class ApmInitializer implements ApplicationRunner {

  @Autowired
  private ModelCenter modelManager;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    try {
      modelManager.init();
      log.info("[apm] init finish");
    } catch (Exception e) {
      log.error("[apm] init fail, msg={}", e.getMessage(), e);
    }
  }
}
