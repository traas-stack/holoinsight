/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.plugin.model.DefaultHostingAlertPlugin;
import io.holoinsight.server.home.biz.plugin.model.HostingAlert;
import io.holoinsight.server.home.biz.plugin.model.HostingAlertList;
import io.holoinsight.server.home.dal.model.dto.IntegrationFormDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationMetricsDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO.DataRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-03-24 13:55:00
 */
public class IntegrationPluginIT extends BaseIT {

  IntegrationProductDTO product;
  IntegrationPluginDTO plugin;
  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping().create();

  @Order(1)
  @Test
  public void test_integration_product_create() {

    IntegrationProductDTO condition = buildProduct();
    // Create product
    Map<String, Object> data = given() //
        .body(new JSONObject(condition)) //
        .when() //
        .post("/webapi/integration/product/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data"); //
    product = gson.fromJson(gson.toJson(data), IntegrationProductDTO.class);
  }

  @Order(2)
  @Test
  public void test_integration_plugin_create() throws InterruptedException {

    IntegrationPluginDTO condition = buildPlugin(product);
    // Create plugin
    Map<String, Object> data = given() //
        .body(new JSONObject(condition)) //
        .when() //
        .post("/webapi/integration/plugin/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data"); //
    plugin = gson.fromJson(gson.toJson(data), IntegrationPluginDTO.class);

    await("Test alert history generation") //
        .atMost(Duration.ofSeconds(10)) //
        .untilAsserted(() -> {
          AlarmRuleDTO alertCondition = new AlarmRuleDTO();
          alertCondition.setSourceType(new DefaultHostingAlertPlugin().getSourceType());
          MonitorPageRequest<AlarmRuleDTO> pageRequest = new MonitorPageRequest<>();
          pageRequest.setTarget(alertCondition);
          given() //
              .body(new JSONObject(J.toMap(J.toJson(pageRequest)))).log().all() //
              .when() //
              .post("/webapi/alarmRule/pageQuery") //
              .then().log().all() //
              .body("success", IS_TRUE) //
              .body("data.items.size()", gt(0));
        });



  }

  private IntegrationPluginDTO buildPlugin(IntegrationProductDTO product) {
    IntegrationPluginDTO plugin = new IntegrationPluginDTO();
    plugin.setProduct(product.name);
    plugin.setVersion(product.version);
    plugin.setTenant("default");
    plugin.setCreator(product.creator);
    plugin.setType(product.type);
    plugin.setJson("{}");
    plugin.setName(String.join("_", "default", plugin.type, product.getVersion()));
    plugin.setWorkspace("default");

    plugin.setCollectRange(buildDataRange());
    return plugin;
  }

  private Map<String, Object> buildDataRange() {
    DataRange dataRange = new DataRange();
    dataRange.getValuesMap().put("app", "holoinsight-server-example");
    return J.toMap(J.toJson(dataRange));
  }

  private IntegrationProductDTO buildProduct() {
    DefaultHostingAlertPlugin defaultHostingAlertPlugin = new DefaultHostingAlertPlugin();
    defaultHostingAlertPlugin.setName(DefaultHostingAlertPlugin.HOSTING_AI_ALERT);
    String simpleName = defaultHostingAlertPlugin.getSimplePluginName();

    IntegrationProductDTO integrationProductDTO = new IntegrationProductDTO();
    integrationProductDTO.setName(simpleName);
    integrationProductDTO.setProfile("Default智能告警托管");
    integrationProductDTO.setOverview(
        "<p style=\\\"box-sizing: inherit;  border: 0px; font-size: 12px; margin-top: 0px; margin-bottom: 16px; outline: 0px; padding: 0px; vertical-align: initial; color: #FFFFFF; font-family: NotoSans, &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, sans-serif; white-space: normal;\\\">\\n    开启智能告警托管:\\n</p>\\n<ul style=\\\"box-sizing: inherit;  border: 0px; font-size: 12px; margin-bottom: 16px; outline: 0px; padding: 0px 0px 0px 2em; vertical-align: initial; list-style-position: initial; list-style-image: initial; color: #FFFFFF; font-family: NotoSans, &quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, sans-serif; white-space: normal;\\\" class=\\\" list-paddingleft-2\\\">\\n    <li>\\n        <p>\\n            对告警进行布控.\\n        </p>\\n    </li>\\n</ul>");
    integrationProductDTO.setConfiguration(buildConfiguration());
    integrationProductDTO.setTemplate(new HashMap<>());
    integrationProductDTO.setMetrics(new IntegrationMetricsDTO());
    integrationProductDTO.setStatus(true);
    integrationProductDTO.setType(DefaultHostingAlertPlugin.HOSTING_AI_ALERT);
    integrationProductDTO.setForm(new IntegrationFormDTO());
    integrationProductDTO.setVersion("1");
    integrationProductDTO.setCreator("admin");
    return integrationProductDTO;
  }

  private String buildConfiguration() {
    HostingAlertList hostingAlertList = new HostingAlertList();
    hostingAlertList.hostingAlertList = new ArrayList<>();
    HostingAlert a = buildHostingAlert("system_cpu_util", "cpu util ");
    HostingAlert b = buildHostingAlert("system_mem_util", "mem util ");
    hostingAlertList.hostingAlertList.add(a);
    hostingAlertList.hostingAlertList.add(b);
    return J.toJson(hostingAlertList);
  }

  private HostingAlert buildHostingAlert(String metricName, String alias) {
    HostingAlert hostingAlert = new HostingAlert();
    hostingAlert.alertMetric = metricName;
    hostingAlert.alertType = "ai";
    hostingAlert.metricType = "app";
    hostingAlert.alertMetricAlias = alias;
    hostingAlert.alertTitle = "[托管]" + alias + "智能告警";
    hostingAlert.downsample = "1m-avg";
    hostingAlert.aggregator = "avg";
    hostingAlert.groupBy = Arrays.asList("app");
    hostingAlert.functionType = "ValueUp";
    return hostingAlert;
  }
}
