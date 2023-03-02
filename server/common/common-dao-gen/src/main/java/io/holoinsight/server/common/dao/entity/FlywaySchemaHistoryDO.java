/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FlywaySchemaHistoryDO {
  private Integer installedRank;

  private String version;

  private String description;

  private String type;

  private String script;

  private Integer checksum;

  private String installedBy;

  private Date installedOn;

  private Integer executionTime;

  private Boolean success;

  public Integer getInstalledRank() {
    return installedRank;
  }

  public void setInstalledRank(Integer installedRank) {
    this.installedRank = installedRank;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version == null ? null : version.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type == null ? null : type.trim();
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script == null ? null : script.trim();
  }

  public Integer getChecksum() {
    return checksum;
  }

  public void setChecksum(Integer checksum) {
    this.checksum = checksum;
  }

  public String getInstalledBy() {
    return installedBy;
  }

  public void setInstalledBy(String installedBy) {
    this.installedBy = installedBy == null ? null : installedBy.trim();
  }

  public Date getInstalledOn() {
    return installedOn;
  }

  public void setInstalledOn(Date installedOn) {
    this.installedOn = installedOn;
  }

  public Integer getExecutionTime() {
    return executionTime;
  }

  public void setExecutionTime(Integer executionTime) {
    this.executionTime = executionTime;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", installedRank=").append(installedRank);
    sb.append(", version=").append(version);
    sb.append(", description=").append(description);
    sb.append(", type=").append(type);
    sb.append(", script=").append(script);
    sb.append(", checksum=").append(checksum);
    sb.append(", installedBy=").append(installedBy);
    sb.append(", installedOn=").append(installedOn);
    sb.append(", executionTime=").append(executionTime);
    sb.append(", success=").append(success);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public boolean equals(Object that) {
    if (this == that) {
      return true;
    }
    if (that == null) {
      return false;
    }
    if (getClass() != that.getClass()) {
      return false;
    }
    FlywaySchemaHistoryDO other = (FlywaySchemaHistoryDO) that;
    return (this.getInstalledRank() == null ? other.getInstalledRank() == null
        : this.getInstalledRank().equals(other.getInstalledRank()))
        && (this.getVersion() == null ? other.getVersion() == null
            : this.getVersion().equals(other.getVersion()))
        && (this.getDescription() == null ? other.getDescription() == null
            : this.getDescription().equals(other.getDescription()))
        && (this.getType() == null ? other.getType() == null
            : this.getType().equals(other.getType()))
        && (this.getScript() == null ? other.getScript() == null
            : this.getScript().equals(other.getScript()))
        && (this.getChecksum() == null ? other.getChecksum() == null
            : this.getChecksum().equals(other.getChecksum()))
        && (this.getInstalledBy() == null ? other.getInstalledBy() == null
            : this.getInstalledBy().equals(other.getInstalledBy()))
        && (this.getInstalledOn() == null ? other.getInstalledOn() == null
            : this.getInstalledOn().equals(other.getInstalledOn()))
        && (this.getExecutionTime() == null ? other.getExecutionTime() == null
            : this.getExecutionTime().equals(other.getExecutionTime()))
        && (this.getSuccess() == null ? other.getSuccess() == null
            : this.getSuccess().equals(other.getSuccess()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getInstalledRank() == null) ? 0 : getInstalledRank().hashCode());
    result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
    result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
    result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
    result = prime * result + ((getScript() == null) ? 0 : getScript().hashCode());
    result = prime * result + ((getChecksum() == null) ? 0 : getChecksum().hashCode());
    result = prime * result + ((getInstalledBy() == null) ? 0 : getInstalledBy().hashCode());
    result = prime * result + ((getInstalledOn() == null) ? 0 : getInstalledOn().hashCode());
    result = prime * result + ((getExecutionTime() == null) ? 0 : getExecutionTime().hashCode());
    result = prime * result + ((getSuccess() == null) ? 0 : getSuccess().hashCode());
    return result;
  }

  public static FlywaySchemaHistoryDO.Builder builder() {
    return new FlywaySchemaHistoryDO.Builder();
  }

  public static class Builder {
    private FlywaySchemaHistoryDO obj;

    public Builder() {
      this.obj = new FlywaySchemaHistoryDO();
    }

    public Builder installedRank(Integer installedRank) {
      obj.setInstalledRank(installedRank);
      return this;
    }

    public Builder version(String version) {
      obj.setVersion(version);
      return this;
    }

    public Builder description(String description) {
      obj.setDescription(description);
      return this;
    }

    public Builder type(String type) {
      obj.setType(type);
      return this;
    }

    public Builder script(String script) {
      obj.setScript(script);
      return this;
    }

    public Builder checksum(Integer checksum) {
      obj.setChecksum(checksum);
      return this;
    }

    public Builder installedBy(String installedBy) {
      obj.setInstalledBy(installedBy);
      return this;
    }

    public Builder installedOn(Date installedOn) {
      obj.setInstalledOn(installedOn);
      return this;
    }

    public Builder executionTime(Integer executionTime) {
      obj.setExecutionTime(executionTime);
      return this;
    }

    public Builder success(Boolean success) {
      obj.setSuccess(success);
      return this;
    }

    public FlywaySchemaHistoryDO build() {
      return this.obj;
    }
  }

  public enum Column {
    installedRank("installed_rank", "installedRank", "INTEGER", false), version("version",
        "version", "VARCHAR",
        false), description("description", "description", "VARCHAR", false), type("type", "type",
            "VARCHAR", true), script("script", "script", "VARCHAR", false), checksum("checksum",
                "checksum", "INTEGER", true), installedBy("installed_by", "installedBy", "VARCHAR",
                    false), installedOn("installed_on", "installedOn", "TIMESTAMP",
                        false), executionTime("execution_time", "executionTime", "INTEGER",
                            false), success("success", "success", "BIT", false);

    private static final String BEGINNING_DELIMITER = "`";

    private static final String ENDING_DELIMITER = "`";

    private final String column;

    private final boolean isColumnNameDelimited;

    private final String javaProperty;

    private final String jdbcType;

    public String value() {
      return this.column;
    }

    public String getValue() {
      return this.column;
    }

    public String getJavaProperty() {
      return this.javaProperty;
    }

    public String getJdbcType() {
      return this.jdbcType;
    }

    Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
      this.column = column;
      this.javaProperty = javaProperty;
      this.jdbcType = jdbcType;
      this.isColumnNameDelimited = isColumnNameDelimited;
    }

    public String desc() {
      return this.getEscapedColumnName() + " DESC";
    }

    public String asc() {
      return this.getEscapedColumnName() + " ASC";
    }

    public static Column[] excludes(Column... excludes) {
      ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
      if (excludes != null && excludes.length > 0) {
        columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
      }
      return columns.toArray(new Column[] {});
    }

    public static Column[] all() {
      return Column.values();
    }

    public String getEscapedColumnName() {
      if (this.isColumnNameDelimited) {
        return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column)
            .append(ENDING_DELIMITER).toString();
      } else {
        return this.column;
      }
    }

    public String getAliasedEscapedColumnName() {
      return this.getEscapedColumnName();
    }
  }
}
