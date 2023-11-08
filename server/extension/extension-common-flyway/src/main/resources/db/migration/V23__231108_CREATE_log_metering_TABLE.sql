-- ----------------------------
-- Table structure for log_metering
-- ----------------------------
CREATE TABLE `log_metering` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `region` varchar(64) DEFAULT NULL COMMENT '站点',
    `project_name` varchar(64) DEFAULT NULL COMMENT '项目名',
    `app_name` varchar(64) DEFAULT NULL COMMENT '应用名',
    `arch_domain_name` varchar(64) DEFAULT NULL COMMENT '一级架构域名',
    `arch_domain_code` varchar(64) DEFAULT NULL COMMENT '一级架构域编码',
    `usage_cy` varchar(64) DEFAULT NULL COMMENT '价格计费，单位元',
    `read_count` varchar(64) DEFAULT NULL COMMENT '百万次',
    `write_count` varchar(64) DEFAULT NULL COMMENT '百万次',
    `inflow` varchar(64) DEFAULT NULL COMMENT '写入流量GB',
    `outflow` varchar(64) DEFAULT NULL COMMENT '读取流量GB',
    `index_flow` varchar(64) DEFAULT NULL COMMENT '索引流量GB',
    `storage` varchar(64) DEFAULT NULL COMMENT '存储大小GB',
    `uid` varchar(64) DEFAULT NULL COMMENT '账号id',
    `dt_cy` varchar(16) DEFAULT NULL COMMENT '拷贝dt',
    `dt` varchar(16) DEFAULT NULL COMMENT 'dt',
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '日志计量表'