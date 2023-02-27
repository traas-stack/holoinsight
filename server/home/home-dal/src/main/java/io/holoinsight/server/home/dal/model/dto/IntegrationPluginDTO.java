/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.common.MD5Hash;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version 1.0: IntegrationInstDTO.java, v 0.1 2022年06月08日 8:04 下午 xiangwanpeng Exp $
 */
@Data
public class IntegrationPluginDTO {
  public Long id;

  public String tenant;

  public String workspace;

  public String name;

  public String product;

  public String type;

  /**
   * 采集范围
   */
  public Map<String, Object> collectRange;

  public Map<String, Object> template;

  public String json;

  public Boolean status;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  public String version;

  public boolean checkVersion(IntegrationPluginDTO originalRecord) {
    return StringUtils.isNotEmpty(version) && !version.equals(originalRecord.getVersion());
  }

  public boolean checkJsonChange(IntegrationPluginDTO originalRecord) {
    if ((originalRecord == null) || (this.json == null && originalRecord.json != null)
        || (this.json != null && originalRecord.json == null)) {
      return true;
    }
    return !MD5Hash.getMD5(this.json).equalsIgnoreCase(MD5Hash.getMD5(originalRecord.json));
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataRange extends GaeaCollectConfigDTO.CollectRange {
    Map<String, String> valuesMap = new HashMap<>();

    public String getTenant(boolean isCloudRun) {
      String key = "tenant";
      if (isCloudRun) {
        key = "appId";
      }
      return get(key);
    }

    public String getWorkspace(boolean isCloudRun) {
      String key = "workspace";
      if (isCloudRun) {
        key = "envId";
      }
      return get(key);
    }

    public String get(String key) {
      return valuesMap.get(key);
    }
  }

}
