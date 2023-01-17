package io.holoinsight.server.extension.model;

import lombok.Data;

/**
 * @author jinyan.ljw
 * @date 2023/1/11
 */
@Data
public class QueryMetricsParam {

  String tenant;
  String name;
  Integer limit;
}
