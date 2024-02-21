/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import java.time.Duration;

import io.holoinsight.server.common.dao.CommonDaoConfiguration;
import io.holoinsight.server.common.dao.mapper.GaeaConfigDOMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.xzchaoo.commons.basic.config.Config;
import com.xzchaoo.commons.basic.config.DefaultManager;
import com.xzchaoo.commons.basic.config.MutableMapConfig;
import com.xzchaoo.commons.basic.config.spring.ConfigListenerContainer;
import com.xzchaoo.commons.basic.config.spring.UpdateConfigTask;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author xzchaoo
 */
@Configuration
@Import(CommonDaoConfiguration.class)
public class ConfigConfiguration {
  @Bean
  public ConfigService configService() {
    return new ConfigService();
  }

  @Bean
  public ConfigWebController configWebController() {
    return new ConfigWebController();
  }

  @Bean
  public ConfigListenerContainer configListenerContainer(Config config) {
    return new ConfigListenerContainer(config);
  }

  /**
   * internal bean
   *
   * @return
   */
  @Bean
  ConfigDao __configDao(GaeaConfigDOMapper mapper) {
    return new ConfigDao(mapper);
  }

  @Bean
  public Config config(ConfigDao configDao) {
    DefaultManager m = new DefaultManager();

    // MutableMapConfig defaultConfig = m.createMutableMapConfig();
    // 自己在这里手动set进去, 也许没必要, 因为使用 AbstractConfig 的子类的话基本上自带默认值了

    MutableMapConfig dbConfig = m.createMutableMapConfig();
    // 立即绑定一波 初始化失败是致命的
    dbConfig.setMap(configDao.getConfig());

    new UpdateConfigTask(dbConfig, configDao::getConfig, Duration.ofSeconds(10)).start();

    return dbConfig;
  }
}
