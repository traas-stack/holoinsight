/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for metric_info
-- ----------------------------
ALTER TABLE `metric_info`
    ADD COLUMN `storage_tenant` VARCHAR(200) NULL DEFAULT NULL COMMENT 'storage tenant' AFTER `ext_info`;
