/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
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

CALL Uniform_Holoinsight_Column("alarm_block","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("alarm_history","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("alarm_history_detail","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("alarm_subscribe","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("custom_plugin","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("dashboard","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("folder","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("integration_generated","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("openmetrics_scraper","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("user_favorite","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("user_oplog","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);
CALL Uniform_Holoinsight_Column("gaea_collect_config","workspace","VARCHAR(150) NULL DEFAULT NULL COMMENT 'workspace' AFTER `tenant`",1);

DROP PROCEDURE IF EXISTS Uniform_Holoinsight_Column;