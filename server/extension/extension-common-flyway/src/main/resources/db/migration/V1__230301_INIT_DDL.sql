SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alarm_block
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_block` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL COMMENT 'Creator user',
  `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier user',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'Start time',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'End time',
  `tags` longtext COMMENT 'Masked dimension',
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id',
  `reason` varchar(256) DEFAULT NULL COMMENT 'Reason of the alarm is blocked',
  `extra` longtext COMMENT 'Extra message',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Tenant',
  `hour` tinyint DEFAULT '0' COMMENT 'Blocked duration(hour)',
  `minute` tinyint DEFAULT '0' COMMENT 'Blocked duration(minute)',
  `source_type` varchar(64) DEFAULT NULL COMMENT 'Source type of alarm block',
  `source_id` bigint DEFAULT NULL COMMENT 'Source id of alarm block',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm masking';

-- ----------------------------
-- Table structure for agent_configuration
-- ----------------------------
CREATE TABLE IF NOT EXISTS `agent_configuration` (
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
  `service` varchar(100) NOT NULL COMMENT 'Name of the service that trace agent collects',
  `app_id` varchar(100) NOT NULL COMMENT 'Cloudrun app Id',
  `env_id` varchar(100) NOT NULL COMMENT 'Cloudrun env Id',
  `value` longtext NOT NULL COMMENT 'To send the configuration to the trace agent',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`tenant`,`service`,`app_id`,`env_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for alarm_ding_ding_robot
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_ding_ding_robot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
  `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
  `group_name` varchar(64) NOT NULL COMMENT 'Specified name of dingtalk robot',
  `robot_url` varchar(128) NOT NULL COMMENT 'Webhook url of dingtalk robot',
  `extra` longtext COMMENT 'Extra message',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Tenant',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm dingtalk robot infomation';

-- ----------------------------
-- Table structure for alarm_group
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
  `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
  `group_name` varchar(256) NOT NULL COMMENT 'Specified name of alarm group',
  `group_info` mediumtext  COMMENT 'Information of alarm group',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Tenant',
  `sms_phone` mediumtext COMMENT 'Alarm SMS number',
  `dyvms_phone` mediumtext COMMENT 'Alarm phone number',
  `dd_webhook` mediumtext COMMENT 'Dingtalk robot',
  `email_address` mediumtext,
  `env_type` varchar(100) DEFAULT NULL COMMENT 'Environment type',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm group';

-- ----------------------------
-- Table structure for alarm_history
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `alarm_time` timestamp NULL DEFAULT NULL COMMENT 'The time of data detection',
  `recover_time` timestamp NULL DEFAULT NULL COMMENT 'The time of data detection passes',
  `duration` bigint DEFAULT NULL COMMENT 'Alert duration(ms)',
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm rule type and alarm rule id',
  `rule_name` varchar(256) NOT NULL COMMENT 'Alarm rule name',
  `alarm_level` varchar(32) NOT NULL COMMENT 'Alarm level',
  `trigger_content` varchar(256) DEFAULT NULL COMMENT 'The description of the alarm rule',
  `extra` longtext COMMENT 'Extra message',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Tenant',
  `source_type` varchar(64) DEFAULT NULL COMMENT 'Source type of alarm history',
  `source_id` bigint DEFAULT NULL COMMENT 'Source id of alarm history',
  `env_type` varchar(255) DEFAULT NULL COMMENT 'Environment type',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm history';

-- ----------------------------
-- Table structure for alarm_history_detail
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_history_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `alarm_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The time of data detection',
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id',
  `history_id` bigint NOT NULL DEFAULT '-1' COMMENT 'Alarm history id',
  `tags` longtext COMMENT 'Alarm dimension',
  `alarm_content` mediumtext COMMENT 'Introduction of trigger mode',
  `datasource` mediumtext COMMENT 'Datasource of the data detection',
  `extra` longtext COMMENT 'Extra message',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
  `source_type` varchar(64) DEFAULT NULL COMMENT 'Source type of alarm history detail',
  `source_id` bigint DEFAULT NULL COMMENT 'Source id of alarm history detail',
  `env_type` varchar(255) DEFAULT NULL COMMENT 'Environment type',
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
  `rule_name` varchar(256) NOT NULL COMMENT 'Alarm rule name',
  `rule_type` varchar(32) NOT NULL COMMENT 'AI or RULE',
  `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
  `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
  `alarm_level` varchar(32) NOT NULL COMMENT 'Alarm level',
  `rule_describe` varchar(256) DEFAULT NULL COMMENT 'Alarm rule description',
  `rule` mediumtext NOT NULL COMMENT 'Alarm rule configuration',
  `pql` varchar(512) DEFAULT NULL COMMENT 'Pql content',
  `time_filter` mediumtext NOT NULL COMMENT 'Effective time',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the rule is effective',
  `is_merge` tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the alarm is merged',
  `merge_type` varchar(64) DEFAULT NULL  COMMENT 'Merge type',
  `recover` tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the recovery notification is enabled',
  `notice_type` varchar(32) DEFAULT NULL COMMENT 'The way of notification',
  `extra` longtext COMMENT 'Extra message',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Tenant',
  `alarm_content` varchar(255) DEFAULT NULL COMMENT 'Alarm content',
  `source_type` varchar(64) DEFAULT NULL COMMENT 'Source type of alarm rule',
  `source_id` bigint DEFAULT NULL COMMENT 'Source id of alarm rule',
  `env_type` varchar(255) DEFAULT NULL COMMENT 'Environment type',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm rule';

-- ----------------------------
-- Table structure for alarm_subscribe
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_subscribe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
  `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
  `subscriber` varchar(128) DEFAULT NULL COMMENT 'User id of subscriber',
  `group_id` bigint NOT NULL DEFAULT '-1' COMMENT 'Id of alarm group',
  `unique_id` varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm rule type and alarm rule id',
  `notice_type` varchar(256) DEFAULT NULL COMMENT 'The way of notification',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Status of subscriptions',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Tenant',
  `source_type` varchar(64) DEFAULT NULL COMMENT 'Source type of alarm rule',
  `source_id` bigint DEFAULT NULL COMMENT 'Source id of alarm rule',
  `env_type` varchar(100) DEFAULT NULL COMMENT 'Environment type',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm subscription';

-- ----------------------------
-- Table structure for alarm_webhook
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alarm_webhook` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(64) DEFAULT NULL COMMENT 'Creator',
  `modifier` varchar(64) DEFAULT NULL COMMENT 'Modifier',
  `webhook_name` varchar(256) NOT NULL COMMENT 'Alarm webhook name',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'Callback state',
  `request_type` varchar(16) NOT NULL COMMENT 'Http method',
  `request_url` varchar(256) NOT NULL COMMENT 'Http(s) url',
  `request_headers` mediumtext COMMENT 'Http header',
  `request_body` mediumtext COMMENT 'Http body template json',
  `type` tinyint NOT NULL DEFAULT '2' COMMENT 'Callback type',
  `extra` longtext COMMENT 'Extra message',
  `role` varchar(200) DEFAULT NULL COMMENT 'Webhook role',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
  `webhook_test` mediumtext COMMENT 'Debugging data',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Alarm callback information';

-- ----------------------------
-- Table structure for alertmanager_webhook
-- ----------------------------
CREATE TABLE IF NOT EXISTS `alertmanager_webhook` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL COMMENT 'Alarm webhook name',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
  `creator` varchar(45) DEFAULT NULL COMMENT 'Creator',
  `modifier` varchar(45) DEFAULT NULL COMMENT 'Modifier',
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
  `name` varchar(100) NOT NULL COMMENT 'AccessId, like source app name',
  `apikey` varchar(255) NOT NULL COMMENT 'AccessKey, unique key',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `creator` varchar(255) DEFAULT NULL  COMMENT 'Create user',
  `modifier` varchar(200) DEFAULT NULL  COMMENT 'Modified user',
  `role` varchar(100) DEFAULT NULL  COMMENT 'Apikey role type',
  `status` tinyint DEFAULT NULL COMMENT 'Status, like online, offline',
  `access_config` mediumtext COMMENT 'Extra config',
  `desc` varchar(200) DEFAULT NULL COMMENT 'Description',
  PRIMARY KEY (`id`),
  UNIQUE KEY `apikey_uk_apikey` (`apikey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for cluster
-- ----------------------------
CREATE TABLE IF NOT EXISTS `cluster` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `ip` varchar(45) NOT NULL COMMENT 'Machine ip',
  `hostname` varchar(200) DEFAULT NULL COMMENT 'Machine hostname',
  `role` varchar(45) NOT NULL COMMENT 'Machine role',
  `last_heartbeat_time` bigint NOT NULL COMMENT 'Machine last heartbeat time',
  `manual_close` tinyint DEFAULT NULL COMMENT 'When value is 1, remove from the cluster',
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
  `task_id` varchar(100) NOT NULL COMMENT 'Task name',
  `period` bigint NOT NULL COMMENT 'Task execute period',
  `status` varchar(45) NOT NULL COMMENT 'Current task status',
  `cluster_ip` varchar(45) NOT NULL COMMENT 'Execute task machine ip',
  `context` longtext COMMENT 'Extra config',
  `result` varchar(50) DEFAULT NULL COMMENT 'Execute task result',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for custom_plugin
-- ----------------------------
CREATE TABLE IF NOT EXISTS `custom_plugin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant',
  `parent_folder_id` bigint NOT NULL DEFAULT '-1'  COMMENT 'Parent folder id',
  `name` varchar(100) NOT NULL COMMENT 'Config name',
  `plugin_type` varchar(45) NOT NULL COMMENT 'Config type',
  `status` varchar(30) NOT NULL COMMENT 'Config status',
  `period_type` varchar(45) NOT NULL COMMENT 'Collect period, like 1s, 5s, 1min',
  `conf` longtext NOT NULL COMMENT 'Config meta',
  `sample_log` longtext COMMENT 'Sample log',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) NOT NULL COMMENT 'Create user',
  `modifier` varchar(45) NOT NULL COMMENT 'Modified user',
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
  `title` varchar(255) NOT NULL COMMENT 'Config name',
  `conf` text NOT NULL COMMENT 'Config meta',
  `creator` varchar(45) DEFAULT NULL COMMENT 'Create user',
  `modifier` varchar(45) DEFAULT NULL COMMENT 'Modified user',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Relate tenant code',
  `type` varchar(100) DEFAULT NULL COMMENT 'dashboard type',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for display_menu
-- ----------------------------
CREATE TABLE IF NOT EXISTS `display_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `ref_id` bigint NOT NULL COMMENT 'Relate id, like integration id',
  `type` varchar(100) NOT NULL COMMENT 'Menu type, like app',
  `config` mediumtext NOT NULL COMMENT 'Config meta',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `creator` varchar(100) NOT NULL COMMENT 'Create user',
  `modifier` varchar(100) NOT NULL COMMENT 'Modified user',
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
  `name` varchar(100) NOT NULL COMMENT 'Config name',
  `ref_id` bigint NOT NULL COMMENT 'Relate id, like integration id',
  `type` varchar(100) NOT NULL COMMENT 'Template config type',
  `config` mediumtext NOT NULL COMMENT 'Config meta',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `creator` varchar(100) NOT NULL COMMENT 'Create user',
  `modifier` varchar(100) NOT NULL COMMENT 'Modified user',
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
  `name` varchar(100) NOT NULL COMMENT 'Folder name',
  `parent_folder_id` bigint DEFAULT NULL COMMENT 'parent folder id',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `creator` varchar(100) DEFAULT NULL COMMENT 'Create user',
  `modifier` varchar(100) DEFAULT NULL COMMENT 'Modified user',
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
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `product` varchar(100) NOT NULL COMMENT 'Integration product name',
  `item` varchar(100) NOT NULL COMMENT 'Monitor item, like portcheck',
  `name` varchar(100) NOT NULL COMMENT 'Monitor item name, like app name',
  `config` mediumtext COMMENT 'Config meta',
  `deleted` tinyint NOT NULL COMMENT 'Softed delete, when 1 indicates that the configuration has been deleted',
  `custom` tinyint NOT NULL COMMENT 'Custom monitor config',
  `creator` varchar(100) NOT NULL COMMENT 'Create user',
  `modifier` varchar(100) NOT NULL COMMENT 'Modified user',
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
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `name` varchar(100) NOT NULL COMMENT 'Plugin name',
  `workspace` varchar(100) NOT NULL COMMENT 'Plugin workspace',
  `product` varchar(100) NOT NULL COMMENT 'Integration product name',
  `type` varchar(200) NOT NULL COMMENT 'Plugin type',
  `status` tinyint NOT NULL COMMENT 'Status',
  `json` longtext NOT NULL COMMENT 'Config meta',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) NOT NULL COMMENT 'Create user',
  `modifier` varchar(45) NOT NULL COMMENT 'Modified user',
  `collect_range` longtext COMMENT 'Collect meta range',
  `template` longtext COMMENT 'Display template',
  `version` varchar(45) DEFAULT NULL COMMENT 'Config version',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_name` (`tenant`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for integration_product
-- ----------------------------
CREATE TABLE IF NOT EXISTS `integration_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(100) NOT NULL COMMENT 'Product name',
  `profile` longtext COMMENT 'Desprition',
  `overview` longtext COMMENT 'Product function',
  `configuration` longtext COMMENT 'Config meta',
  `metrics` longtext COMMENT 'Collect metrics',
  `status` tinyint DEFAULT NULL COMMENT 'Status',
  `type` varchar(100) DEFAULT NULL COMMENT 'Type',
  `form` longtext COMMENT 'Front form',
  `template` longtext COMMENT 'Display template',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) DEFAULT NULL COMMENT 'Create user',
  `modifier` varchar(45) DEFAULT NULL COMMENT 'Modified user',
  `version` varchar(45) DEFAULT NULL COMMENT 'Product version',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for marketplace_plugin
-- ----------------------------
CREATE TABLE IF NOT EXISTS `marketplace_plugin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `workspace` varchar(100) NOT NULL COMMENT 'Plugin workspace',
  `name` varchar(100) NOT NULL COMMENT 'Plugin name',
  `product` varchar(100) NOT NULL COMMENT 'Product name',
  `status` tinyint DEFAULT NULL COMMENT 'Status',
  `type` varchar(150) DEFAULT NULL COMMENT 'Type',
  `data_range` longtext COMMENT 'Data usage range',
  `json` longtext COMMENT 'Config meta',
  `creator` varchar(100) NOT NULL COMMENT 'Create user',
  `modifier` varchar(100) NOT NULL COMMENT 'Modified user',
  `version` varchar(45) DEFAULT NULL COMMENT 'Product version',
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
  `name` varchar(100) NOT NULL COMMENT 'Product name',
  `profile` longtext COMMENT 'Product description',
  `overview` longtext COMMENT 'Product function',
  `configuration` longtext COMMENT 'Config meta',
  `price` longtext COMMENT 'Product price list',
  `feature` longtext COMMENT 'Product feature list',
  `status` tinyint DEFAULT NULL COMMENT 'Status',
  `type` varchar(150) DEFAULT NULL COMMENT 'Type',
  `version` varchar(45) DEFAULT NULL COMMENT 'Product version',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(100) NOT NULL COMMENT 'Create user',
  `modifier` varchar(100) NOT NULL COMMENT 'Modified user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for meta_data
-- ----------------------------
CREATE TABLE IF NOT EXISTS `meta_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `_uk` varchar(100) NOT NULL COMMENT 'COL uk',
  `_table` varchar(80) NOT NULL COMMENT 'Relate table name',
  `_type` varchar(45) NOT NULL COMMENT 'Meta type',
  `_workspace` varchar(100) DEFAULT NULL COMMENT 'Meta workspace',
  `_status` varchar(45) DEFAULT NULL COMMENT 'Meta status',
  `_basic` mediumtext COMMENT 'Basic info meta',
  `_labels` longtext COMMENT 'Label meta',
  `_annotations` longtext COMMENT 'Annotations',
  `_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Modified time',
  `_modifier` varchar(100) DEFAULT NULL COMMENT 'Modifier user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `_uk_UNIQUE` (`_uk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
CREATE TABLE IF NOT EXISTS `meta_table` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL COMMENT 'Meta table name',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
  `table_schema` longtext COMMENT 'Table schema',
  `status` varchar(40) DEFAULT NULL COMMENT 'Table status',
  `config` longtext COMMENT 'Table relate config',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `creator` varchar(45) NOT NULL COMMENT 'Creator user',
  `modifier` varchar(45) NOT NULL COMMENT 'Modifier user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`),
  KEY `index_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for metadata_dictvalue
-- ----------------------------
CREATE TABLE IF NOT EXISTS `metadata_dictvalue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `type` varchar(45) NOT NULL COMMENT 'Kv type',
  `dict_key` varchar(45) NOT NULL COMMENT 'Key',
  `dict_value` mediumtext NOT NULL COMMENT 'Value',
  `dict_value_type` varchar(45) DEFAULT NULL COMMENT 'Value type, like string, double',
  `dict_desc` varchar(45) DEFAULT NULL COMMENT 'Value description',
  `version` int NOT NULL COMMENT 'Kv version, like 1,2,3',
  `creator` varchar(45) NOT NULL COMMENT 'Creator user',
  `modifier` varchar(45) NOT NULL COMMENT 'Modifier user',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for openmetrics_scraper
-- ----------------------------
CREATE TABLE IF NOT EXISTS `openmetrics_scraper` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL COMMENT 'Config name',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant code',
  `conf` text COMMENT 'Tenant conf',
  `creator` varchar(45) DEFAULT NULL COMMENT 'Creator user',
  `modifier` varchar(45) DEFAULT NULL COMMENT 'Modifier user',
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
  `app_id` varchar(16) NOT NULL COMMENT 'Relate appid',
  `env_id` varchar(16) NOT NULL COMMENT 'Relate envId',
  `app_name` varchar(64) NOT NULL COMMENT 'Relate appName',
  `interface_type` varchar(16) NOT NULL COMMENT 'Interface type',
  `interface_name` varchar(256) NOT NULL COMMENT 'Interface name',
  `response_type` varchar(64) NOT NULL COMMENT 'ModelMap indicates acquisition in modelmap, and Return indicates acquisition of return value',
  `response_property` varchar(64) DEFAULT NULL COMMENT 'The attribute taken from the modelmap, this field is only required when ModelMap is selected',
  `error_code_config` varchar(8192) NOT NULL COMMENT 'Error code',
  `global_open` varchar(1) NOT NULL COMMENT 'Whether the rule takes effect globally: T indicates that the rule takes effect globally, and F indicates that the rule does not take effect globally',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_teai` (`app_id`,`env_id`,`app_name`,`interface_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Problem Location Business Rules Table';

-- ----------------------------
-- Table structure for tenant
-- -------------------- --------
CREATE TABLE IF NOT EXISTS `tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `name` varchar(255) NOT NULL COMMENT 'Tenant name',
  `code` varchar(45) NOT NULL COMMENT 'Tenant code',
  `desc` varchar(500) DEFAULT NULL COMMENT 'Tenant description',
  `json` text COMMENT 'Tenant information',
  `md5` varchar(45) NOT NULL COMMENT 'Tenant md5',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  `product` varchar(255) DEFAULT NULL COMMENT 'Relate product',
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
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant code',
  `storage` varchar(4096) NOT NULL COMMENT 'Storage configuration',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant` (`tenant`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `user_login_name` varchar(45) NOT NULL COMMENT 'User login name',
  `relate_id` varchar(300) NOT NULL COMMENT 'Favorite relate id',
  `type` varchar(50) NOT NULL COMMENT 'Relate type, like log, app, alarm',
  `name` varchar(200) NOT NULL COMMENT 'Favorite name',
  `url` varchar(1000) NOT NULL COMMENT 'Relate url',
  `tenant` varchar(255) NOT NULL COMMENT 'Relate tenant code',
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
  `table_name` varchar(200) DEFAULT NULL  COMMENT 'Oplog relate table name',
  `table_entity_id` varchar(200) DEFAULT NULL  COMMENT 'Oplog relate table unique id',
  `op_type` varchar(45) DEFAULT NULL  COMMENT 'Oplog type, like create,update,delete',
  `op_before_context` longtext COMMENT 'Oplog before context json',
  `op_after_context` longtext COMMENT 'Oplog after context json',
  `name` varchar(200) DEFAULT NULL COMMENT 'Oplog name',
  `relate` varchar(1000) DEFAULT NULL COMMENT 'Oplog relate meta',
  `tenant` varchar(255) DEFAULT NULL COMMENT 'Oplog relate tenant code',
  `creator` varchar(45) DEFAULT NULL COMMENT 'Create user',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ----------------------------
-- Table structure for workspace
-- ----------------------------
CREATE TABLE IF NOT EXISTS `workspace` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id',
  `tenant` varchar(255) NOT NULL COMMENT 'Tenant code',
  `name` varchar(100) NOT NULL  COMMENT 'Workspace name',
  `desc` varchar(100) DEFAULT NULL  COMMENT 'Workspace description',
  `config` mediumtext DEFAULT NULL  COMMENT 'Extra config meta',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
  PRIMARY KEY(`id`),
  UNIQUE KEY `id_UNIQUE`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;