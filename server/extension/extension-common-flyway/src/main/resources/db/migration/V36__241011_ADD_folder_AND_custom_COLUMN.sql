ALTER TABLE `folder` ADD COLUMN `recent_alarm_history_id` varchar(5000) DEFAULT NULL COMMENT '(归档)最近报警历史ID';
ALTER TABLE `custom_plugin` ADD COLUMN `recent_alarm_history_id` varchar(5000) DEFAULT NULL COMMENT '(归档)最近报警历史ID';
