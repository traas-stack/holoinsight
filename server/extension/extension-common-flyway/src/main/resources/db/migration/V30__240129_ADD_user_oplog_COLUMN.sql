/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for user_oplog
-- ----------------------------
ALTER TABLE `user_oplog`
    ADD COLUMN `table_entity_uuid` varchar(100) DEFAULT NULL COMMENT 'Oplog relate table unique uuid';