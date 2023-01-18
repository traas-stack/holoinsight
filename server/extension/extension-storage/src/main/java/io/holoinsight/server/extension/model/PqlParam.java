package io.holoinsight.server.extension.model;

import lombok.Data;

/**
 * @author jinyan.ljw
 * @Description pql Query parameters
 * @date 2023/1/1
 */
@Data
public class PqlParam {
  String tenant;
  String query;
  long time;
  long start;
  long end;
  long step;
  String timeout;
  String delta;
}
