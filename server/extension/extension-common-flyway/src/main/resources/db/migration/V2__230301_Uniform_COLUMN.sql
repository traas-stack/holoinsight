
DROP PROCEDURE IF EXISTS Uniform_Holoinsight_Column;
DELIMITER $$
-- 1 ADD COLUMN,2 UPDATE COLUMN,3 DELETE COLUMN
CREATE PROCEDURE Uniform_Holoinsight_Column(TableName VARCHAR(50),ColumnName VARCHAR(50),SqlStr VARCHAR(4000),CType INT)
BEGIN
DECLARE RowCount INT;
SET RowCount=0;
SELECT COUNT(*) INTO RowCount  FROM INFORMATION_SCHEMA.Columns
WHERE table_schema= DATABASE() AND table_name=TableName AND column_name=ColumnName;
-- ADD COLUMN
IF (CType=1 AND RowCount<=0) THEN
SET SqlStr := CONCAT( 'ALTER TABLE ',TableName,' ADD COLUMN ',ColumnName,' ',SqlStr);
-- UPDATE COLUMN
ELSEIF (CType=2 AND RowCount>0)  THEN
SET SqlStr := CONCAT('ALTER TABLE ',TableName,' MODIFY  ',ColumnName,' ',SqlStr);
-- DELETE COLUMN
ELSEIF (CType=3 AND RowCount>0) THEN
SET SqlStr := CONCAT('ALTER TABLE  ',TableName,' DROP COLUMN  ',ColumnName);
ELSE  SET SqlStr :='';
END IF;
-- EXEC
IF (SqlStr<>'') THEN
SET @SQL1 = SqlStr;
PREPARE stmt1 FROM @SQL1;
EXECUTE stmt1;
END IF;
END$$
DELIMITER ;

-- ----------------------------
-- Table structure for alarm_block
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_block","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","creator","varchar(64) DEFAULT NULL COMMENT 'Creator user'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","modifier","varchar(64) DEFAULT NULL COMMENT 'Modifier user'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","start_time","timestamp NULL DEFAULT NULL COMMENT 'Start time'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","end_time","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'End time'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","tags","longtext COMMENT 'Masked dimension'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","unique_id","varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","reason","varchar(256) DEFAULT NULL COMMENT 'Reason of the alarm is blocked'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","extra","longtext COMMENT 'Extra message'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","tenant","varchar(255) DEFAULT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","hour","tinyint DEFAULT '0' COMMENT 'Blocked duration(hour)'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","minute","tinyint DEFAULT '0' COMMENT 'Blocked duration(minute)'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","source_type","varchar(64) DEFAULT NULL COMMENT 'Source type of alarm block'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","source_id","bigint DEFAULT NULL COMMENT 'Source id of alarm block'",1);

-- ----------------------------
-- Table structure for agent_configuration
-- ----------------------------
  CALL Uniform_Holoinsight_Column("agent_configuration","tenant","varchar(255) NOT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","service","varchar(100) NOT NULL COMMENT 'Name of the service that trace agent collects'",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","app_id","varchar(100) NOT NULL COMMENT 'Cloudrun app Id'",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","env_id","varchar(100) NOT NULL COMMENT 'Cloudrun env Id'",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","value","longtext NOT NULL COMMENT 'To send the configuration to the trace agent'",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for alarm_ding_ding_robot
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","creator","varchar(64) DEFAULT NULL COMMENT 'Creator'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","modifier","varchar(64) DEFAULT NULL COMMENT 'Modifier'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","group_name","varchar(64) NOT NULL COMMENT 'Specified name of dingtalk robot'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","robot_url","varchar(128) NOT NULL COMMENT 'Webhook url of dingtalk robot'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","extra","longtext COMMENT 'Extra message'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","tenant","varchar(255) DEFAULT NULL COMMENT 'Tenant'",1);

-- ----------------------------
-- Table structure for alarm_group
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_group","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","creator","varchar(64) DEFAULT NULL COMMENT 'Creator'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","modifier","varchar(64) DEFAULT NULL COMMENT 'Modifier'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","group_name","varchar(256) NOT NULL COMMENT 'Specified name of alarm group'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","group_info","mediumtext  COMMENT 'Information of alarm group'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","tenant","varchar(255) DEFAULT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","sms_phone","mediumtext COMMENT 'Alarm SMS number'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","dyvms_phone","mediumtext COMMENT 'Alarm phone number'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","dd_webhook","mediumtext COMMENT 'Dingtalk robot'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","email_address","mediumtext",1);
  CALL Uniform_Holoinsight_Column("alarm_group","env_type","varchar(100) DEFAULT NULL COMMENT 'Environment type'",1);

-- ----------------------------
-- Table structure for alarm_history
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_history","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","alarm_time","timestamp NULL DEFAULT NULL COMMENT 'The time of data detection'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","recover_time","timestamp NULL DEFAULT NULL COMMENT 'The time of data detection passes'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","duration","bigint DEFAULT NULL COMMENT 'Alert duration(ms)'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","unique_id","varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm rule type and alarm rule id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","rule_name","varchar(256) NOT NULL COMMENT 'Alarm rule name'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","alarm_level","varchar(32) NOT NULL COMMENT 'Alarm level'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","trigger_content","varchar(256) DEFAULT NULL COMMENT 'The description of the alarm rule'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","extra","longtext COMMENT 'Extra message'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","tenant","varchar(255) DEFAULT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","source_type","varchar(64) DEFAULT NULL COMMENT 'Source type of alarm history'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","source_id","bigint DEFAULT NULL COMMENT 'Source id of alarm history'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","env_type","varchar(255) DEFAULT NULL COMMENT 'Environment type'",1);

-- ----------------------------
-- Table structure for alarm_history_detail
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_history_detail","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","alarm_time","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The time of data detection'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","unique_id","varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","history_id","bigint NOT NULL DEFAULT '-1' COMMENT 'Alarm history id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","tags","longtext COMMENT 'Alarm dimension'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","alarm_content","mediumtext COMMENT 'Introduction of trigger mode'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","datasource","mediumtext COMMENT 'Datasource of the data detection'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","extra","longtext COMMENT 'Extra message'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","tenant","varchar(255) NOT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","source_type","varchar(64) DEFAULT NULL COMMENT 'Source type of alarm history detail'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","source_id","bigint DEFAULT NULL COMMENT 'Source id of alarm history detail'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","env_type","varchar(255) DEFAULT NULL COMMENT 'Environment type'",1);

-- ----------------------------
-- Table structure for alarm_rule
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_rule","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule_name","varchar(256) NOT NULL COMMENT 'Alarm rule name'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule_type","varchar(32) NOT NULL COMMENT 'AI or RULE'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","creator","varchar(64) DEFAULT NULL COMMENT 'Creator'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","modifier","varchar(64) DEFAULT NULL COMMENT 'Modifier'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","alarm_level","varchar(32) NOT NULL COMMENT 'Alarm level'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule_describe","varchar(256) DEFAULT NULL COMMENT 'Alarm rule description'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule","mediumtext NOT NULL COMMENT 'Alarm rule configuration'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","pql","varchar(512) DEFAULT NULL COMMENT 'Pql content'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","time_filter","mediumtext NOT NULL COMMENT 'Effective time'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","status","tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the rule is effective'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","is_merge","tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the alarm is merged'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","merge_type","varchar(64) DEFAULT NULL  COMMENT 'Merge type'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","recover","tinyint NOT NULL DEFAULT '1' COMMENT 'Whether the recovery notification is enabled'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","notice_type","varchar(32) DEFAULT NULL COMMENT 'The way of notification'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","extra","longtext COMMENT 'Extra message'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","tenant","varchar(255) DEFAULT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","alarm_content","varchar(255) DEFAULT NULL COMMENT 'Alarm content'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","source_type","varchar(64) DEFAULT NULL COMMENT 'Source type of alarm rule'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","source_id","bigint DEFAULT NULL COMMENT 'Source id of alarm rule'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","env_type","varchar(255) DEFAULT NULL COMMENT 'Environment type'",1);

-- ----------------------------
-- Table structure for alarm_subscribe
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_subscribe","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","creator","varchar(64) DEFAULT NULL COMMENT 'Creator'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","modifier","varchar(64) DEFAULT NULL COMMENT 'Modifier'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","subscriber","varchar(128) DEFAULT NULL COMMENT 'User id of subscriber'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","group_id","bigint NOT NULL DEFAULT '-1' COMMENT 'Id of alarm group'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","unique_id","varchar(64) NOT NULL DEFAULT '-1' COMMENT 'Alarm rule type and alarm rule id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","notice_type","varchar(256) DEFAULT NULL COMMENT 'The way of notification'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","status","tinyint NOT NULL DEFAULT '1' COMMENT 'Status of subscriptions'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","tenant","varchar(255) DEFAULT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","source_type","varchar(64) DEFAULT NULL COMMENT 'Source type of alarm rule'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","source_id","bigint DEFAULT NULL COMMENT 'Source id of alarm rule'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","env_type","varchar(100) DEFAULT NULL COMMENT 'Environment type'",1);

-- ----------------------------
-- Table structure for alarm_webhook
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_webhook","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","creator","varchar(64) DEFAULT NULL COMMENT 'Creator'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","modifier","varchar(64) DEFAULT NULL COMMENT 'Modifier'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","webhook_name","varchar(256) NOT NULL COMMENT 'Alarm webhook name'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","status","tinyint NOT NULL DEFAULT '1' COMMENT 'Callback state'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_type","varchar(16) NOT NULL COMMENT 'Http method'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_url","varchar(256) NOT NULL COMMENT 'Http(s) url'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_headers","mediumtext COMMENT 'Http header'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_body","mediumtext COMMENT 'Http body template json'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","type","tinyint NOT NULL DEFAULT '2' COMMENT 'Callback type'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","extra","longtext COMMENT 'Extra message'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","role","varchar(200) DEFAULT NULL COMMENT 'Webhook role'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","tenant","varchar(255) NOT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","webhook_test","mediumtext COMMENT 'Debugging data'",1);

-- ----------------------------
-- Table structure for alertmanager_webhook
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","name","varchar(255) NOT NULL COMMENT 'Alarm webhook name'",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","tenant","varchar(255) NOT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","creator","varchar(45) DEFAULT NULL COMMENT 'Creator'",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","modifier","varchar(45) DEFAULT NULL COMMENT 'Modifier'",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for apikey
-- ----------------------------
  CALL Uniform_Holoinsight_Column("apikey","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("apikey","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("apikey","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("apikey","name","varchar(100) NOT NULL COMMENT 'AccessId, like source app name'",1);
  CALL Uniform_Holoinsight_Column("apikey","apikey","varchar(255) NOT NULL COMMENT 'AccessKey, unique key'",1);
  CALL Uniform_Holoinsight_Column("apikey","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("apikey","creator","varchar(255) DEFAULT NULL  COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("apikey","modifier","varchar(200) DEFAULT NULL  COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("apikey","role","varchar(100) DEFAULT NULL  COMMENT 'Apikey role type'",1);
  CALL Uniform_Holoinsight_Column("apikey","status","tinyint DEFAULT NULL COMMENT 'Status, like online, offline'",1);
  CALL Uniform_Holoinsight_Column("apikey","access_config","mediumtext COMMENT 'Extra config'",1);
  CALL Uniform_Holoinsight_Column("apikey","desc","varchar(200) DEFAULT NULL COMMENT 'Description'",1);

-- ----------------------------
-- Table structure for cluster
-- ----------------------------
  CALL Uniform_Holoinsight_Column("cluster","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("cluster","ip","varchar(45) NOT NULL COMMENT 'Machine ip'",1);
  CALL Uniform_Holoinsight_Column("cluster","hostname","varchar(200) DEFAULT NULL COMMENT 'Machine hostname'",1);
  CALL Uniform_Holoinsight_Column("cluster","role","varchar(45) NOT NULL COMMENT 'Machine role'",1);
  CALL Uniform_Holoinsight_Column("cluster","last_heartbeat_time","bigint NOT NULL COMMENT 'Machine last heartbeat time'",1);
  CALL Uniform_Holoinsight_Column("cluster","manual_close","tinyint DEFAULT NULL COMMENT 'When value is 1, remove from the cluster'",1);
  CALL Uniform_Holoinsight_Column("cluster","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("cluster","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for cluster_task
-- ----------------------------
  CALL Uniform_Holoinsight_Column("cluster_task","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","task_id","varchar(100) NOT NULL COMMENT 'Task name'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","period","bigint NOT NULL COMMENT 'Task execute period'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","status","varchar(45) NOT NULL COMMENT 'Current task status'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","cluster_ip","varchar(45) NOT NULL COMMENT 'Execute task machine ip'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","context","longtext COMMENT 'Extra config'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","result","varchar(50) DEFAULT NULL COMMENT 'Execute task result'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("cluster_task","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for custom_plugin
-- ----------------------------
  CALL Uniform_Holoinsight_Column("custom_plugin","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","parent_folder_id","bigint NOT NULL DEFAULT '-1'  COMMENT 'Parent folder id'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","name","varchar(100) NOT NULL COMMENT 'Config name'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","plugin_type","varchar(45) NOT NULL COMMENT 'Config type'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","status","varchar(30) NOT NULL COMMENT 'Config status'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","period_type","varchar(45) NOT NULL COMMENT 'Collect period, like 1s, 5s, 1min'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","conf","longtext NOT NULL COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","sample_log","longtext COMMENT 'Sample log'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","creator","varchar(45) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","modifier","varchar(45) NOT NULL COMMENT 'Modified user'",1);

-- ----------------------------
-- Table structure for dashboard
-- ----------------------------
  CALL Uniform_Holoinsight_Column("dashboard","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("dashboard","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("dashboard","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("dashboard","title","varchar(255) NOT NULL COMMENT 'Config name'",1);
  CALL Uniform_Holoinsight_Column("dashboard","conf","text NOT NULL COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("dashboard","creator","varchar(45) DEFAULT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("dashboard","modifier","varchar(45) DEFAULT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("dashboard","tenant","varchar(255) DEFAULT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("dashboard","type","varchar(100) DEFAULT NULL COMMENT 'dashboard type'",1);

-- ----------------------------
-- Table structure for display_menu
-- ----------------------------
  CALL Uniform_Holoinsight_Column("display_menu","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("display_menu","ref_id","bigint NOT NULL COMMENT 'Relate id, like integration id'",1);
  CALL Uniform_Holoinsight_Column("display_menu","type","varchar(100) NOT NULL COMMENT 'Menu type, like app'",1);
  CALL Uniform_Holoinsight_Column("display_menu","config","mediumtext NOT NULL COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("display_menu","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("display_menu","creator","varchar(100) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("display_menu","modifier","varchar(100) NOT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("display_menu","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("display_menu","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for display_template
-- ----------------------------
  CALL Uniform_Holoinsight_Column("display_template","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("display_template","name","varchar(100) NOT NULL COMMENT 'Config name'",1);
  CALL Uniform_Holoinsight_Column("display_template","ref_id","bigint NOT NULL COMMENT 'Relate id, like integration id'",1);
  CALL Uniform_Holoinsight_Column("display_template","type","varchar(100) NOT NULL COMMENT 'Template config type'",1);
  CALL Uniform_Holoinsight_Column("display_template","config","mediumtext NOT NULL COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("display_template","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("display_template","creator","varchar(100) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("display_template","modifier","varchar(100) NOT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("display_template","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("display_template","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for folder
-- ----------------------------
  CALL Uniform_Holoinsight_Column("folder","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("folder","name","varchar(100) NOT NULL COMMENT 'Folder name'",1);
  CALL Uniform_Holoinsight_Column("folder","parent_folder_id","bigint DEFAULT NULL COMMENT 'parent folder id'",1);
  CALL Uniform_Holoinsight_Column("folder","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("folder","creator","varchar(100) DEFAULT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("folder","modifier","varchar(100) DEFAULT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("folder","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("folder","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("folder","ext_info","varchar(2000) DEFAULT NULL",1);

-- ----------------------------
-- Table structure for gaea_agent
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_agent","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","tenant","varchar(255) NOT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","agent_id","varchar(255) NOT NULL COMMENT 'Agent id'",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","json","text NOT NULL COMMENT 'A json contains agent content'",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","status","int NOT NULL COMMENT 'Agent status. 0 means normal, 1 means deleted.'",1);

-- ----------------------------
-- Table structure for gaea_cluster_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","tenant","varchar(255) NOT NULL COMMENT 'Registry cluster tenant'",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","group","varchar(16) NOT NULL COMMENT 'Registry cluster group'",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","json","varchar(4096) NOT NULL COMMENT 'Registry cluster group config'",1);

-- ----------------------------
-- Table structure for gaea_collect_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_collect_config","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","tenant","varchar(255) NOT NULL COMMENT 'Tenant'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","table_name","varchar(255) NOT NULL COMMENT 'Name of the collect config. It will be used by key or metric name generation.'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","json","longtext NOT NULL COMMENT 'A json to describe collect config'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","deleted","int NOT NULL COMMENT 'Deleted flag. Set to 1 to mark as deleted'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","version","bigint NOT NULL COMMENT 'When update a config, we need to mark-delete old db record and create a new db record with new version in same transaction. These two record must have same gmt_modified.'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","collect_range","text NOT NULL COMMENT 'A json to describe the collect range'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","executor_selector","text NOT NULL COMMENT 'A json to describe which agent to run this collect config'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","ref_id","varchar(100) DEFAULT NULL COMMENT 'An internal id for Integration'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","biz_tenant","varchar(255) DEFAULT NULL COMMENT 'Deprecated field'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","type","varchar(255) DEFAULT NULL COMMENT 'Type of collect. Agent use this field to resolve json to specified type'",1);

-- ----------------------------
-- Table structure for gaea_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_config","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","tenant","varchar(255) NOT NULL COMMENT 'Tenant for isolating data. Has no relation to tenants on the product tier.'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","conf_key","varchar(255) NOT NULL COMMENT 'Config key. Use format foo.bar.enabled'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","conf_value","text NOT NULL COMMENT 'Config value in string style.'",1);

-- ----------------------------
-- Table structure for gaea_lock
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_lock","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","tenant","varchar(255) NOT NULL COMMENT 'Lock tenant. Its meaning depends on the implementation of the application layer.'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","name","varchar(255) NOT NULL COMMENT 'Lock name. Its meaning depends on the implementation of the application layer.'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","version","int NOT NULL COMMENT 'Version for optimistic lock'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","json","varchar(1024) NOT NULL COMMENT 'Master content. Its meaning depends on the implementation of the application layer.'",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","status","int NOT NULL",1);

-- ----------------------------
-- Table structure for gaea_master
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_master","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("gaea_master","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("gaea_master","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("gaea_master","tenant","varchar(255) NOT NULL COMMENT 'Master tenant. Its meaning depends on the implementation of the application layer.'",1);
  CALL Uniform_Holoinsight_Column("gaea_master","name","varchar(255) NOT NULL COMMENT 'Master name. Its meaning depends on the implementation of the application layer.'",1);
  CALL Uniform_Holoinsight_Column("gaea_master","version","int NOT NULL COMMENT 'Version for atomic update'",1);
  CALL Uniform_Holoinsight_Column("gaea_master","json","varchar(1024) NOT NULL COMMENT 'Master content. Its meaning depends on the implementation of the application layer.'",1);

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
  CALL Uniform_Holoinsight_Column("hibernate_sequence","next_val","bigint DEFAULT NULL",1);

-- ----------------------------
-- Table structure for integration_generated
-- ----------------------------
  CALL Uniform_Holoinsight_Column("integration_generated","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","product","varchar(100) NOT NULL COMMENT 'Integration product name'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","item","varchar(100) NOT NULL COMMENT 'Monitor item, like portcheck'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","name","varchar(100) NOT NULL COMMENT 'Monitor item name, like app name'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","config","mediumtext COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","deleted","tinyint NOT NULL COMMENT 'Softed delete, when 1 indicates that the configuration has been deleted'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","custom","tinyint NOT NULL COMMENT 'Custom monitor config'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","creator","varchar(100) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","modifier","varchar(100) NOT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("integration_generated","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for integration_plugin
-- ----------------------------
  CALL Uniform_Holoinsight_Column("integration_plugin","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","name","varchar(100) NOT NULL COMMENT 'Plugin name'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","workspace","varchar(100) NOT NULL COMMENT 'Plugin workspace'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","product","varchar(100) NOT NULL COMMENT 'Integration product name'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","type","varchar(200) NOT NULL COMMENT 'Plugin type'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","status","tinyint NOT NULL COMMENT 'Status'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","json","longtext NOT NULL COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","creator","varchar(45) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","modifier","varchar(45) NOT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","collect_range","longtext COMMENT 'Collect meta range'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","template","longtext COMMENT 'Display template'",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","version","varchar(45) DEFAULT NULL COMMENT 'Config version'",1);

-- ----------------------------
-- Table structure for integration_product
-- ----------------------------
  CALL Uniform_Holoinsight_Column("integration_product","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("integration_product","name","varchar(100) NOT NULL COMMENT 'Product name'",1);
  CALL Uniform_Holoinsight_Column("integration_product","profile","longtext COMMENT 'Desprition'",1);
  CALL Uniform_Holoinsight_Column("integration_product","overview","longtext COMMENT 'Product function'",1);
  CALL Uniform_Holoinsight_Column("integration_product","configuration","longtext COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("integration_product","metrics","longtext COMMENT 'Collect metrics'",1);
  CALL Uniform_Holoinsight_Column("integration_product","status","tinyint DEFAULT NULL COMMENT 'Status'",1);
  CALL Uniform_Holoinsight_Column("integration_product","type","varchar(100) DEFAULT NULL COMMENT 'Type'",1);
  CALL Uniform_Holoinsight_Column("integration_product","form","longtext COMMENT 'Front form'",1);
  CALL Uniform_Holoinsight_Column("integration_product","template","longtext COMMENT 'Display template'",1);
  CALL Uniform_Holoinsight_Column("integration_product","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("integration_product","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("integration_product","creator","varchar(45) DEFAULT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("integration_product","modifier","varchar(45) DEFAULT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("integration_product","version","varchar(45) DEFAULT NULL COMMENT 'Product version'",1);

-- ----------------------------
-- Table structure for marketplace_plugin
-- ----------------------------
  CALL Uniform_Holoinsight_Column("marketplace_plugin","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","name","varchar(100) NOT NULL COMMENT 'Plugin name'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","product","varchar(100) NOT NULL COMMENT 'Product name'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","status","tinyint DEFAULT NULL COMMENT 'Status'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","type","varchar(150) DEFAULT NULL COMMENT 'Type'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","data_range","longtext COMMENT 'Data usage range'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","json","longtext COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","creator","varchar(100) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","modifier","varchar(100) NOT NULL COMMENT 'Modified user'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for marketplace_product
-- ----------------------------
  CALL Uniform_Holoinsight_Column("marketplace_product","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","name","varchar(100) NOT NULL COMMENT 'Product name'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","profile","longtext COMMENT 'Product description'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","overview","longtext COMMENT 'Product function'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","configuration","longtext COMMENT 'Config meta'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","price","longtext COMMENT 'Product price list'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","feature","longtext COMMENT 'Product feature list'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","status","tinyint DEFAULT NULL COMMENT 'Status'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","type","varchar(150) DEFAULT NULL COMMENT 'Type'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","creator","varchar(100) NOT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","modifier","varchar(100) NOT NULL COMMENT 'Modified user'",1);

-- ----------------------------
-- Table structure for meta_data
-- ----------------------------
  CALL Uniform_Holoinsight_Column("meta_data","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_uk","varchar(100) NOT NULL COMMENT 'COL uk'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_table","varchar(80) NOT NULL COMMENT 'Relate table name'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_type","varchar(45) NOT NULL COMMENT 'Meta type'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_workspace","varchar(100) DEFAULT NULL COMMENT 'Meta workspace'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_status","varchar(45) DEFAULT NULL COMMENT 'Meta status'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_basic","mediumtext COMMENT 'Basic info meta'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_labels","longtext COMMENT 'Label meta'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_annotations","longtext COMMENT 'Annotations'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Modified time'",1);
  CALL Uniform_Holoinsight_Column("meta_data","_modifier","varchar(100) DEFAULT NULL COMMENT 'Modifier user'",1);

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
  CALL Uniform_Holoinsight_Column("meta_table","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("meta_table","name","varchar(255) NOT NULL COMMENT 'Meta table name'",1);
  CALL Uniform_Holoinsight_Column("meta_table","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("meta_table","table_schema","longtext COMMENT 'Table schema'",1);
  CALL Uniform_Holoinsight_Column("meta_table","status","varchar(40) DEFAULT NULL COMMENT 'Table status'",1);
  CALL Uniform_Holoinsight_Column("meta_table","config","longtext COMMENT 'Table relate config'",1);
  CALL Uniform_Holoinsight_Column("meta_table","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("meta_table","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("meta_table","creator","varchar(45) NOT NULL COMMENT 'Creator user'",1);
  CALL Uniform_Holoinsight_Column("meta_table","modifier","varchar(45) NOT NULL COMMENT 'Modifier user'",1);

-- ----------------------------
-- Table structure for metadata_dictvalue
-- ----------------------------
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","type","varchar(45) NOT NULL COMMENT 'Kv type'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_key","varchar(45) NOT NULL COMMENT 'Key'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_value","mediumtext NOT NULL COMMENT 'Value'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_value_type","varchar(45) DEFAULT NULL COMMENT 'Value type, like string, double'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_desc","varchar(45) DEFAULT NULL COMMENT 'Value description'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","version","int NOT NULL COMMENT 'Kv version, like 1,2,3'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","creator","varchar(45) NOT NULL COMMENT 'Creator user'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","modifier","varchar(45) NOT NULL COMMENT 'Modifier user'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for openmetrics_scraper
-- ----------------------------
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","name","varchar(255) NOT NULL COMMENT 'Config name'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","tenant","varchar(255) NOT NULL COMMENT 'Tenant code'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","conf","text COMMENT 'Tenant conf'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","creator","varchar(45) DEFAULT NULL COMMENT 'Creator user'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","modifier","varchar(45) DEFAULT NULL COMMENT 'Modifier user'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for position_biz_rule
-- ----------------------------
  CALL Uniform_Holoinsight_Column("position_biz_rule","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","app_id","varchar(16) NOT NULL COMMENT 'Relate appid'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","env_id","varchar(16) NOT NULL COMMENT 'Relate envId'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","app_name","varchar(64) NOT NULL COMMENT 'Relate appName'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","interface_type","varchar(16) NOT NULL COMMENT 'Interface type'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","interface_name","varchar(256) NOT NULL COMMENT 'Interface name'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","response_type","varchar(64) NOT NULL COMMENT 'ModelMap indicates acquisition in modelmap, and Return indicates acquisition of return value'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","response_property","varchar(64) DEFAULT NULL COMMENT 'The attribute taken from the modelmap, this field is only required when ModelMap is selected'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","error_code_config","varchar(8192) NOT NULL COMMENT 'Error code'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","global_open","varchar(1) NOT NULL COMMENT 'Whether the rule takes effect globally: T indicates that the rule takes effect globally, and F indicates that the rule does not take effect globally'",1);

-- ----------------------------
-- Table structure for tenant
-- -------------------- --------
  CALL Uniform_Holoinsight_Column("tenant","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("tenant","name","varchar(255) NOT NULL COMMENT 'Tenant name'",1);
  CALL Uniform_Holoinsight_Column("tenant","code","varchar(45) NOT NULL COMMENT 'Tenant code'",1);
  CALL Uniform_Holoinsight_Column("tenant","desc","varchar(500) DEFAULT NULL COMMENT 'Tenant description'",1);
  CALL Uniform_Holoinsight_Column("tenant","json","text COMMENT 'Tenant information'",1);
  CALL Uniform_Holoinsight_Column("tenant","md5","varchar(45) NOT NULL COMMENT 'Tenant md5'",1);
  CALL Uniform_Holoinsight_Column("tenant","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("tenant","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("tenant","product","varchar(255) DEFAULT NULL COMMENT 'Relate product'",1);

-- ----------------------------
-- Table structure for tenant_ops
-- ----------------------------
  CALL Uniform_Holoinsight_Column("tenant_ops","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","tenant","varchar(255) NOT NULL COMMENT 'Tenant code'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","storage","varchar(4096) NOT NULL COMMENT 'Storage configuration'",1);

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
  CALL Uniform_Holoinsight_Column("user_favorite","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","user_login_name","varchar(45) NOT NULL COMMENT 'User login name'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","relate_id","varchar(300) NOT NULL COMMENT 'Favorite relate id'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","type","varchar(50) NOT NULL COMMENT 'Relate type, like log, app, alarm'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","name","varchar(200) NOT NULL COMMENT 'Favorite name'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","url","varchar(1000) NOT NULL COMMENT 'Relate url'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","tenant","varchar(255) NOT NULL COMMENT 'Relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("user_favorite","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for user_oplog
-- ----------------------------
  CALL Uniform_Holoinsight_Column("user_oplog","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","table_name","varchar(200) DEFAULT NULL  COMMENT 'Oplog relate table name'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","table_entity_id","varchar(200) DEFAULT NULL  COMMENT 'Oplog relate table unique id'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","op_type","varchar(45) DEFAULT NULL  COMMENT 'Oplog type, like create,update,delete'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","op_before_context","longtext COMMENT 'Oplog before context json'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","op_after_context","longtext COMMENT 'Oplog after context json'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","name","varchar(200) DEFAULT NULL COMMENT 'Oplog name'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","relate","varchar(1000) DEFAULT NULL COMMENT 'Oplog relate meta'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","tenant","varchar(255) DEFAULT NULL COMMENT 'Oplog relate tenant code'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","creator","varchar(45) DEFAULT NULL COMMENT 'Create user'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("user_oplog","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

-- ----------------------------
-- Table structure for workspace
-- ----------------------------
  CALL Uniform_Holoinsight_Column("workspace","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'Data id'",1);
  CALL Uniform_Holoinsight_Column("workspace","tenant","varchar(255) NOT NULL COMMENT 'Tenant code'",1);
  CALL Uniform_Holoinsight_Column("workspace","name","varchar(100) NOT NULL  COMMENT 'Workspace name'",1);
  CALL Uniform_Holoinsight_Column("workspace","desc","varchar(100) DEFAULT NULL  COMMENT 'Workspace description'",1);
  CALL Uniform_Holoinsight_Column("workspace","config","mediumtext DEFAULT NULL  COMMENT 'Extra config meta'",1);
  CALL Uniform_Holoinsight_Column("workspace","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data creation time'",1);
  CALL Uniform_Holoinsight_Column("workspace","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Data modification time'",1);

DROP PROCEDURE IF EXISTS Uniform_Holoinsight_Column;
