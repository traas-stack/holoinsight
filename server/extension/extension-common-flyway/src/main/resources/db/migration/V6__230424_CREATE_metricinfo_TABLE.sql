/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

CREATE TABLE `metric_info` (
      `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
      `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
      `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'modified time',
      `tenant` VARCHAR(100) NOT NULL COMMENT 'tenant',
      `workspace` VARCHAR(200) NOT NULL COMMENT 'workspace',
      `organization` VARCHAR(100) NOT NULL COMMENT 'organization',
      `product` VARCHAR(100) NOT NULL COMMENT 'product',
      `metric_type` VARCHAR(100) NOT NULL COMMENT 'metric type',
      `metric` VARCHAR(100) NOT NULL COMMENT 'metric',
      `metric_table` VARCHAR(200) NOT NULL COMMENT 'metric_table',
      `description` VARCHAR(400) NULL COMMENT 'description',
      `unit` VARCHAR(100) NULL COMMENT 'unit',
      `period` INT NULL COMMENT 'period',
      `statistic` VARCHAR(100) NULL COMMENT 'statistic',
      `tags` VARCHAR(1000) NULL COMMENT 'tags',
      `deleted` TINYINT(2) NOT NULL COMMENT 'deleted',
      PRIMARY KEY (`id`),
      UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
      INDEX `idx_tenant_metric` (`tenant` ASC, `metric_table` ASC, `deleted` ASC) VISIBLE,
      INDEX `idx_tenant_product` (`tenant` ASC, `product` ASC, `deleted` ASC) VISIBLE);
