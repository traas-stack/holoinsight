/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alert_notification_template
-- ----------------------------
ALTER TABLE `alert_notification_template`
    ADD COLUMN `description` varchar(255) DEFAULT NULL COMMENT '模板描述信息';