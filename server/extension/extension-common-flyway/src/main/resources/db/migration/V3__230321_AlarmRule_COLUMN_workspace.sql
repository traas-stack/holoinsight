/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

ALTER TABLE `alarm_rule`
    ADD COLUMN `workspace` varchar(100) NULL DEFAULT NULL COMMENT 'Rule workspace';