/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GaeaCollectConfigDO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String tenant;

  private String tableName;

  private Integer deleted;

  private Long version;

  private String refId;

  private String bizTenant;

  private String type;

  private String json;

  private String collectRange;

  private String executorSelector;

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

  public String getTenant() {
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant == null ? null : tenant.trim();
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName == null ? null : tableName.trim();
  }

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId == null ? null : refId.trim();
  }

  public String getBizTenant() {
    return bizTenant;
  }

  public void setBizTenant(String bizTenant) {
    this.bizTenant = bizTenant == null ? null : bizTenant.trim();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type == null ? null : type.trim();
  }

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json == null ? null : json.trim();
  }

  public String getCollectRange() {
    return collectRange;
  }

  public void setCollectRange(String collectRange) {
    this.collectRange = collectRange == null ? null : collectRange.trim();
  }

  public String getExecutorSelector() {
    return executorSelector;
  }

  public void setExecutorSelector(String executorSelector) {
    this.executorSelector = executorSelector == null ? null : executorSelector.trim();
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
    sb.append(", tenant=").append(tenant);
    sb.append(", tableName=").append(tableName);
    sb.append(", deleted=").append(deleted);
    sb.append(", version=").append(version);
    sb.append(", refId=").append(refId);
    sb.append(", bizTenant=").append(bizTenant);
    sb.append(", type=").append(type);
    sb.append(", json=").append(json);
    sb.append(", collectRange=").append(collectRange);
    sb.append(", executorSelector=").append(executorSelector);
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
    GaeaCollectConfigDO other = (GaeaCollectConfigDO) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        && (this.getGmtCreate() == null ? other.getGmtCreate() == null
            : this.getGmtCreate().equals(other.getGmtCreate()))
        && (this.getGmtModified() == null ? other.getGmtModified() == null
            : this.getGmtModified().equals(other.getGmtModified()))
        && (this.getTenant() == null ? other.getTenant() == null
            : this.getTenant().equals(other.getTenant()))
        && (this.getTableName() == null ? other.getTableName() == null
            : this.getTableName().equals(other.getTableName()))
        && (this.getDeleted() == null ? other.getDeleted() == null
            : this.getDeleted().equals(other.getDeleted()))
        && (this.getVersion() == null ? other.getVersion() == null
            : this.getVersion().equals(other.getVersion()))
        && (this.getRefId() == null ? other.getRefId() == null
            : this.getRefId().equals(other.getRefId()))
        && (this.getBizTenant() == null ? other.getBizTenant() == null
            : this.getBizTenant().equals(other.getBizTenant()))
        && (this.getType() == null ? other.getType() == null
            : this.getType().equals(other.getType()))
        && (this.getJson() == null ? other.getJson() == null
            : this.getJson().equals(other.getJson()))
        && (this.getCollectRange() == null ? other.getCollectRange() == null
            : this.getCollectRange().equals(other.getCollectRange()))
        && (this.getExecutorSelector() == null ? other.getExecutorSelector() == null
            : this.getExecutorSelector().equals(other.getExecutorSelector()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
    result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
    result = prime * result + ((getTenant() == null) ? 0 : getTenant().hashCode());
    result = prime * result + ((getTableName() == null) ? 0 : getTableName().hashCode());
    result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
    result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
    result = prime * result + ((getRefId() == null) ? 0 : getRefId().hashCode());
    result = prime * result + ((getBizTenant() == null) ? 0 : getBizTenant().hashCode());
    result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
    result = prime * result + ((getJson() == null) ? 0 : getJson().hashCode());
    result = prime * result + ((getCollectRange() == null) ? 0 : getCollectRange().hashCode());
    result =
        prime * result + ((getExecutorSelector() == null) ? 0 : getExecutorSelector().hashCode());
    return result;
  }

  public static GaeaCollectConfigDO.Builder builder() {
    return new GaeaCollectConfigDO.Builder();
  }

  public static class Builder {
    private GaeaCollectConfigDO obj;

    public Builder() {
      this.obj = new GaeaCollectConfigDO();
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

    public Builder tenant(String tenant) {
      obj.setTenant(tenant);
      return this;
    }

    public Builder tableName(String tableName) {
      obj.setTableName(tableName);
      return this;
    }

    public Builder deleted(Integer deleted) {
      obj.setDeleted(deleted);
      return this;
    }

    public Builder version(Long version) {
      obj.setVersion(version);
      return this;
    }

    public Builder refId(String refId) {
      obj.setRefId(refId);
      return this;
    }

    public Builder bizTenant(String bizTenant) {
      obj.setBizTenant(bizTenant);
      return this;
    }

    public Builder type(String type) {
      obj.setType(type);
      return this;
    }

    public Builder json(String json) {
      obj.setJson(json);
      return this;
    }

    public Builder collectRange(String collectRange) {
      obj.setCollectRange(collectRange);
      return this;
    }

    public Builder executorSelector(String executorSelector) {
      obj.setExecutorSelector(executorSelector);
      return this;
    }

    public GaeaCollectConfigDO build() {
      return this.obj;
    }
  }

  public enum Column {
    id("id", "id", "BIGINT", false), gmtCreate("gmt_create", "gmtCreate", "TIMESTAMP",
        false), gmtModified("gmt_modified", "gmtModified", "TIMESTAMP", false), tenant("tenant",
            "tenant", "VARCHAR", false), tableName("table_name", "tableName", "VARCHAR",
                true), deleted("deleted", "deleted", "INTEGER", false), version("version",
                    "version", "BIGINT", false), refId("ref_id", "refId", "VARCHAR",
                        false), bizTenant("biz_tenant", "bizTenant", "VARCHAR", false), type("type",
                            "type", "VARCHAR", true), json("json", "json", "LONGVARCHAR",
                                false), collectRange("collect_range", "collectRange", "LONGVARCHAR",
                                    false), executorSelector("executor_selector",
                                        "executorSelector", "LONGVARCHAR", false);

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
