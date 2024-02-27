/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for monitor_instance
-- ----------------------------
ALTER TABLE `monitor_instance`
    ADD COLUMN `instance_type` varchar(255) NULL DEFAULT NULL comment '实例类型';
ALTER TABLE `monitor_instance`
    ADD COLUMN `instance_info` mediumtext NULL comment '实例配置详情';
