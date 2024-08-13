/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

ALTER TABLE `meta_dim_data` ADD KEY idx_deleted_gmtmodified(`deleted`, `gmt_modified`);
ALTER TABLE `metric_info` ADD KEY idx_deleted_metrictype(`deleted`, `metric_type`);