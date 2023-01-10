/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GaeaLockDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public GaeaLockDOExample() {
    oredCriteria = new ArrayList<Criteria>();
  }

  public void setOrderByClause(String orderByClause) {
    this.orderByClause = orderByClause;
  }

  public String getOrderByClause() {
    return orderByClause;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public boolean isDistinct() {
    return distinct;
  }

  public List<Criteria> getOredCriteria() {
    return oredCriteria;
  }

  public void or(Criteria criteria) {
    oredCriteria.add(criteria);
  }

  public Criteria or() {
    Criteria criteria = createCriteriaInternal();
    oredCriteria.add(criteria);
    return criteria;
  }

  public GaeaLockDOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public GaeaLockDOExample orderBy(String... orderByClauses) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < orderByClauses.length; i++) {
      sb.append(orderByClauses[i]);
      if (i < orderByClauses.length - 1) {
        sb.append(" , ");
      }
    }
    this.setOrderByClause(sb.toString());
    return this;
  }

  public Criteria createCriteria() {
    Criteria criteria = createCriteriaInternal();
    if (oredCriteria.size() == 0) {
      oredCriteria.add(criteria);
    }
    return criteria;
  }

  protected Criteria createCriteriaInternal() {
    Criteria criteria = new Criteria(this);
    return criteria;
  }

  public void clear() {
    oredCriteria.clear();
    orderByClause = null;
    distinct = false;
    rows = null;
    offset = null;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public Integer getOffset() {
    return this.offset;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public Integer getRows() {
    return this.rows;
  }

  public GaeaLockDOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public GaeaLockDOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public GaeaLockDOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    GaeaLockDOExample example = new GaeaLockDOExample();
    return example.createCriteria();
  }

  public GaeaLockDOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public GaeaLockDOExample when(boolean condition, IExampleWhen then, IExampleWhen otherwise) {
    if (condition) {
      then.example(this);
    } else {
      otherwise.example(this);
    }
    return this;
  }

  protected abstract static class GeneratedCriteria {
    protected List<Criterion> criteria;

    protected GeneratedCriteria() {
      super();
      criteria = new ArrayList<Criterion>();
    }

    public boolean isValid() {
      return criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
      return criteria;
    }

    public List<Criterion> getCriteria() {
      return criteria;
    }

    protected void addCriterion(String condition) {
      if (condition == null) {
        throw new RuntimeException("Value for condition cannot be null");
      }
      criteria.add(new Criterion(condition));
    }

    protected void addCriterion(String condition, Object value, String property) {
      if (value == null) {
        throw new RuntimeException("Value for " + property + " cannot be null");
      }
      criteria.add(new Criterion(condition, value));
    }

    protected void addCriterion(String condition, Object value1, Object value2, String property) {
      if (value1 == null || value2 == null) {
        throw new RuntimeException("Between values for " + property + " cannot be null");
      }
      criteria.add(new Criterion(condition, value1, value2));
    }

    public Criteria andIdIsNull() {
      addCriterion("id is null");
      return (Criteria) this;
    }

    public Criteria andIdIsNotNull() {
      addCriterion("id is not null");
      return (Criteria) this;
    }

    public Criteria andIdEqualTo(Long value) {
      addCriterion("id =", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("id <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdIn(List<Long> values) {
      addCriterion("id in", values, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotIn(List<Long> values) {
      addCriterion("id not in", values, "id");
      return (Criteria) this;
    }

    public Criteria andIdBetween(Long value1, Long value2) {
      addCriterion("id between", value1, value2, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotBetween(Long value1, Long value2) {
      addCriterion("id not between", value1, value2, "id");
      return (Criteria) this;
    }

    public Criteria andGmtCreateIsNull() {
      addCriterion("gmt_create is null");
      return (Criteria) this;
    }

    public Criteria andGmtCreateIsNotNull() {
      addCriterion("gmt_create is not null");
      return (Criteria) this;
    }

    public Criteria andGmtCreateEqualTo(Date value) {
      addCriterion("gmt_create =", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateIn(List<Date> values) {
      addCriterion("gmt_create in", values, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotIn(List<Date> values) {
      addCriterion("gmt_create not in", values, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateBetween(Date value1, Date value2) {
      addCriterion("gmt_create between", value1, value2, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotBetween(Date value1, Date value2) {
      addCriterion("gmt_create not between", value1, value2, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedIsNull() {
      addCriterion("gmt_modified is null");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedIsNotNull() {
      addCriterion("gmt_modified is not null");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedEqualTo(Date value) {
      addCriterion("gmt_modified =", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedIn(List<Date> values) {
      addCriterion("gmt_modified in", values, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotIn(List<Date> values) {
      addCriterion("gmt_modified not in", values, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedBetween(Date value1, Date value2) {
      addCriterion("gmt_modified between", value1, value2, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotBetween(Date value1, Date value2) {
      addCriterion("gmt_modified not between", value1, value2, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andTenantIsNull() {
      addCriterion("tenant is null");
      return (Criteria) this;
    }

    public Criteria andTenantIsNotNull() {
      addCriterion("tenant is not null");
      return (Criteria) this;
    }

    public Criteria andTenantEqualTo(String value) {
      addCriterion("tenant =", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("tenant = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualTo(String value) {
      addCriterion("tenant <>", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("tenant <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThan(String value) {
      addCriterion("tenant >", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("tenant > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualTo(String value) {
      addCriterion("tenant >=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("tenant >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThan(String value) {
      addCriterion("tenant <", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("tenant < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualTo(String value) {
      addCriterion("tenant <=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("tenant <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLike(String value) {
      addCriterion("tenant like", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotLike(String value) {
      addCriterion("tenant not like", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantIn(List<String> values) {
      addCriterion("tenant in", values, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotIn(List<String> values) {
      addCriterion("tenant not in", values, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantBetween(String value1, String value2) {
      addCriterion("tenant between", value1, value2, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotBetween(String value1, String value2) {
      addCriterion("tenant not between", value1, value2, "tenant");
      return (Criteria) this;
    }

    public Criteria andNameIsNull() {
      addCriterion("`name` is null");
      return (Criteria) this;
    }

    public Criteria andNameIsNotNull() {
      addCriterion("`name` is not null");
      return (Criteria) this;
    }

    public Criteria andNameEqualTo(String value) {
      addCriterion("`name` =", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("`name` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameNotEqualTo(String value) {
      addCriterion("`name` <>", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`name` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameGreaterThan(String value) {
      addCriterion("`name` >", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("`name` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualTo(String value) {
      addCriterion("`name` >=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`name` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLessThan(String value) {
      addCriterion("`name` <", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("`name` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualTo(String value) {
      addCriterion("`name` <=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`name` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLike(String value) {
      addCriterion("`name` like", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotLike(String value) {
      addCriterion("`name` not like", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameIn(List<String> values) {
      addCriterion("`name` in", values, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotIn(List<String> values) {
      addCriterion("`name` not in", values, "name");
      return (Criteria) this;
    }

    public Criteria andNameBetween(String value1, String value2) {
      addCriterion("`name` between", value1, value2, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotBetween(String value1, String value2) {
      addCriterion("`name` not between", value1, value2, "name");
      return (Criteria) this;
    }

    public Criteria andVersionIsNull() {
      addCriterion("version is null");
      return (Criteria) this;
    }

    public Criteria andVersionIsNotNull() {
      addCriterion("version is not null");
      return (Criteria) this;
    }

    public Criteria andVersionEqualTo(Integer value) {
      addCriterion("version =", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("version = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionNotEqualTo(Integer value) {
      addCriterion("version <>", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("version <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThan(Integer value) {
      addCriterion("version >", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("version > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanOrEqualTo(Integer value) {
      addCriterion("version >=", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("version >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionLessThan(Integer value) {
      addCriterion("version <", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("version < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionLessThanOrEqualTo(Integer value) {
      addCriterion("version <=", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("version <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionIn(List<Integer> values) {
      addCriterion("version in", values, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotIn(List<Integer> values) {
      addCriterion("version not in", values, "version");
      return (Criteria) this;
    }

    public Criteria andVersionBetween(Integer value1, Integer value2) {
      addCriterion("version between", value1, value2, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotBetween(Integer value1, Integer value2) {
      addCriterion("version not between", value1, value2, "version");
      return (Criteria) this;
    }

    public Criteria andJsonIsNull() {
      addCriterion("json is null");
      return (Criteria) this;
    }

    public Criteria andJsonIsNotNull() {
      addCriterion("json is not null");
      return (Criteria) this;
    }

    public Criteria andJsonEqualTo(String value) {
      addCriterion("json =", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("json = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andJsonNotEqualTo(String value) {
      addCriterion("json <>", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("json <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andJsonGreaterThan(String value) {
      addCriterion("json >", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("json > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andJsonGreaterThanOrEqualTo(String value) {
      addCriterion("json >=", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("json >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andJsonLessThan(String value) {
      addCriterion("json <", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("json < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andJsonLessThanOrEqualTo(String value) {
      addCriterion("json <=", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(new StringBuilder("json <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andJsonLike(String value) {
      addCriterion("json like", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonNotLike(String value) {
      addCriterion("json not like", value, "json");
      return (Criteria) this;
    }

    public Criteria andJsonIn(List<String> values) {
      addCriterion("json in", values, "json");
      return (Criteria) this;
    }

    public Criteria andJsonNotIn(List<String> values) {
      addCriterion("json not in", values, "json");
      return (Criteria) this;
    }

    public Criteria andJsonBetween(String value1, String value2) {
      addCriterion("json between", value1, value2, "json");
      return (Criteria) this;
    }

    public Criteria andJsonNotBetween(String value1, String value2) {
      addCriterion("json not between", value1, value2, "json");
      return (Criteria) this;
    }

    public Criteria andStatusIsNull() {
      addCriterion("`status` is null");
      return (Criteria) this;
    }

    public Criteria andStatusIsNotNull() {
      addCriterion("`status` is not null");
      return (Criteria) this;
    }

    public Criteria andStatusEqualTo(Integer value) {
      addCriterion("`status` =", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`status` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusNotEqualTo(Integer value) {
      addCriterion("`status` <>", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`status` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThan(Integer value) {
      addCriterion("`status` >", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`status` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
      addCriterion("`status` >=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`status` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusLessThan(Integer value) {
      addCriterion("`status` <", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThanColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`status` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusLessThanOrEqualTo(Integer value) {
      addCriterion("`status` <=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThanOrEqualToColumn(GaeaLockDO.Column column) {
      addCriterion(
          new StringBuilder("`status` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusIn(List<Integer> values) {
      addCriterion("`status` in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotIn(List<Integer> values) {
      addCriterion("`status` not in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusBetween(Integer value1, Integer value2) {
      addCriterion("`status` between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotBetween(Integer value1, Integer value2) {
      addCriterion("`status` not between", value1, value2, "status");
      return (Criteria) this;
    }
  }

  public static class Criteria extends GeneratedCriteria {
    private GaeaLockDOExample example;

    protected Criteria(GaeaLockDOExample example) {
      super();
      this.example = example;
    }

    public GaeaLockDOExample example() {
      return this.example;
    }

    @Deprecated
    public Criteria andIf(boolean ifAdd, ICriteriaAdd add) {
      if (ifAdd) {
        add.add(this);
      }
      return this;
    }

    public Criteria when(boolean condition, ICriteriaWhen then) {
      if (condition) {
        then.criteria(this);
      }
      return this;
    }

    public Criteria when(boolean condition, ICriteriaWhen then, ICriteriaWhen otherwise) {
      if (condition) {
        then.criteria(this);
      } else {
        otherwise.criteria(this);
      }
      return this;
    }

    @Deprecated
    public interface ICriteriaAdd {
      Criteria add(Criteria add);
    }
  }

  public static class Criterion {
    private String condition;

    private Object value;

    private Object secondValue;

    private boolean noValue;

    private boolean singleValue;

    private boolean betweenValue;

    private boolean listValue;

    private String typeHandler;

    public String getCondition() {
      return condition;
    }

    public Object getValue() {
      return value;
    }

    public Object getSecondValue() {
      return secondValue;
    }

    public boolean isNoValue() {
      return noValue;
    }

    public boolean isSingleValue() {
      return singleValue;
    }

    public boolean isBetweenValue() {
      return betweenValue;
    }

    public boolean isListValue() {
      return listValue;
    }

    public String getTypeHandler() {
      return typeHandler;
    }

    protected Criterion(String condition) {
      super();
      this.condition = condition;
      this.typeHandler = null;
      this.noValue = true;
    }

    protected Criterion(String condition, Object value, String typeHandler) {
      super();
      this.condition = condition;
      this.value = value;
      this.typeHandler = typeHandler;
      if (value instanceof List<?>) {
        this.listValue = true;
      } else {
        this.singleValue = true;
      }
    }

    protected Criterion(String condition, Object value) {
      this(condition, value, null);
    }

    protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
      super();
      this.condition = condition;
      this.value = value;
      this.secondValue = secondValue;
      this.typeHandler = typeHandler;
      this.betweenValue = true;
    }

    protected Criterion(String condition, Object value, Object secondValue) {
      this(condition, value, secondValue, null);
    }
  }

  public interface ICriteriaWhen {
    void criteria(Criteria criteria);
  }

  public interface IExampleWhen {
    void example(io.holoinsight.server.common.dao.entity.GaeaLockDOExample example);
  }
}
