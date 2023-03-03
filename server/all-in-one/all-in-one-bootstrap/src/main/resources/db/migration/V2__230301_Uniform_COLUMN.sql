
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
-- Table structure for account
-- ----------------------------
  CALL Uniform_Holoinsight_Column("account","account_id","varchar(255) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("account","balance","bigint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("account","gmt_create","date DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("account","gmt_modified","date DEFAULT NULL",1);

-- ----------------------------
-- Table structure for agent_configuration
-- ----------------------------
  CALL Uniform_Holoinsight_Column("agent_configuration","tenant","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","service","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","app_id","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","env_id","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","value","longtext NOT NULL",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("agent_configuration","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for alarm_block
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_block","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","creator","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","modifier","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","start_time","timestamp NULL DEFAULT NULL COMMENT '开始时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","end_time","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","tags","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '屏蔽维度'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","unique_id","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '-1' COMMENT '告警id'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","reason","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '原因'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","extra","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","hour","tinyint DEFAULT '0' COMMENT '屏蔽小时'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","minute","tinyint DEFAULT '0' COMMENT '屏蔽分钟'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","source_type","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '来源类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_block","source_id","bigint DEFAULT NULL COMMENT '来源id'",1);

-- ----------------------------
-- Table structure for alarm_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_config","id","bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","creator","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","modifier","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","config_key","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置项'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","config_value","varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '具体值'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","config_type","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '值类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_config","component","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'default' COMMENT '组件区分'",1);

-- ----------------------------
-- Table structure for alarm_ding_ding_robot
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","creator","varchar(64) DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","modifier","varchar(64) DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","group_name","varchar(64) NOT NULL COMMENT '群名称'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","robot_url","varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '机器人url'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","extra","longtext COMMENT '额外信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_ding_ding_robot","tenant","varchar(64) DEFAULT NULL COMMENT '租户id'",1);

-- ----------------------------
-- Table structure for alarm_group
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_group","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","creator","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","modifier","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","group_name","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警组名称'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","group_info","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '告警组信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","sms_phone","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '告警短信号码'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","dyvms_phone","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '告警电话号码'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","dd_webhook","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '钉钉机器人'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","email_address","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '邮件地址'",1);
  CALL Uniform_Holoinsight_Column("alarm_group","env_type","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '环境类型'",1);

-- ----------------------------
-- Table structure for alarm_history
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_history","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","alarm_time","timestamp NULL DEFAULT NULL COMMENT '告警时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","recover_time","timestamp NULL DEFAULT NULL COMMENT '恢复时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","duration","bigint DEFAULT NULL COMMENT '持续时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","unique_id","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '-1' COMMENT '告警id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","rule_name","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","alarm_level","varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警级别'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","trigger_content","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '触发详情'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","extra","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","source_type","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '来源类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","source_id","bigint DEFAULT NULL COMMENT '来源id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history","env_type","varchar(255) DEFAULT NULL",1);

-- ----------------------------
-- Table structure for alarm_history_detail
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_history_detail","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","alarm_time","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '告警时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","unique_id","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '-1' COMMENT '告警id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","history_id","bigint NOT NULL DEFAULT '-1' COMMENT '告警历史id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","tags","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '报警维度'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","alarm_content","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '触发方式简述'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","datasource","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '数据源信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","extra","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","source_type","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '来源类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","source_id","bigint DEFAULT NULL COMMENT '来源id'",1);
  CALL Uniform_Holoinsight_Column("alarm_history_detail","env_type","varchar(255) DEFAULT NULL",1);

-- ----------------------------
-- Table structure for alarm_rule
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_rule","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule_name","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule_type","varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则类型（AI、RULE）'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","creator","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","modifier","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","alarm_level","varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警级别'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule_describe","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '规则描述'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","rule","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '告警规则'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","pql","varchar(512) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","time_filter","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生效时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","status","tinyint NOT NULL DEFAULT '1' COMMENT '规则是否生效'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","is_merge","tinyint NOT NULL DEFAULT '1' COMMENT '合并是否开启'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","merge_type","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '合并方式'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","recover","tinyint NOT NULL DEFAULT '1' COMMENT '恢复通知是否开启'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","notice_type","varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通知方式'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","extra","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","alarm_content","varchar(255) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","source_type","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '来源类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","source_id","bigint DEFAULT NULL COMMENT '来源id'",1);
  CALL Uniform_Holoinsight_Column("alarm_rule","env_type","varchar(255) DEFAULT NULL",1);

-- ----------------------------
-- Table structure for alarm_subscribe
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_subscribe","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","creator","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","modifier","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","subscriber","varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '订阅者'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","group_id","bigint NOT NULL DEFAULT '-1' COMMENT '订阅组id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","unique_id","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '-1' COMMENT '告警id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","notice_type","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通知方式'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","status","tinyint NOT NULL DEFAULT '1' COMMENT '通知是否生效'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","source_type","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '来源类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","source_id","bigint DEFAULT NULL COMMENT '来源id'",1);
  CALL Uniform_Holoinsight_Column("alarm_subscribe","env_type","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '环境类型'",1);

-- ----------------------------
-- Table structure for alarm_webhook
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alarm_webhook","id","bigint NOT NULL AUTO_INCREMENT COMMENT 'id'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","gmt_modified","timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","creator","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","modifier","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '修改者'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","webhook_name","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回调名称'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","status","tinyint NOT NULL DEFAULT '1' COMMENT '回调状态'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_type","varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方式'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_url","varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求地址'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_headers","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '请求头'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","request_body","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '请求体'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","type","tinyint NOT NULL DEFAULT '2' COMMENT '回调类型'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","extra","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '额外信息'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","role","varchar(200) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '租户id'",1);
  CALL Uniform_Holoinsight_Column("alarm_webhook","webhook_test","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '调试数据'",1);

-- ----------------------------
-- Table structure for alertmanager_webhook
-- ----------------------------
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","tenant","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","gmt_create","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("alertmanager_webhook","gmt_modified","datetime DEFAULT NULL",1);

-- ----------------------------
-- Table structure for apikey
-- ----------------------------
  CALL Uniform_Holoinsight_Column("apikey","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("apikey","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("apikey","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("apikey","name","varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","apikey","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","creator","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","modifier","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","role","varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","status","tinyint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("apikey","access_config","mediumtext COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("apikey","desc","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for cluster
-- ----------------------------
  CALL Uniform_Holoinsight_Column("cluster","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("cluster","ip","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster","hostname","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster","role","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster","last_heartbeat_time","bigint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster","manual_close","tinyint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster","gmt_modified","datetime DEFAULT NULL",1);

-- ----------------------------
-- Table structure for cluster_task
-- ----------------------------
  CALL Uniform_Holoinsight_Column("cluster_task","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("cluster_task","task_id","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster_task","period","bigint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster_task","status","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster_task","cluster_ip","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster_task","context","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("cluster_task","result","varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster_task","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("cluster_task","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for custom_plugin
-- ----------------------------
  CALL Uniform_Holoinsight_Column("custom_plugin","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","tenant","varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","parent_folder_id","bigint NOT NULL DEFAULT '-1'",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","name","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","plugin_type","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","status","varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","period_type","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","conf","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","sample_log","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","gmt_modified","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("custom_plugin","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);

-- ----------------------------
-- Table structure for dashboard
-- ----------------------------
  CALL Uniform_Holoinsight_Column("dashboard","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("dashboard","gmt_create","datetime NOT NULL DEFAULT CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("dashboard","gmt_modified","datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("dashboard","title","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("dashboard","conf","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置信息'",1);
  CALL Uniform_Holoinsight_Column("dashboard","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("dashboard","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("dashboard","tenant","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("dashboard","type","varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for display_menu
-- ----------------------------
  CALL Uniform_Holoinsight_Column("display_menu","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("display_menu","ref_id","bigint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","type","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","config","mediumtext NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","tenant","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","creator","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","modifier","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_menu","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for display_template
-- ----------------------------
  CALL Uniform_Holoinsight_Column("display_template","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("display_template","name","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","ref_id","bigint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","type","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","config","mediumtext NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","tenant","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","creator","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","modifier","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("display_template","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for folder
-- ----------------------------
  CALL Uniform_Holoinsight_Column("folder","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("folder","name","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","parent_folder_id","bigint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","tenant","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","creator","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","modifier","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","gmt_create","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","gmt_modified","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("folder","ext_info","varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for gaea_agent
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_agent","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","agent_id","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","json","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_agent","status","int NOT NULL",1);

-- ----------------------------
-- Table structure for gaea_cluster_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","group","varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_cluster_config","json","varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);

-- ----------------------------
-- Table structure for gaea_collect_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_collect_config","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","table_name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '跟主站类似的表名'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","json","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置内容, 是一个大json'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","deleted","int NOT NULL COMMENT '标记删除'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","version","bigint NOT NULL COMMENT '对于同一个table_name的配置来说是一个递增的版本号'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","collect_range","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '采集目标的dim表达式'",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","executor_selector","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","ref_id","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","biz_tenant","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_collect_config","type","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for gaea_config
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_config","id","bigint NOT NULL AUTO_INCREMENT COMMENT '主键'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","tenant","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'registry自己内部的租户, 非业务租户'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","conf_key","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置key'",1);
  CALL Uniform_Holoinsight_Column("gaea_config","conf_value","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置value'",1);

-- ----------------------------
-- Table structure for gaea_lock
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_lock","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","tenant","varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","version","int NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","json","varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_lock","status","int NOT NULL",1);

-- ----------------------------
-- Table structure for gaea_master
-- ----------------------------
  CALL Uniform_Holoinsight_Column("gaea_master","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("gaea_master","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_master","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("gaea_master","tenant","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_master","name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_master","version","int NOT NULL",1);
  CALL Uniform_Holoinsight_Column("gaea_master","json","varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
  CALL Uniform_Holoinsight_Column("hibernate_sequence","next_val","bigint DEFAULT NULL",1);

-- ----------------------------
-- Table structure for integration_generated
-- ----------------------------
  CALL Uniform_Holoinsight_Column("integration_generated","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("integration_generated","tenant","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","product","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","item","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","name","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","config","mediumtext",1);
  CALL Uniform_Holoinsight_Column("integration_generated","deleted","tinyint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","custom","tinyint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","creator","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","modifier","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_generated","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for integration_plugin
-- ----------------------------
  CALL Uniform_Holoinsight_Column("integration_plugin","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","tenant","varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","name","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","product","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","type","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","status","tinyint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","json","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","gmt_modified","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","collect_range","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","template","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_plugin","version","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '版本'",1);

-- ----------------------------
-- Table structure for integration_product
-- ----------------------------
  CALL Uniform_Holoinsight_Column("integration_product","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("integration_product","name","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","profile","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_product","overview","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_product","configuration","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_product","metrics","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_product","status","tinyint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","type","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","form","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_product","template","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("integration_product","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","gmt_modified","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("integration_product","version","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '版本'",1);

-- ----------------------------
-- Table structure for marketplace_plugin
-- ----------------------------
  CALL Uniform_Holoinsight_Column("marketplace_plugin","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","tenant","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","name","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","product","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","status","tinyint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","type","varchar(150) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","data_range","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","json","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","creator","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","modifier","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_plugin","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for marketplace_product
-- ----------------------------
  CALL Uniform_Holoinsight_Column("marketplace_product","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","name","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","profile","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","overview","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","configuration","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","price","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","feature","longtext",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","status","tinyint DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","type","varchar(150) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","gmt_modified","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","creator","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("marketplace_product","modifier","varchar(100) NOT NULL",1);

-- ----------------------------
-- Table structure for meta_data
-- ----------------------------
  CALL Uniform_Holoinsight_Column("meta_data","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("meta_data","_uk","varchar(100) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_data","_table","varchar(80) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_data","_type","varchar(45) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_data","_workspace","varchar(100) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_data","_status","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_data","_basic","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("meta_data","_labels","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("meta_data","_annotations","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("meta_data","_modified","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_data","_modifier","varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
  CALL Uniform_Holoinsight_Column("meta_table","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("meta_table","name","varchar(255) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_table","tenant","varchar(255) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_table","table_schema","longtext",1);
  CALL Uniform_Holoinsight_Column("meta_table","status","varchar(40) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_table","config","longtext",1);
  CALL Uniform_Holoinsight_Column("meta_table","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("meta_table","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",1);
  CALL Uniform_Holoinsight_Column("meta_table","creator","varchar(45) NOT NULL",1);
  CALL Uniform_Holoinsight_Column("meta_table","modifier","varchar(45) NOT NULL",1);

-- ----------------------------
-- Table structure for metadata_dictvalue
-- ----------------------------
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","type","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_key","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_value","mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_value_type","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","dict_desc","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","version","int NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metadata_dictvalue","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for metric_info
-- ----------------------------
  CALL Uniform_Holoinsight_Column("metric_info","id","bigint NOT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","gmt_create","datetime(6) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","gmt_modified","datetime(6) DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","metric_type","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","metrics","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","ref_id","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","tags","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("metric_info","tenant","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for openmetrics_scraper
-- ----------------------------
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","tenant","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","conf","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","modifier","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","gmt_create","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("openmetrics_scraper","gmt_modified","datetime DEFAULT NULL",1);

-- ----------------------------
-- Table structure for position_biz_rule
-- ----------------------------
  CALL Uniform_Holoinsight_Column("position_biz_rule","id","bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","app_id","varchar(16) NOT NULL COMMENT 'appid'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","env_id","varchar(16) NOT NULL COMMENT 'envid'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","app_name","varchar(64) NOT NULL COMMENT '服务名'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","interface_type","varchar(16) NOT NULL COMMENT '服务类型'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","interface_name","varchar(256) NOT NULL COMMENT '服务接口'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","response_type","varchar(64) NOT NULL COMMENT '返回值类型，ModelMap表示modelmap中获取，Return表示返回值获取'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","response_property","varchar(64) DEFAULT NULL COMMENT '从modelmap里取的属性，只有选择ModelMap才需要该字段'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","error_code_config","varchar(8192) NOT NULL COMMENT '错误码相关配置'",1);
  CALL Uniform_Holoinsight_Column("position_biz_rule","global_open","varchar(1) NOT NULL COMMENT '规则是否全局生效，T代表全局生效，F代表全局不生效'",1);

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
  CALL Uniform_Holoinsight_Column("tenant","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("tenant","name","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("tenant","code","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("tenant","desc","varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("tenant","json","text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '租户的基本信息, 作为一个json大对象'",1);
  CALL Uniform_Holoinsight_Column("tenant","md5","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("tenant","gmt_create","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("tenant","gmt_modified","datetime DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("tenant","product","varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);

-- ----------------------------
-- Table structure for tenant_ops
-- ----------------------------
  CALL Uniform_Holoinsight_Column("tenant_ops","id","bigint NOT NULL AUTO_INCREMENT COMMENT '主键'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","gmt_create","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","gmt_modified","timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","tenant","varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户名'",1);
  CALL Uniform_Holoinsight_Column("tenant_ops","storage","varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储配置'",1);

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
  CALL Uniform_Holoinsight_Column("user_favorite","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("user_favorite","user_login_name","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","relate_id","varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","type","varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","name","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","url","varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","tenant","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","gmt_create","datetime NOT NULL",1);
  CALL Uniform_Holoinsight_Column("user_favorite","gmt_modified","datetime NOT NULL",1);

-- ----------------------------
-- Table structure for user_oplog
-- ----------------------------
  CALL Uniform_Holoinsight_Column("user_oplog","id","bigint NOT NULL AUTO_INCREMENT",1);
  CALL Uniform_Holoinsight_Column("user_oplog","table_name","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","table_entity_id","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","op_type","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","op_before_context","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("user_oplog","op_after_context","longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",1);
  CALL Uniform_Holoinsight_Column("user_oplog","name","varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","relate","varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","tenant","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","creator","varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL",1);
  CALL Uniform_Holoinsight_Column("user_oplog","gmt_create","datetime DEFAULT NULL",1);

DROP PROCEDURE IF EXISTS Uniform_Holoinsight_Column;
