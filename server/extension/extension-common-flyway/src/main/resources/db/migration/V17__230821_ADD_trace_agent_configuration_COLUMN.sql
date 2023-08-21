-- ----------------------------
-- Table structure for trace_agent_configuration
-- ----------------------------
ALTER TABLE `trace_agent_configuration` ADD COLUMN `creator` varchar(255) NULL DEFAULT NULL comment 'Create user';
ALTER TABLE `trace_agent_configuration` ADD COLUMN `modifier` varchar(255) NULL DEFAULT NULL comment 'Modified user';