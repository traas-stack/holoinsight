/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for agg_task_v1
-- ----------------------------
ALTER TABLE `agg_task_v1`
    ADD COLUMN `ref_id` VARCHAR(200) NULL DEFAULT NULL COMMENT 'ref id' AFTER `json`;
