/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.flyway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jiwliu
 * @date 2023/3/1
 */
@ConfigurationProperties(prefix = "holoinsight.flyway")
public class FlywayProperties {
  /**
   * Whether to enable flyway.
   */
  private boolean enabled = false;

  /**
   * Whether to disable clean.
   */
  private boolean cleanDisabled = true;
  /**
   * Locations of migrations scripts. Can contain the special "{vendor}" placeholder to use
   * vendor-specific locations.
   */
  private List<String> locations =
      new ArrayList<>(Collections.singletonList("classpath:db/migration"));

  /**
   * Version to tag an existing schema with when executing baseline.
   */
  private String baselineVersion = "0";

  /**
   * Whether to automatically call baseline when migrating a non-empty schema.
   */
  private boolean baselineOnMigrate = true;

  /**
   * Perform placeholder replacement in migration scripts.
   */
  private boolean placeholderReplacement = false;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isCleanDisabled() {
    return cleanDisabled;
  }

  public void setCleanDisabled(boolean cleanDisabled) {
    this.cleanDisabled = cleanDisabled;
  }

  public List<String> getLocations() {
    return locations;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public String getBaselineVersion() {
    return baselineVersion;
  }

  public void setBaselineVersion(String baselineVersion) {
    this.baselineVersion = baselineVersion;
  }

  public boolean isBaselineOnMigrate() {
    return baselineOnMigrate;
  }

  public void setBaselineOnMigrate(boolean baselineOnMigrate) {
    this.baselineOnMigrate = baselineOnMigrate;
  }

  public boolean isPlaceholderReplacement() {
    return placeholderReplacement;
  }

  public void setPlaceholderReplacement(boolean placeholderReplacement) {
    this.placeholderReplacement = placeholderReplacement;
  }
}
