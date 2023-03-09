SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for agent_configuration
-- ----------------------------
CREATE TABLE IF NOT EXISTS `agent_configuration` (
  `tenant` varchar(255) NOT NULL,
  `service` varchar(100) NOT NULL,
  `app_id` varchar(100) NOT NULL,
  `env_id` varchar(100) NOT NULL,
  `value` longtext NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`tenant`,`service`,`app_id`,`env_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for alarm_block
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_block` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL,
  `modifier` varchar(64) DEFAULT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `tags` longtext COMMENT 'Masked dimension',
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id',
  `reason` varchar(256) DEFAULT NULL,
  `extra` longtext ,
  `tenant` varchar(255) DEFAULT NULL ,
  `hour` tinyint DEFAULT '0' COMMENT 'Shield hour',
  `minute` tinyint DEFAULT '0' COMMENT 'Shield minute',
  `source_type` varchar(64) DEFAULT NULL,
  `source_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm masking';

-- ----------------------------
-- Table structure for alarm_ding_ding_robot
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_ding_ding_robot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL,
  `modifier` varchar(64) DEFAULT NULL,
  `group_name` varchar(64) NOT NULL,
  `robot_url` varchar(128) NOT NULL,
  `extra` longtext,
  `tenant` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm dingtalk robot infomation';

-- ----------------------------
-- Table structure for alarm_group
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL,
  `modifier` varchar(64) DEFAULT NULL,
  `group_name` varchar(256) NOT NULL,
  `group_info` mediumtext,
  `tenant` varchar(255) DEFAULT NULL,
  `sms_phone` mediumtext COMMENT 'Alarm SMS number',
  `dyvms_phone` mediumtext COMMENT 'Alarm phone number',
  `dd_webhook` mediumtext COMMENT 'Dingtalk robot',
  `email_address` mediumtext,
  `env_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm group';

-- ----------------------------
-- Table structure for alarm_history
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `alarm_time` timestamp NULL DEFAULT NULL,
  `recover_time` timestamp NULL DEFAULT NULL,
  `duration` bigint DEFAULT NULL,
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id',
  `rule_name` varchar(256) NOT NULL,
  `alarm_level` varchar(32) NOT NULL,
  `trigger_content` varchar(256) DEFAULT NULL,
  `extra` longtext,
  `tenant` varchar(255) DEFAULT NULL,
  `source_type` varchar(64) DEFAULT NULL,
  `source_id` bigint DEFAULT NULL,
  `env_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm history';

-- ----------------------------
-- Table structure for alarm_history_detail
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_history_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `alarm_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id',
  `history_id` bigint NOT NULL DEFAULT '-1' COMMENT 'Alarm history id',
  `tags` longtext COMMENT 'Alarm dimension',
  `alarm_content` mediumtext COMMENT 'Introduction of trigger mode',
  `datasource` mediumtext,
  `extra` longtext,
  `tenant` varchar(255) NOT NULL,
  `source_type` varchar(64) DEFAULT NULL,
  `source_id` bigint DEFAULT NULL,
  `env_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_history_id` (`history_id`),
  KEY `idx_unique_id` (`unique_id`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm history details';

-- ----------------------------
-- Table structure for alarm_rule
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `rule_name` varchar(256) NOT NULL,
  `rule_type` varchar(32) NOT NULL COMMENT 'AI or RULE',
  `creator` varchar(64) DEFAULT NULL,
  `modifier` varchar(64) DEFAULT NULL,
  `alarm_level` varchar(32) NOT NULL,
  `rule_describe` varchar(256) DEFAULT NULL,
  `rule` mediumtext NOT NULL,
  `pql` varchar(512) DEFAULT NULL,
  `time_filter` mediumtext NOT NULL COMMENT 'Effective time',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the rule is effective',
  `is_merge` tinyint NOT NULL DEFAULT '1',
  `merge_type` varchar(64) DEFAULT NULL,
  `recover` tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the recovery notification is enabled',
  `notice_type` varchar(32) DEFAULT NULL,
  `extra` longtext,
  `tenant` varchar(255) DEFAULT NULL,
  `alarm_content` varchar(255) DEFAULT NULL,
  `source_type` varchar(64) DEFAULT NULL,
  `source_id` bigint DEFAULT NULL,
  `env_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm rule';

-- ----------------------------
-- Table structure for alarm_subscribe
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_subscribe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL,
  `modifier` varchar(64) DEFAULT NULL,
  `subscriber` varchar(128) DEFAULT NULL,
  `group_id` bigint NOT NULL DEFAULT '-1',
  `unique_id` varchar(64) NOT NULL DEFAULT '-1',
  `notice_type` varchar(256) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `tenant` varchar(255) DEFAULT NULL,
  `source_type` varchar(64) DEFAULT NULL,
  `source_id` bigint DEFAULT NULL,
  `env_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm subscription';

-- ----------------------------
-- Table structure for alarm_webhook
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_webhook` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL,
  `modifier` varchar(64) DEFAULT NULL,
  `webhook_name` varchar(256) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Callback state',
  `request_type` varchar(16) NOT NULL,
  `request_url` varchar(256) NOT NULL,
  `request_headers` mediumtext,
  `request_body` mediumtext,
  `type` tinyint NOT NULL DEFAULT '2' COMMENT 'Callback type',
  `extra` longtext,
  `role` varchar(200) DEFAULT NULL,
  `tenant` varchar(255) NOT NULL,
  `webhook_test` mediumtext COMMENT 'Debugging data',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm callback information';

-- ----------------------------
-- Table structure for alertmanager_webhook
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alertmanager_webhook` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `creator` varchar(45) DEFAULT NULL,
  `modifier` varchar(45) DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for apikey
-- ----------------------------
CREATE TABLE IF NOT EXISTS `apikey` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `name` varchar(100) NOT NULL,
  `apikey` varchar(255) NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `modifier` varchar(200) DEFAULT NULL,
  `role` varchar(100) DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `access_config` mediumtext,
  `desc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `apikey_uk_apikey` (`apikey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for cluster
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cluster` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `ip` varchar(45) NOT NULL,
  `hostname` varchar(200) DEFAULT NULL,
  `role` varchar(45) NOT NULL,
  `last_heartbeat_time` bigint NOT NULL,
  `manual_close` tinyint DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for cluster_task
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cluster_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `task_id` varchar(100) NOT NULL,
  `period` bigint NOT NULL,
  `status` varchar(45) NOT NULL,
  `cluster_ip` varchar(45) NOT NULL,
  `context` longtext,
  `result` varchar(50) DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for custom_plugin
-- ----------------------------
CREATE TABLE IF NOT EXISTS `custom_plugin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL,
  `parent_folder_id` bigint NOT NULL DEFAULT '-1',
  `name` varchar(100) NOT NULL,
  `plugin_type` varchar(45) NOT NULL,
  `status` varchar(30) NOT NULL,
  `period_type` varchar(45) NOT NULL,
  `conf` longtext NOT NULL,
  `sample_log` longtext,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) NOT NULL,
  `modifier` varchar(45) NOT NULL,
  PRIMARY KEY (`id`,`period_type`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for dashboard
-- ----------------------------
CREATE TABLE IF NOT EXISTS `dashboard` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `title` varchar(255) NOT NULL,
  `conf` text NOT NULL,
  `creator` varchar(45) DEFAULT NULL,
  `modifier` varchar(45) DEFAULT NULL,
  `tenant` varchar(255) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for display_menu
-- ----------------------------
CREATE TABLE IF NOT EXISTS `display_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `ref_id` bigint NOT NULL,
  `type` varchar(100) NOT NULL,
  `config` mediumtext NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `creator` varchar(100) NOT NULL,
  `modifier` varchar(100) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for display_template
-- ----------------------------
CREATE TABLE IF NOT EXISTS `display_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(100) NOT NULL,
  `ref_id` bigint NOT NULL,
  `type` varchar(100) NOT NULL,
  `config` mediumtext NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `creator` varchar(100) NOT NULL,
  `modifier` varchar(100) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for folder
-- ----------------------------
CREATE TABLE IF NOT EXISTS `folder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(100) NOT NULL,
  `parent_folder_id` bigint DEFAULT NULL,
  `tenant` varchar(255) NOT NULL,
  `creator` varchar(100) DEFAULT NULL,
  `modifier` varchar(100) DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `ext_info` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for gaea_agent
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gaea_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
  `agent_id` varchar(255) NOT NULL COMMENT 'Agent id',
  `json` text NOT NULL COMMENT 'A json contains agent content',
  `status` int NOT NULL COMMENT 'Agent status. 0 means normal, 1 means deleted.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `gaea_agent_uk_agent_id` (`agent_id`),
  KEY `gaea_agent_k_gmt_modified` (`gmt_modified`),
  KEY `gaea_agent_k_tenant` (`tenant`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for gaea_cluster_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gaea_cluster_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL COMMENT 'Registry cluster tenant',
  `group` varchar(16) NOT NULL COMMENT 'Registry cluster group',
  `json` varchar(4096) NOT NULL COMMENT 'Registry cluster group config',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for gaea_collect_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gaea_collect_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
  `table_name` varchar(255) NOT NULL COMMENT 'Name of the collect config. It will be used by key or metric name generation.',
  `json` longtext NOT NULL COMMENT 'A json to describe collect config',
  `deleted` int NOT NULL COMMENT 'Deleted flag. Set to 1 to mark as deleted',
  `version` bigint NOT NULL COMMENT 'When update a config, we need to mark-delete old db record and create a new db record with new version in same transaction. These two record must have same gmt_modified.',
  `collect_range` text NOT NULL COMMENT 'A json to describe the collect range',
  `executor_selector` text NOT NULL COMMENT 'A json to describe which agent to run this collect config',
  `ref_id` varchar(100) DEFAULT NULL COMMENT 'An internal id for Integration',
  `biz_tenant` varchar(255) DEFAULT NULL COMMENT 'Deprecated field',
  `type` varchar(255) DEFAULT NULL COMMENT 'Type of collect. Agent use this field to resolve json to specified type',
  PRIMARY KEY (`id`),
  UNIQUE KEY `gaea_collect_config_uk_table_name__version` (`table_name`,`version`) USING BTREE,
  KEY `gaea_collect_config_k_table_name` (`table_name`) USING BTREE,
  KEY `gaea_collect_config_k_gmt_modified` (`gmt_modified`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Collect config';

-- ----------------------------
-- Table structure for gaea_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gaea_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant for isolating data. Has no relation to tenants on the product tier.',
  `conf_key` varchar(255) NOT NULL COMMENT 'Config key. Use format foo.bar.enabled',
  `conf_value` text NOT NULL COMMENT 'Config value in string style.',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_conf_key` (`tenant`,`conf_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for gaea_lock
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gaea_lock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL COMMENT 'Lock tenant. Its meaning depends on the implementation of the application layer.',
  `name` varchar(255) NOT NULL COMMENT 'Lock name. Its meaning depends on the implementation of the application layer.',
  `version` int NOT NULL COMMENT 'Version for optimistic lock',
  `json` varchar(1024) NOT NULL COMMENT 'Master content. Its meaning depends on the implementation of the application layer.',
  `status` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `gaea_lock_uk_tenant_name` (`tenant`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for gaea_master
-- ----------------------------
CREATE TABLE IF NOT EXISTS `gaea_master` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL COMMENT 'Master tenant. Its meaning depends on the implementation of the application layer.',
  `name` varchar(255) NOT NULL COMMENT 'Master name. Its meaning depends on the implementation of the application layer.',
  `version` int NOT NULL COMMENT 'Version for atomic update',
  `json` varchar(1024) NOT NULL COMMENT 'Master content. Its meaning depends on the implementation of the application layer.',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `gaea_master_uk_tenant_name` (`tenant`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for integration_generated
-- ----------------------------
CREATE TABLE IF NOT EXISTS `integration_generated` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL,
  `product` varchar(100) NOT NULL,
  `item` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `config` mediumtext,
  `deleted` tinyint NOT NULL,
  `custom` tinyint NOT NULL,
  `creator` varchar(100) NOT NULL,
  `modifier` varchar(100) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `idx_tenant_integration_name` (`product`,`name`,`tenant`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for integration_plugin
-- ----------------------------
CREATE TABLE IF NOT EXISTS `integration_plugin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `product` varchar(100) NOT NULL,
  `type` varchar(200) NOT NULL,
  `status` tinyint NOT NULL,
  `json` longtext NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) NOT NULL,
  `modifier` varchar(45) NOT NULL,
  `collect_range` longtext,
  `template` longtext,
  `version` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_name` (`tenant`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for integration_product
-- ----------------------------
CREATE TABLE IF NOT EXISTS `integration_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(100) NOT NULL,
  `profile` longtext,
  `overview` longtext,
  `configuration` longtext,
  `metrics` longtext,
  `status` tinyint DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `form` longtext,
  `template` longtext,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) DEFAULT NULL,
  `modifier` varchar(45) DEFAULT NULL,
  `version` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for marketplace_plugin
-- ----------------------------
CREATE TABLE IF NOT EXISTS `marketplace_plugin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `product` varchar(100) NOT NULL,
  `status` tinyint DEFAULT NULL,
  `type` varchar(150) DEFAULT NULL,
  `data_range` longtext,
  `json` longtext,
  `creator` varchar(100) NOT NULL,
  `modifier` varchar(100) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for marketplace_product
-- ----------------------------
CREATE TABLE IF NOT EXISTS `marketplace_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(100) NOT NULL,
  `profile` longtext,
  `overview` longtext,
  `configuration` longtext,
  `price` longtext,
  `feature` longtext,
  `status` tinyint DEFAULT NULL,
  `type` varchar(150) DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(100) NOT NULL,
  `modifier` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for meta_data
-- ----------------------------
CREATE TABLE IF NOT EXISTS `meta_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `_uk` varchar(100) NOT NULL,
  `_table` varchar(80) NOT NULL,
  `_type` varchar(45) NOT NULL,
  `_workspace` varchar(100) DEFAULT NULL,
  `_status` varchar(45) DEFAULT NULL,
  `_basic` mediumtext,
  `_labels` longtext,
  `_annotations` longtext,
  `_modified` timestamp NULL DEFAULT NULL,
  `_modifier` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `_uk_UNIQUE` (`_uk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
CREATE TABLE IF NOT EXISTS `meta_table` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `table_schema` longtext,
  `status` varchar(40) DEFAULT NULL,
  `config` longtext,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) NOT NULL,
  `modifier` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`),
  KEY `index_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for metadata_dictvalue
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_dictvalue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `type` varchar(45) NOT NULL,
  `dict_key` varchar(45) NOT NULL,
  `dict_value` mediumtext NOT NULL,
  `dict_value_type` varchar(45) DEFAULT NULL,
  `dict_desc` varchar(45) DEFAULT NULL,
  `version` int NOT NULL,
  `creator` varchar(45) NOT NULL,
  `modifier` varchar(45) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for metric_info
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metric_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `metric_type` varchar(255) DEFAULT NULL,
  `metrics` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ref_id` varchar(255) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `tenant` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for openmetrics_scraper
-- ----------------------------
CREATE TABLE IF NOT EXISTS `openmetrics_scraper` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `conf` text,
  `creator` varchar(45) DEFAULT NULL,
  `modifier` varchar(45) DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for position_biz_rule
-- ----------------------------
CREATE TABLE IF NOT EXISTS `position_biz_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `app_id` varchar(16) NOT NULL,
  `env_id` varchar(16) NOT NULL,
  `app_name` varchar(64) NOT NULL,
  `interface_type` varchar(16) NOT NULL,
  `interface_name` varchar(256) NOT NULL,
  `response_type` varchar(64) NOT NULL COMMENT 'ModelMap indicates acquisition in modelmap, and Return indicates acquisition of return value',
  `response_property` varchar(64) DEFAULT NULL COMMENT 'The attribute taken from the modelmap, this field is only required when ModelMap is selected',
  `error_code_config` varchar(8192) NOT NULL,
  `global_open` varchar(1) NOT NULL COMMENT 'Whether the rule takes effect globally: T indicates that the rule takes effect globally, and F indicates that the rule does not take effect globally',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_teai` (`app_id`,`env_id`,`app_name`,`interface_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Problem Location Business Rules Table';

-- ----------------------------
-- Table structure for tenant
-- --------------------LL AUTO_INCREMENT --------
CREATE TABLE IF NOT EXISTS `tenant` (
  `id` bigint NOT NUCOMMENT 'Data id',
  `name` varchar(255) NOT NULL,
  `code` varchar(45) NOT NULL,
  `desc` varchar(500) DEFAULT NULL,
  `json` text COMMENT 'Tenant information',
  `md5` varchar(45) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `product` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for tenant_ops
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tenant_ops` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `tenant` varchar(255) NOT NULL,
  `storage` varchar(4096) NOT NULL COMMENT 'storage configuration',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant` (`tenant`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `user_login_name` varchar(45) NOT NULL,
  `relate_id` varchar(300) NOT NULL,
  `type` varchar(50) NOT NULL,
  `name` varchar(200) NOT NULL,
  `url` varchar(1000) NOT NULL,
  `tenant` varchar(255) NOT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uk_login_tenant_relateId_type` (`user_login_name`,`relate_id`,`type`,`tenant`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for user_oplog
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user_oplog` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `table_name` varchar(200) DEFAULT NULL,
  `table_entity_id` varchar(200) DEFAULT NULL,
  `op_type` varchar(45) DEFAULT NULL,
  `op_before_context` longtext,
  `op_after_context` longtext,
  `name` varchar(200) DEFAULT NULL,
  `relate` varchar(1000) DEFAULT NULL,
  `tenant` varchar(255) DEFAULT NULL,
  `creator` varchar(45) DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ----------------------------
-- Table structure for workspace
-- ----------------------------
DROP TABLE IF EXISTS `workspace`;
CREATE TABLE `workspace` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `desc` varchar(100) DEFAULT NULL,
  `config` mediumtext DEFAULT NULL,
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY(`id`),
  UNIQUE KEY `id_UNIQUE`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
