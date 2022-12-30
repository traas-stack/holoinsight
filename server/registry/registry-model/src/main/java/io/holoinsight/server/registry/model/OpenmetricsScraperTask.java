/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import io.holoinsight.server.registry.model.integration.GaeaTask;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@Setter
public class OpenmetricsScraperTask extends GaeaTask {

    private String schema;
    private String metricsPath;
    private String scrapeInterval;
    private String scrapeTimeout;
    private String scrapePort;
    private List<String> targets;
    private Map<String, String> labels;
    private Boolean honorLabels;
    private Boolean honorTimestamps;

}
