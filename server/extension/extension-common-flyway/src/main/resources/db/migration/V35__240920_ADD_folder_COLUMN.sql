ALTER TABLE `folder` ADD COLUMN `alarmed` tinyint(4) DEFAULT NULL COMMENT '(归档)是否报警';
ALTER TABLE `folder` ADD COLUMN `recent_alarm_rule_unique_id` varchar(5000) DEFAULT NULL COMMENT '(归档)最近报警unique ID';
ALTER TABLE `folder` ADD COLUMN `recent_alarm` int(11) DEFAULT NULL COMMENT '(归档)最近报警数量';
ALTER TABLE `folder` ADD COLUMN `alarm_rrd_time` bigint(20) DEFAULT NULL COMMENT '归档时间';