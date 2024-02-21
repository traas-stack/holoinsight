/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.gateway.core.prometheus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xerial.snappy.Snappy;

import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import prometheus.Prometheus;
import prometheus.PrometheusTypes;

/**
 * @author sw1136562366
 */
@RestController
@RequestMapping("/api/v1/prometheus")
public class PrometheusController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusController.class);
  @Autowired
  private MetricStorage metricStorage;

  @Autowired
  private ApikeyAuthService authService;

  /**
   * Before prometheus 2.25.0-rc.0, url: http://xxxxxxx:8080/api/v1/prometheus/write?apikey=xxxxx
   *
   * After prometheus 2.25.0-rc.0, headers: apikey: xxxxx,
   * url:http://xxxxxxx:8080/api/v1/prometheus/write
   *
   */
  @PostMapping("/write")
  public void write(HttpServletRequest request) throws IOException {
    try {
      String apikey = request.getHeader("apikey");

      if (StringUtils.isBlank(apikey)) {
        apikey = request.getParameter("apikey");
      }
      if (StringUtils.isBlank(apikey)) {
        LOGGER.warn("prometheus remote write apikey is not set!");
        return;
      }

      byte[] body = IOUtils.toByteArray(request.getInputStream());
      Prometheus.WriteRequest writeRequest =
          Prometheus.WriteRequest.parseFrom(Snappy.uncompress(body));

      authService.get(apikey, true).flatMap(authInfo -> {
        WriteMetricsParam param = convertToWriteMetricsParam(authInfo, writeRequest);
        return metricStorage.write(param);
      }).subscribe(null, error -> {
        LOGGER.error("write error", error);
      });
    } catch (Exception e) {
      LOGGER.error("prometheus remote write error: ", e);
    }
  }

  private WriteMetricsParam convertToWriteMetricsParam(AuthInfo authInfo,
      Prometheus.WriteRequest writeRequest) {
    List<WriteMetricsParam.Point> points = new ArrayList<>(writeRequest.getTimeseriesList().size());

    for (PrometheusTypes.TimeSeries timeSeries : writeRequest.getTimeseriesList()) {
      String metric = "";
      Map<String, String> tags = new HashMap<>();

      for (PrometheusTypes.Label label : timeSeries.getLabelsList()) {
        String tagValue = label.getValue();
        if (label.getName().equals("__name__")) {
          metric = fixName(tagValue);
        } else {
          tags.put(label.getName(), tagValue);
        }
      }

      if (StringUtils.isBlank(metric)) {
        continue;
      }

      for (PrometheusTypes.Sample sample : timeSeries.getSamplesList()) {
        WriteMetricsParam.Point wmpp = new WriteMetricsParam.Point();
        wmpp.setMetricName(metric);
        wmpp.setTimeStamp(sample.getTimestamp());
        wmpp.setValue(sample.getValue());
        wmpp.setTags(tags);
        points.add(wmpp);
      }
    }

    WriteMetricsParam param = new WriteMetricsParam();
    param.setTenant(authInfo.getTenant());
    param.setPoints(points);
    return param;
  }

  private static String fixName(String name) {
    return name.replace('.', '_');
  }

}
