/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ApikeyDO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String name;

  private String apikey;

  private String tenant;

  private String creator;

  private String modifier;

  private String role;

  private Byte status;

  private String desc;

  private String accessConfig;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey == null ? null : apikey.trim();
  }

  public String getTenant() {
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant == null ? null : tenant.trim();
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

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role == null ? null : role.trim();
  }

  public Byte getStatus() {
    return status;
  }

  public void setStatus(Byte status) {
    this.status = status;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc == null ? null : desc.trim();
  }

  public String getAccessConfig() {
    return accessConfig;
  }

  public void setAccessConfig(String accessConfig) {
    this.accessConfig = accessConfig == null ? null : accessConfig.trim();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", id=").append(id);
    sb.append(", gmtCreate=").append(gmtCreate);
    sb.append(", gmtModified=").append(gmtModified);
    sb.append(", name=").append(name);
    sb.append(", apikey=").append(apikey);
    sb.append(", tenant=").append(tenant);
    sb.append(", creator=").append(creator);
    sb.append(", modifier=").append(modifier);
    sb.append(", role=").append(role);
    sb.append(", status=").append(status);
    sb.append(", desc=").append(desc);
    sb.append(", accessConfig=").append(accessConfig);
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
    ApikeyDO other = (ApikeyDO) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        && (this.getGmtCreate() == null ? other.getGmtCreate() == null
            : this.getGmtCreate().equals(other.getGmtCreate()))
        && (this.getGmtModified() == null ? other.getGmtModified() == null
            : this.getGmtModified().equals(other.getGmtModified()))
        && (this.getName() == null ? other.getName() == null
            : this.getName().equals(other.getName()))
        && (this.getApikey() == null ? other.getApikey() == null
            : this.getApikey().equals(other.getApikey()))
        && (this.getTenant() == null ? other.getTenant() == null
            : this.getTenant().equals(other.getTenant()))
        && (this.getCreator() == null ? other.getCreator() == null
            : this.getCreator().equals(other.getCreator()))
        && (this.getModifier() == null ? other.getModifier() == null
            : this.getModifier().equals(other.getModifier()))
        && (this.getRole() == null ? other.getRole() == null
            : this.getRole().equals(other.getRole()))
        && (this.getStatus() == null ? other.getStatus() == null
            : this.getStatus().equals(other.getStatus()))
        && (this.getDesc() == null ? other.getDesc() == null
            : this.getDesc().equals(other.getDesc()))
        && (this.getAccessConfig() == null ? other.getAccessConfig() == null
            : this.getAccessConfig().equals(other.getAccessConfig()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
    result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + ((getApikey() == null) ? 0 : getApikey().hashCode());
    result = prime * result + ((getTenant() == null) ? 0 : getTenant().hashCode());
    result = prime * result + ((getCreator() == null) ? 0 : getCreator().hashCode());
    result = prime * result + ((getModifier() == null) ? 0 : getModifier().hashCode());
    result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
    result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
    result = prime * result + ((getDesc() == null) ? 0 : getDesc().hashCode());
    result = prime * result + ((getAccessConfig() == null) ? 0 : getAccessConfig().hashCode());
    return result;
  }

  public static ApikeyDO.Builder builder() {
    return new ApikeyDO.Builder();
  }

  public static class Builder {
    private ApikeyDO obj;

    public Builder() {
      this.obj = new ApikeyDO();
    }

    public Builder id(Long id) {
      obj.setId(id);
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

    public Builder name(String name) {
      obj.setName(name);
      return this;
    }

    public Builder apikey(String apikey) {
      obj.setApikey(apikey);
      return this;
    }

    public Builder tenant(String tenant) {
      obj.setTenant(tenant);
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

    public Builder role(String role) {
      obj.setRole(role);
      return this;
    }

    public Builder status(Byte status) {
      obj.setStatus(status);
      return this;
    }

    public Builder desc(String desc) {
      obj.setDesc(desc);
      return this;
    }

    public Builder accessConfig(String accessConfig) {
      obj.setAccessConfig(accessConfig);
      return this;
    }

    public ApikeyDO build() {
      return this.obj;
    }
  }

  public enum Column {
    id("id", "id", "BIGINT", false), gmtCreate("gmt_create", "gmtCreate", "TIMESTAMP",
        false), gmtModified("gmt_modified", "gmtModified", "TIMESTAMP", false), name("name", "name",
            "VARCHAR", true), apikey("apikey", "apikey", "VARCHAR", false), tenant("tenant",
                "tenant", "VARCHAR", false), creator("creator", "creator", "VARCHAR",
                    false), modifier("modifier", "modifier", "VARCHAR", false), role("role", "role",
                        "VARCHAR", true), status("status", "status", "TINYINT", true), desc("desc",
                            "desc", "VARCHAR", true), accessConfig("access_config", "accessConfig",
                                "LONGVARCHAR", false);

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
