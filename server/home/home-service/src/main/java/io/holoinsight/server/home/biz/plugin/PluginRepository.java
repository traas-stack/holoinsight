/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import io.holoinsight.server.home.biz.plugin.model.HostingPlugin;
import io.holoinsight.server.home.biz.plugin.model.Plugin;
import io.holoinsight.server.home.biz.plugin.model.PluginType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 插件仓库，不同类型的插件进行不同处理： 数据源插件（比如日志插件）最终走 gaea_collect_config 链条插件（比如通知链）需要检查关联的插件是否可用，然后落
 * IntegrationPlugin 配置库 通知插件（消息模板、短信通知等）落 IntegrationPlugin 配置库
 *
 * @author masaimu
 * @version 2022-11-02 16:37:00
 */
@Service
public class PluginRepository {

  private Map<String/* plugin name */, Map<String/* version */, Plugin>> pluginTemplates =
      new HashMap<>();
  private Set<String/* plugin name */> datasourcePluginSet = new HashSet<>();
  private Set<String/* plugin name */> hostingPluginSet = new HashSet<>();

  public void registry(Plugin plugin, String pluginName, String version) {
    if (StringUtils.isEmpty(pluginName) || StringUtils.isEmpty(version)) {
      return;
    }
    plugin.setName(pluginName);
    plugin.setVersion(version);
    Map<String/* version */, Plugin> pluginMap =
        this.pluginTemplates.computeIfAbsent(pluginName, k -> new HashMap<>());
    pluginMap.put(version, plugin);
    if (plugin.getPluginType() == PluginType.datasource) {
      datasourcePluginSet.add(pluginName);
    } else if (plugin.getPluginType() == PluginType.hosting) {
      hostingPluginSet.add(pluginName);
    }
  }

  /**
   * 判断是否包含在插件市场
   *
   * @param type
   * @return
   */
  public boolean contains(String type) {
    return this.pluginTemplates.containsKey(type);
  }

  /**
   * 是否为数据源插件，如果是，后续就需要插入 gaea 配置
   * 
   * @param type
   * @return
   */
  public boolean isDataSourcePlugin(String type) {
    return this.datasourcePluginSet.contains(type);
  }

  public boolean isHostingPlugin(String type) {
    return this.hostingPluginSet.contains(type);
  }

  public Plugin getTemplate(String pluginName, String version) {
    Map<String/* version */, Plugin> pluginMap = this.pluginTemplates.get(pluginName);
    if (CollectionUtils.isEmpty(pluginMap)) {
      return null;
    }
    return pluginMap.get(version);
  }

  public Map<String/* plugin name */, Map<String/* version */, HostingPlugin>> getHostingPlugins() {
    if (CollectionUtils.isEmpty(this.hostingPluginSet)) {
      return Collections.emptyMap();
    }
    Map<String/* plugin name */, Map<String/* version */, HostingPlugin>> result = new HashMap<>();
    for (String pluginName : this.hostingPluginSet) {
      Map<String/* version */, Plugin> versionPlugins = this.pluginTemplates.get(pluginName);
      if (CollectionUtils.isEmpty(versionPlugins)) {
        continue;
      }
      Map<String/* version */, HostingPlugin> values = new HashMap<>();
      for (Map.Entry<String/* version */, Plugin> entry : versionPlugins.entrySet()) {
        Plugin plugin = entry.getValue();
        if (plugin instanceof HostingPlugin) {
          values.put(entry.getKey(), (HostingPlugin) plugin);
        }
      }
      result.put(pluginName, values);
    }
    return result;
  }
}
