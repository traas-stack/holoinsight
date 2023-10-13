CREATE TABLE IF NOT EXISTS `agg_offset_v1` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `partition` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `consumer_group` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint NOT NULL,
  `data` blob,
  PRIMARY KEY (`id`),
  KEY `k_partition_version` (`partition`,`consumer_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `agg_task_v1` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `agg_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint NOT NULL,
  `json` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agg_id_version` (`agg_id`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `agg_state_v1` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `partition` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `consumer_group` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `state` longblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_partition` (`partition`, `consumer_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
