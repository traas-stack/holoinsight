/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

-- ----------------------------
-- Table structure for alert_notify_record
-- ----------------------------
ALTER TABLE `alert_notify_record`
    ADD INDEX `idx_sus_chanel_gmt` (`is_success` ASC, `notify_channel` ASC, `gmt_create` ASC) VISIBLE;
;
