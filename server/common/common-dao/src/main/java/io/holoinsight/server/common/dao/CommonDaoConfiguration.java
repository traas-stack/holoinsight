/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * created at 2023/1/30
 *
 * @author xzchaoo
 */
@Configuration
@MapperScan(basePackages = {"io.holoinsight.server.common.dao.mapper"})
public class CommonDaoConfiguration {
}
