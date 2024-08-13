/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for agg_task_v1
-- ----------------------------
ALTER TABLE `monitor_instance`
    ADD COLUMN `instance_name` varchar(255) NULL DEFAULT NULL comment '实例名称' AFTER `instance`;
