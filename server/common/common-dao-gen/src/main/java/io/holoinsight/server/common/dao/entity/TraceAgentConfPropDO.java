/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Arrays;

public class TraceAgentConfPropDO {
  private Long id;

  private String type;

  private String language;

  private String propKey;

  private String name;

  private String cName;

  private String checkExpression;

  private String description;

  private String cDescription;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getPropKey() {
    return propKey;
  }

  public void setPropKey(String propKey) {
    this.propKey = propKey == null ? null : propKey.trim();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getcName() {
    return cName;
  }

  public void setcName(String cName) {
    this.cName = cName == null ? null : cName.trim();
  }

  public String getCheckExpression() {
    return checkExpression;
  }

  public void setCheckExpression(String checkExpression) {
    this.checkExpression = checkExpression == null ? null : checkExpression.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public String getcDescription() {
    return cDescription;
  }

  public void setcDescription(String cDescription) {
    this.cDescription = cDescription == null ? null : cDescription.trim();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", id=").append(id);
    sb.append(", type=").append(type);
    sb.append(", language=").append(language);
    sb.append(", propKey=").append(propKey);
    sb.append(", name=").append(name);
    sb.append(", cName=").append(cName);
    sb.append(", checkExpression=").append(checkExpression);
    sb.append(", description=").append(description);
    sb.append(", cDescription=").append(cDescription);
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
    TraceAgentConfPropDO other = (TraceAgentConfPropDO) that;
    return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        && (this.getType() == null ? other.getType() == null
            : this.getType().equals(other.getType()))
        && (this.getLanguage() == null ? other.getLanguage() == null
            : this.getLanguage().equals(other.getLanguage()))
        && (this.getPropKey() == null ? other.getPropKey() == null
            : this.getPropKey().equals(other.getPropKey()))
        && (this.getName() == null ? other.getName() == null
            : this.getName().equals(other.getName()))
        && (this.getcName() == null ? other.getcName() == null
            : this.getcName().equals(other.getcName()))
        && (this.getCheckExpression() == null ? other.getCheckExpression() == null
            : this.getCheckExpression().equals(other.getCheckExpression()))
        && (this.getDescription() == null ? other.getDescription() == null
            : this.getDescription().equals(other.getDescription()))
        && (this.getcDescription() == null ? other.getcDescription() == null
            : this.getcDescription().equals(other.getcDescription()));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
    result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
    result = prime * result + ((getLanguage() == null) ? 0 : getLanguage().hashCode());
    result = prime * result + ((getPropKey() == null) ? 0 : getPropKey().hashCode());
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result + ((getcName() == null) ? 0 : getcName().hashCode());
    result =
        prime * result + ((getCheckExpression() == null) ? 0 : getCheckExpression().hashCode());
    result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
    result = prime * result + ((getcDescription() == null) ? 0 : getcDescription().hashCode());
    return result;
  }

  public static TraceAgentConfPropDO.Builder builder() {
    return new TraceAgentConfPropDO.Builder();
  }

  public static class Builder {
    private TraceAgentConfPropDO obj;

    public Builder() {
      this.obj = new TraceAgentConfPropDO();
    }

    public Builder id(Long id) {
      obj.setId(id);
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

    public Builder propKey(String propKey) {
      obj.setPropKey(propKey);
      return this;
    }

    public Builder name(String name) {
      obj.setName(name);
      return this;
    }

    public Builder cName(String cName) {
      obj.setcName(cName);
      return this;
    }

    public Builder checkExpression(String checkExpression) {
      obj.setCheckExpression(checkExpression);
      return this;
    }

    public Builder description(String description) {
      obj.setDescription(description);
      return this;
    }

    public Builder cDescription(String cDescription) {
      obj.setcDescription(cDescription);
      return this;
    }

    public TraceAgentConfPropDO build() {
      return this.obj;
    }
  }

  public enum Column {
    id("id", "id", "BIGINT", false), type("type", "type", "VARCHAR", true), language("language",
        "language", "VARCHAR", true), propKey("prop_key", "propKey", "VARCHAR", false), name("name",
            "name", "VARCHAR", true), cName("c_name", "cName", "VARCHAR", false), checkExpression(
                "check_expression", "checkExpression", "VARCHAR", false), description("description",
                    "description", "LONGVARCHAR",
                    false), cDescription("c_description", "cDescription", "LONGVARCHAR", false);

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
