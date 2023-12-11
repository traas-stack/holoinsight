/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class AggTaskV1DO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String aggId;

  private Long version;

  private String refId;

  private Integer deleted;

  private String json;

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

  public String getAggId() {
    return aggId;
  }

  public void setAggId(String aggId) {
    this.aggId = aggId == null ? null : aggId.trim();
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

  public Integer getDeleted() {
    return deleted;
  }

  public void setDeleted(Integer deleted) {
    this.deleted = deleted;
  }

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json == null ? null : json.trim();
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
    sb.append(", aggId=").append(aggId);
    sb.append(", version=").append(version);
    sb.append(", refId=").append(refId);
    sb.append(", deleted=").append(deleted);
    sb.append(", json=").append(json);
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
    AggTaskV1DO other = (AggTaskV1DO) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        && (this.getGmtCreate() == null ? other.getGmtCreate() == null
            : this.getGmtCreate().equals(other.getGmtCreate()))
        && (this.getGmtModified() == null ? other.getGmtModified() == null
            : this.getGmtModified().equals(other.getGmtModified()))
        && (this.getAggId() == null ? other.getAggId() == null
            : this.getAggId().equals(other.getAggId()))
        && (this.getVersion() == null ? other.getVersion() == null
            : this.getVersion().equals(other.getVersion()))
        && (this.getRefId() == null ? other.getRefId() == null
            : this.getRefId().equals(other.getRefId()))
        && (this.getDeleted() == null ? other.getDeleted() == null
            : this.getDeleted().equals(other.getDeleted()))
        && (this.getJson() == null ? other.getJson() == null
            : this.getJson().equals(other.getJson()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
    result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
    result = prime * result + ((getAggId() == null) ? 0 : getAggId().hashCode());
    result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
    result = prime * result + ((getRefId() == null) ? 0 : getRefId().hashCode());
    result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
    result = prime * result + ((getJson() == null) ? 0 : getJson().hashCode());
    return result;
  }

  public static AggTaskV1DO.Builder builder() {
    return new AggTaskV1DO.Builder();
  }

  public static class Builder {
    private AggTaskV1DO obj;

    public Builder() {
      this.obj = new AggTaskV1DO();
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

    public Builder aggId(String aggId) {
      obj.setAggId(aggId);
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

    public Builder deleted(Integer deleted) {
      obj.setDeleted(deleted);
      return this;
    }

    public Builder json(String json) {
      obj.setJson(json);
      return this;
    }

    public AggTaskV1DO build() {
      return this.obj;
    }
  }

  public enum Column {
    id("id", "id", "BIGINT", false), gmtCreate("gmt_create", "gmtCreate", "TIMESTAMP",
        false), gmtModified("gmt_modified", "gmtModified", "TIMESTAMP", false), aggId("agg_id",
            "aggId", "VARCHAR", false), version("version", "version", "BIGINT",
                false), refId("ref_id", "refId", "VARCHAR", false), deleted("deleted", "deleted",
                    "INTEGER", false), json("json", "json", "LONGVARCHAR", false);

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
