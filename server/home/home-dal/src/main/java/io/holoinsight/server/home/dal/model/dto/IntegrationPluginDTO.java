/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.common.MD5Hash;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version 1.0: IntegrationInstDTO.java, v 0.1 2022年06月08日 8:04 下午 wanpeng.xwp Exp $
 */
@Data
public class IntegrationPluginDTO {
    public Long                id;

    public String              tenant;

    public String              name;

    public String              product;

    public String              type;

    /**
     * 采集范围
     */
    public GaeaCollectRange    collectRange;

    public Map<String, Object> template;

    public String              json;

    public Boolean             status;

    public String              creator;

    public String              modifier;

    public Date                gmtCreate;

    public Date                gmtModified;

    public String              version;

    public boolean checkVersion(IntegrationPluginDTO originalRecord) {
        return StringUtils.isNotEmpty(version) && !version.equals(originalRecord.getVersion());
    }

    public boolean checkJsonChange(IntegrationPluginDTO originalRecord) {
        if((originalRecord == null)
                ||(this.json == null && originalRecord.json != null)
                ||(this.json != null && originalRecord.json == null)){
            return true;
        }
        return !MD5Hash.getMD5(this.json).equalsIgnoreCase(MD5Hash.getMD5(originalRecord.json));
    }

    public boolean checkCollectRange(IntegrationPluginDTO originalRecord) {
        if((originalRecord == null)
                ||(this.collectRange == null && originalRecord.collectRange != null)
                ||(this.collectRange != null && originalRecord.collectRange == null)){
            return true;
        }
        return !this.collectRange.isEqual(originalRecord.collectRange);
    }

    public boolean checkCollectConfig(IntegrationPluginDTO originalRecord) {
        return false;
    }
}