package io.holoinsight.server.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorInstanceCfg {
  Map<String, BigDecimal> freeQuota;
  Map<String, Boolean> ctlStatus;
}
