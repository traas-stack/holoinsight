/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

ALTER TABLE `alarm_block` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `alarm_history` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `alarm_history_detail` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `alarm_subscribe` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `custom_plugin` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `dashboard` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `folder` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `integration_generated` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `openmetrics_scraper` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `user_favorite` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `user_oplog` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `gaea_collect_config` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;