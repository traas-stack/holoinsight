-- ----------------------------
-- Table structure for monitor_instance
-- ----------------------------
CREATE TABLE `monitor_instance` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    `instance` varchar(255) NULL comment '实例标识',
    `type` varchar(255) NULL comment '实例类型',
    `tenant` varchar(255) NULL comment '租户',
    `tenant_name` varchar(255) NULL comment '租户名称',
    `workspace` varchar(255) NULL comment '工作空间',
    `workspace_name` varchar(255) NULL comment '工作空间名称',
    `meter_state` tinyint(4) NULL comment '计量状态',
    `billing_state` tinyint(4) NULL comment '计费状态',
    `config` mediumtext NULL comment '扩展配置',
    `deleted` tinyint(4) NULL comment '软删除标记',
    PRIMARY KEY(`id`),
    KEY `idx_type`(`type`),
    UNIQUE KEY `unique_idx_instance_type`(`instance`, `type`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '监控服务实例';