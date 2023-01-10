/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TenantOpsDO {
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String tenant;

  private String storage;

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

  public String getStorage() {
    return storage;
  }

  public void setStorage(String storage) {
    this.storage = storage == null ? null : storage.trim();
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
    sb.append(", storage=").append(storage);
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
    TenantOpsDO other = (TenantOpsDO) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        && (this.getGmtCreate() == null ? other.getGmtCreate() == null
            : this.getGmtCreate().equals(other.getGmtCreate()))
        && (this.getGmtModified() == null ? other.getGmtModified() == null
            : this.getGmtModified().equals(other.getGmtModified()))
        && (this.getTenant() == null ? other.getTenant() == null
            : this.getTenant().equals(other.getTenant()))
        && (this.getStorage() == null ? other.getStorage() == null
            : this.getStorage().equals(other.getStorage()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
    result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
    result = prime * result + ((getTenant() == null) ? 0 : getTenant().hashCode());
    result = prime * result + ((getStorage() == null) ? 0 : getStorage().hashCode());
    return result;
  }

  public static TenantOpsDO.Builder builder() {
    return new TenantOpsDO.Builder();
  }

  public static class Builder {
    private TenantOpsDO obj;

    public Builder() {
      this.obj = new TenantOpsDO();
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

    public Builder storage(String storage) {
      obj.setStorage(storage);
      return this;
    }

    public TenantOpsDO build() {
      return this.obj;
    }
  }

  public enum Column {
    id("id", "id", "BIGINT", false), gmtCreate("gmt_create", "gmtCreate", "TIMESTAMP",
        false), gmtModified("gmt_modified", "gmtModified", "TIMESTAMP", false), tenant("tenant",
            "tenant", "VARCHAR", false), storage("storage", "storage", "VARCHAR", true);

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
