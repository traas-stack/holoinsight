/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.installer;

import org.elasticsearch.common.settings.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author jiwliu
 * @version : IndexStructures.java, v 0.1 2022年10月12日 10:20 xiangwanpeng Exp $
 */
public class IndexStructures {

  private final Map<String, Mappings> structures;

  private final Map<String, Settings> settingsMap;

  public IndexStructures() {
    this.structures = new HashMap<>();
    this.settingsMap = new HashMap<>();
  }

  public Mappings getMapping(String tableName) {
    Map<String, Object> properties =
        structures.containsKey(tableName) ? structures.get(tableName).getProperties()
            : new HashMap<>();
    return Mappings.builder().properties(properties).build();
  }

  public Settings getSettings(String tableName) {
    return settingsMap.getOrDefault(tableName, Settings.EMPTY);
  }

  /**
   * Add or append field when the current structures don't contain the input structure or having new
   * fields in it.
   */
  public void putStructure(String tableName, Mappings mapping) {
    if (Objects.isNull(mapping) || Objects.isNull(mapping.getProperties())
        || mapping.getProperties().isEmpty()) {
      return;
    }
    if (structures.containsKey(tableName)) {
      structures.get(tableName).appendNewFields(mapping);
    } else {
      structures.put(tableName, mapping);
    }
  }

  public void putSettings(String tableName, Settings settings) {
    if (Objects.isNull(settings) || settings.isEmpty()) {
      return;
    }
    settingsMap.put(tableName, settings);
  }

  /**
   * Returns mappings with fields that not exist in the input mappings. The input mappings should be
   * history mapping from current index. Do not return _source config to avoid current index update
   * conflict.
   */
  public Mappings diffStructure(String tableName, Mappings mappings) {
    if (!structures.containsKey(tableName)) {
      return new Mappings();
    }
    mappings.getProperties();
    Map<String, Object> diffProperties = structures.get(tableName).diffFields(mappings);
    return Mappings.builder().properties(diffProperties).build();
  }

  /**
   * Returns true when the current structures already contains the properties of the input mappings.
   */
  public boolean containsStructure(String tableName, Mappings mappings) {
    if (Objects.isNull(mappings) || Objects.isNull(mappings.getProperties())
        || mappings.getProperties().isEmpty()) {
      return true;
    }
    return structures.containsKey(tableName)
        && structures.get(tableName).containsAllFields(mappings);
  }

}
