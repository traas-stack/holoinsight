/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TraceAgentConfigurationDO {
  private Long id;

  private String tenant;

  private String service;

  private String type;

  private String language;

  private Date gmtCreate;

  private Date gmtModified;

  private String creator;

  private String modifier;

  private String value;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTenant() {
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant == null ? null : tenant.trim();
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service == null ? null : service.trim();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type == null ? null : type.trim();
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language == null ? null : language.trim();
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator == null ? null : creator.trim();
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String modifier) {
    this.modifier = modifier == null ? null : modifier.trim();
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value == null ? null : value.trim();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", id=").append(id);
    sb.append(", tenant=").append(tenant);
    sb.append(", service=").append(service);
    sb.append(", type=").append(type);
    sb.append(", language=").append(language);
    sb.append(", gmtCreate=").append(gmtCreate);
    sb.append(", gmtModified=").append(gmtModified);
    sb.append(", creator=").append(creator);
    sb.append(", modifier=").append(modifier);
    sb.append(", value=").append(value);
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
    TraceAgentConfigurationDO other = (TraceAgentConfigurationDO) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        && (this.getTenant() == null ? other.getTenant() == null
            : this.getTenant().equals(other.getTenant()))
        && (this.getService() == null ? other.getService() == null
            : this.getService().equals(other.getService()))
        && (this.getType() == null ? other.getType() == null
            : this.getType().equals(other.getType()))
        && (this.getLanguage() == null ? other.getLanguage() == null
            : this.getLanguage().equals(other.getLanguage()))
        && (this.getGmtCreate() == null ? other.getGmtCreate() == null
            : this.getGmtCreate().equals(other.getGmtCreate()))
        && (this.getGmtModified() == null ? other.getGmtModified() == null
            : this.getGmtModified().equals(other.getGmtModified()))
        && (this.getCreator() == null ? other.getCreator() == null
            : this.getCreator().equals(other.getCreator()))
        && (this.getModifier() == null ? other.getModifier() == null
            : this.getModifier().equals(other.getModifier()))
        && (this.getValue() == null ? other.getValue() == null
            : this.getValue().equals(other.getValue()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getTenant() == null) ? 0 : getTenant().hashCode());
    result = prime * result + ((getService() == null) ? 0 : getService().hashCode());
    result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
    result = prime * result + ((getLanguage() == null) ? 0 : getLanguage().hashCode());
    result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
    result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
    result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
    result = prime * result + ((getModifier() == null) ? 0 : getModifier().hashCode());
    result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
    return result;
  }

  public static TraceAgentConfigurationDO.Builder builder() {
    return new TraceAgentConfigurationDO.Builder();
  }

  public static class Builder {
    private TraceAgentConfigurationDO obj;

    public Builder() {
      this.obj = new TraceAgentConfigurationDO();
    }

    public Builder id(Long id) {
      obj.setId(id);
      return this;
    }

    public Builder tenant(String tenant) {
      obj.setTenant(tenant);
      return this;
    }

    public Builder service(String service) {
      obj.setService(service);
      return this;
    }

    public Builder type(String type) {
      obj.setType(type);
      return this;
    }

    public Builder language(String language) {
      obj.setLanguage(language);
      return this;
    }

    public Builder gmtCreate(Date gmtCreate) {
      obj.setGmtCreate(gmtCreate);
      return this;
    }

    public Builder gmtModified(Date gmtModified) {
      obj.setGmtModified(gmtModified);
      return this;
    }

    public Builder creator(String creator) {
      obj.setCreator(creator);
      return this;
    }

    public Builder modifier(String modifier) {
      obj.setModifier(modifier);
      return this;
    }

    public Builder value(String value) {
      obj.setValue(value);
      return this;
    }

    public TraceAgentConfigurationDO build() {
      return this.obj;
    }
  }

  public enum Column {
    id("id", "id", "BIGINT", false), tenant("tenant", "tenant", "VARCHAR", false), service(
        "service", "service", "VARCHAR", false), type("type", "type", "VARCHAR", true), language(
            "language", "language", "VARCHAR", true), gmtCreate("gmt_create", "gmtCreate",
                "TIMESTAMP", false), gmtModified("gmt_modified", "gmtModified", "TIMESTAMP",
                    false), creator("creator", "creator", "VARCHAR", false), modifier("modifier",
                        "modifier", "VARCHAR", false), value("value", "value", "LONGVARCHAR", true);

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
