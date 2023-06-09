/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
CREATE TABLE `userinfo` (
      `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
      `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
      `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'modified time',
      `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
      `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
      `tenant` VARCHAR(100) DEFAULT NULL COMMENT 'tenant',
      `workspace` VARCHAR(200) DEFAULT NULL COMMENT 'workspace',
      `nickname` VARCHAR(255) NOT NULL COMMENT 'nickname',
      `phone_number` VARCHAR(100) DEFAULT NULL COMMENT 'phone Number',
      `phone_number_alias` VARCHAR(100) DEFAULT NULL COMMENT 'phone Number Alias',
      `email` VARCHAR(100) DEFAULT NULL COMMENT 'email',
      `email_alias` VARCHAR(100) DEFAULT NULL COMMENT 'email Alias',
      `deleted` tinyint NOT NULL COMMENT 'Softed delete, when 1 indicates that the configuration has been deleted',
      `status` VARCHAR(100) DEFAULT NULL COMMENT 'status',
      PRIMARY KEY (`id`),
      UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
      INDEX `idx_tenant_nickname` (`tenant` ASC, `nickname` ASC, `deleted` ASC) VISIBLE);

-- ----------------------------
-- Table structure for userinfo_verification
-- ----------------------------
CREATE TABLE `userinfo_verification` (
                            `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
                            `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                            `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'modified time',
                            `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
                            `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
                            `tenant` VARCHAR(100) DEFAULT NULL COMMENT 'tenant',
                            `workspace` VARCHAR(200) DEFAULT NULL COMMENT 'workspace',
                            `code` VARCHAR(100) NOT NULL COMMENT 'code',
                            `verification_content` VARCHAR(100) DEFAULT NULL COMMENT 'verification content',
                            `content_type` VARCHAR(100) DEFAULT NULL COMMENT 'content type',
                            `expire_timestamp` BIGINT(20) NOT NULL COMMENT 'expire timestamp',
                            `status` VARCHAR(100) DEFAULT NULL COMMENT 'status',
                            PRIMARY KEY (`id`),
                            UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);
