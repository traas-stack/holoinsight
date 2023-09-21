/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

ALTER TABLE `integration_plugin` ADD COLUMN `config` MEDIUMTEXT NULL DEFAULT NULL COMMENT 'config' AFTER `json`;
