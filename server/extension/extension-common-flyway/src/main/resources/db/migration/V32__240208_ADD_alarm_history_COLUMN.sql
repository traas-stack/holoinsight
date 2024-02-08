/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alarm_history
-- ----------------------------
ALTER TABLE `alarm_history`
    ADD COLUMN `deleted` TINYINT(4) NULL COMMENT '软删除标记';