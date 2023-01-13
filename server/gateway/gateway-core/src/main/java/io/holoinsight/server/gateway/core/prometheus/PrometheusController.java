/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.gateway.core.prometheus;

import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.gateway.core.storage.MetricStorage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xerial.snappy.Snappy;
import prometheus.Prometheus;
import prometheus.PrometheusTypes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author sw1136562366
 */
@RestController
@RequestMapping("/api/v1/prometheus")
public class PrometheusController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusController.class);
  @Autowired
  private MetricStorage storage;

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

      InputStream is = request.getInputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = is.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      buffer.flush();
      Prometheus.WriteRequest writeRequest =
          Prometheus.WriteRequest.parseFrom(Snappy.uncompress(buffer.toByteArray()));

      authService.get(apikey, true).flatMap(authInfo -> {
        return storage.write(authInfo, writeRequest);
      }).subscribe(null, error -> {
        LOGGER.error("write error", error);
      });
    } catch (Exception e) {
      LOGGER.error("prometheus remote write error: ", e);
    }
  }

  /**
   *
   * @param request
   * @throws IOException
   * @deprecated There is not read api now.
   */
  // @PostMapping("/read")
  @Deprecated
  public void read(HttpServletRequest request) throws IOException {
    Prometheus.ReadRequest readRequest = Prometheus.ReadRequest.parseFrom(request.getInputStream());

    for (Prometheus.Query query : readRequest.getQueriesList()) {
      String metric = "";

      for (PrometheusTypes.LabelMatcher labelMatcher : query.getMatchersList()) {
        if (labelMatcher.getName().equals("__name__")) {
          metric = labelMatcher.getValue();
        }
      }
    }
  }

}
