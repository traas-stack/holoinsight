/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for meta_data
-- ----------------------------
ALTER TABLE `integration_plugin` CHANGE COLUMN `workspace` `workspace` VARCHAR(100)  NULL DEFAULT NULL;
