/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alarm_history
-- ----------------------------
ALTER TABLE `alarm_history`
    ADD COLUMN `app` VARCHAR(1000) NULL DEFAULT NULL COMMENT 'app name list' AFTER `env_type`;
ALTER TABLE `alarm_history_detail`
    ADD COLUMN `app` VARCHAR(1000) NULL DEFAULT NULL COMMENT 'app name list' AFTER `env_type`;