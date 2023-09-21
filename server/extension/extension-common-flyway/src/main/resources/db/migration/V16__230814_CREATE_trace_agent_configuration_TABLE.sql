-- ----------------------------
-- Table structure for trace_agent_configuration
-- Store the user's configuration on the page
-- ----------------------------
CREATE TABLE IF NOT EXISTS `trace_agent_configuration` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `tenant` varchar(255) NOT NULL COMMENT 'Tenant',
    `service` varchar(255) NOT NULL COMMENT 'Name of the service that trace agent collects',
    `type` varchar(100) NOT NULL COMMENT 'Type of trace agent, skywalking、opentelemetry...',
    `language` varchar(100) NOT NULL COMMENT 'Language of trace agent, java、php、nodejs、python',
    `value` longtext NOT NULL COMMENT 'To send the configuration to the trace agent',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ----------------------------
-- Table structure for trace_agent_configuration_properties
-- Trace configuration metadata table
-- ----------------------------
CREATE TABLE IF NOT EXISTS `trace_agent_configuration_properties` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `type` varchar(100) NOT NULL COMMENT 'Type of trace agent, skywalking、opentelemetry...',
    `language` varchar(100) NOT NULL COMMENT 'Language of trace agent, java、php、nodejs、python',
    `prop_key` varchar(255) NOT NULL COMMENT 'Property key of the  agent configuration',
    `name` varchar(255) NOT NULL COMMENT 'Name of the  agent configuration',
    `c_name` varchar(255) NOT NULL COMMENT 'The Chinese name of the configuration',
    `description` longtext COMMENT 'The description of the configuration',
    `c_description` longtext COMMENT 'Chinese configuration description',
    `check_expression` varchar(255) COMMENT 'Used to validate the configuration value entered by the user, which can be a regular expression',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
