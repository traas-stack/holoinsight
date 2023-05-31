/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for meta_data
-- ----------------------------
CREATE TABLE IF NOT EXISTS `meta_dim_data` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
    `table_name` varchar(256) NOT NULL COMMENT 'Table name',
    `uk` varchar(256) NOT NULL COMMENT 'Meta data uk',
    `json` longtext COMMENT 'Json data, json format of metadata',
    `annotations` longtext COMMENT 'annotations message',
    `deleted` tinyint NOT NULL COMMENT 'Softed delete, when 1 indicates that the configuration has been deleted',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_table_name_uk` (`table_name`,`uk`)  USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Meta data';
