/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

/**
 *
 * @author xiangwanpeng
 * @version : IntegrationProductDTO.java, v 0.1 2022年06月08日 16:41 wanpeng.xwp Exp $
 */
@Data
public class IntegrationMetricDTO {
    private String name;
    private String unit;
    private String desc;
    private String format;
    private String aggregator;
}