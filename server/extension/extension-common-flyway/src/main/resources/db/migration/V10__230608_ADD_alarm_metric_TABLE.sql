/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

CREATE TABLE `alarm_metric` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
    `rule_id`      BIGINT(20) NOT NULL,
    `rule_type`    VARCHAR(100) NOT NULL,
    `metric_table` VARCHAR(200) NOT NULL,
    `tenant`       VARCHAR(100) NOT NULL,
    `workspace`    VARCHAR(200) NULL DEFAULT NULL,
    `deleted`      TINYINT(2) NOT NULL,
    `config`       MEDIUMTEXT NULL DEFAULT NULL,
    `gmt_create`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
    `gmt_modified` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
    INDEX `idx_tenant_metric` (`tenant` ASC, `metric_table` ASC, `deleted` ASC) VISIBLE);