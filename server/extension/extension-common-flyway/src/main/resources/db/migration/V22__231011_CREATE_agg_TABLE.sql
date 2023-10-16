CREATE TABLE IF NOT EXISTS `agg_offset_v1` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    `partition_name` varchar(255) NOT NULL comment '分区',
    `consumer_group` varchar(255) NOT NULL comment '消费者组',
    `version` bigint(20) NOT NULL comment '版本号',
    `data` blob DEFAULT NULL comment '位点信息',
    PRIMARY KEY(`id`),
    KEY `k_partition_name_consumer_group`(`partition_name`, `consumer_group`)
    ) AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '消费位点表v1';

CREATE TABLE IF NOT EXISTS `agg_task_v1` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment 'pk',
    `gmt_create` timestamp NOT NULL comment 'create time',
    `gmt_modified` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP comment 'modify time',
    `agg_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL comment 'agg id',
    `version` bigint(20) NOT NULL comment 'agg version',
    `json` text COLLATE utf8mb4_unicode_ci NOT NULL comment 'agg content',
    `deleted` int(11) NOT NULL comment 'deletion mark',
    PRIMARY KEY(`id`),
    UNIQUE KEY `k_agg_id_version`(`agg_id`, `version`)
    ) AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'agg task table';

CREATE TABLE IF NOT EXISTS `agg_state_v1` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    `partition_name` varchar(255) NOT NULL comment '分区',
    `consumer_group` varchar(255) NOT NULL comment '消费者组',
    `state` longblob NOT NULL comment '状态',
    PRIMARY KEY(`id`),
    UNIQUE KEY `k_partition_name_consumer_group`(`partition_name`, `consumer_group`)
    ) AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '聚合状态存储表v1';
