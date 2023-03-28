/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.registry.model.integration.GaeaTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaCollectConfigDTO.java, v 0.1 2022年03月31日 8:46 下午 jinsong.yjs Exp $
 */
@Data
public class GaeaCollectConfigDTO {

  public Long id;

  public String refId;

  public String tenant;
  public String workspace;

  public String tableName;

  public Boolean deleted;
  public Long version;

  public String type;
  public GaeaTask json;

  public GaeaCollectRange collectRange;

  public Map<String, Object> executorSelector;

  public Date gmtCreate;

  public Date gmtModified;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GaeaCollectRange extends CollectRange {
    public String type;
    public CloudMonitorRange cloudmonitor;

    public boolean isEqual(GaeaCollectRange originalRecord) {
      if ((originalRecord == null)
          || (this.cloudmonitor == null && originalRecord.cloudmonitor != null)
          || (this.cloudmonitor != null && originalRecord.cloudmonitor == null)) {
        return false;
      }
      return this.cloudmonitor.isEqual(originalRecord.cloudmonitor);
    }
  }

  public static abstract class CollectRange implements Serializable {
    private static final long serialVersionUID = -2140563386879600142L;

  }

}
