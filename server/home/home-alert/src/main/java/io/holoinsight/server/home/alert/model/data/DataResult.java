/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/10 7:50 下午
 */
@Data
public class DataResult implements Serializable {

    private String metric;

    private Map<String, String> tags;

    private Map<Long, Double> points;

}