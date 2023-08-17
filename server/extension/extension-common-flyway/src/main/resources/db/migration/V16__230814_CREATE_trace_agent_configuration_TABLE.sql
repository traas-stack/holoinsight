DROP TABLE IF EXISTS `agent_configuration`;
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

INSERT INTO `trace_agent_configuration_properties` (`type`, `language`, `prop_key`, `name`, `c_name`, `description`, `c_description`, `check_expression`) VALUES ('skywalking', 'java', 'agent.sample_n_per_3_secs', 'The number of traces sampled every three seconds', '每三秒采样trace条数', 'Negative or zero means off, by default.SAMPLE_N_PER_3_SECS means sampling N trace in 3 seconds tops.', '默认情况下，负数或零表示全部采样。SAMPLE_N_PER_3_SECS 表示在 3 秒内采样 N 个 trace。', '^\\d+$');
INSERT INTO `trace_agent_configuration_properties` (`type`, `language`, `prop_key`, `name`, `c_name`, `description`, `c_description`, `check_expression`) VALUES ('skywalking', 'java', 'agent.ignore_suffix', 'Interface call filtering', '接口调用过滤', 'Default: .jpg,.jpeg,.js,.css,.png,.bmp,.gif,.ico,.mp3,.mp4,.html,.svg, If the operation name of the first span is included in this set, this segment should be ignored.', 'Default: .jpg,.jpeg,.js,.css,.png,.bmp,.gif,.ico,.mp3,.mp4,.html,.svg, 如果第一个span的操作名称的后缀包含在该集合中，则应忽略该span。', '');
INSERT INTO `trace_agent_configuration_properties` (`type`, `language`, `prop_key`, `name`, `c_name`, `description`, `c_description`, `check_expression`) VALUES ('skywalking', 'java', 'plugin.jdbc.trace_sql_parameters', 'Collect sql parameters', '采集sql参数', 'Default: false, If set to true, the parameters of the sql (typically java.sql.PreparedStatement) would be collected.', 'Default: false, 如果设置为 true，则将收集 sql 的参数。', '');
INSERT INTO `trace_agent_configuration_properties` (`type`, `language`, `prop_key`, `name`, `c_name`, `description`, `c_description`, `check_expression`) VALUES ('skywalking', 'java', 'holoinsight.slow.sql.threshold', 'Slow sql threshold', '慢sql阈值', 'Unit: ms，Default: 300, Exceeding this threshold is considered slow sql，please enter a number greater than zero.', 'Unit: ms，Default: 300, 超过该阈值被认定为慢sql, 请输入大于零的数字。', '^[1-9]\d*$');