/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alarm_rule
-- ----------------------------
ALTER TABLE `alarm_rule`
    ADD COLUMN `alert_template_uuid` varchar(100) NULL DEFAULT NULL comment '告警模板UUID';