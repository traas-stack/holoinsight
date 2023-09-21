/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

ALTER TABLE `alarm_group` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;
ALTER TABLE `alarm_ding_ding_robot` ADD COLUMN `workspace` VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`;