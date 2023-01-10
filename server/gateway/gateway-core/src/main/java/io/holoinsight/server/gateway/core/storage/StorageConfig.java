/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.storage;


import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
@Component
@Getter
public class StorageConfig extends AbstractConfig {
  private volatile Basic basic = new Basic();

  /** {@inheritDoc} */
  @Override
  protected void refresh(Binder binder) {
    // 理论上可以做到全自动, 就是在storage 上加注解
    // 但spring的有一个缺点
    // fooBar 的命名风格它不支持, 它需要 foo-bar
    // 应该是有地方改 使得它支持的

    // TODO 与个问题, 如果 storage.ceresdb 根本没有出现过, 此时也算是 ifBound 成立吗?
    binder.bind("storage.basic", Basic.class).ifBound(x -> basic = x);
  }

  @Data
  public static class Basic {
    private int batchSize = 1024;
  }
}
