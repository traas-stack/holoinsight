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

CALL Uniform_Holoinsight_Column("dashboard","conf","COLUMN `conf` MEDIUMTEXT",2);

DROP PROCEDURE IF EXISTS Uniform_Holoinsight_Column;