/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alert_notification_template
-- ----------------------------
CREATE TABLE `alert_notification_template` (
                                    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
                                    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                    `template_name` varchar(255) NULL comment '模板名称',
                                    `scene_type` varchar(255) NULL comment '场景类型:server、miniapp、iot',
                                    `be_default` tinyint(4) NULL comment '是否为默认模板',
                                    `channel_type` varchar(255) NULL comment '发送渠道：dingtalk、sms',
                                    `template_config` mediumtext NULL comment '模板配置',
                                    `tenant` varchar(255) NULL comment '租户',
                                    `workspace` varchar(255) NULL comment '工作空间',
                                    `creator` varchar(255) NULL comment '创建者',
                                    `modifier` varchar(255) NULL comment '修改者',
                                    PRIMARY KEY(`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '告警模板';