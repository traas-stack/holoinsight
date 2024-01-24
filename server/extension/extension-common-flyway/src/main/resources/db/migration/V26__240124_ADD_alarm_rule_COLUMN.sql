/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alarm_rule
-- ----------------------------
ALTER TABLE `alarm_rule`
    ADD COLUMN `alert_notification_template_id` BIGINT(20) NULL DEFAULT NULL comment '告警模板ID' AFTER `env_type`;