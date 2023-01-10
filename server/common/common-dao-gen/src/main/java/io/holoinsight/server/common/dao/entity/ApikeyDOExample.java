/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApikeyDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public ApikeyDOExample() {
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

  public ApikeyDOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public ApikeyDOExample orderBy(String... orderByClauses) {
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

  public ApikeyDOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public ApikeyDOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public ApikeyDOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    ApikeyDOExample example = new ApikeyDOExample();
    return example.createCriteria();
  }

  public ApikeyDOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public ApikeyDOExample when(boolean condition, IExampleWhen then, IExampleWhen otherwise) {
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

    public Criteria andIdEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualToColumn(ApikeyDO.Column column) {
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

    public Criteria andGmtCreateEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualToColumn(ApikeyDO.Column column) {
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

    public Criteria andGmtModifiedEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualToColumn(ApikeyDO.Column column) {
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

    public Criteria andNameEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`name` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameNotEqualTo(String value) {
      addCriterion("`name` <>", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`name` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameGreaterThan(String value) {
      addCriterion("`name` >", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`name` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualTo(String value) {
      addCriterion("`name` >=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`name` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLessThan(String value) {
      addCriterion("`name` <", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`name` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualTo(String value) {
      addCriterion("`name` <=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualToColumn(ApikeyDO.Column column) {
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

    public Criteria andApikeyIsNull() {
      addCriterion("apikey is null");
      return (Criteria) this;
    }

    public Criteria andApikeyIsNotNull() {
      addCriterion("apikey is not null");
      return (Criteria) this;
    }

    public Criteria andApikeyEqualTo(String value) {
      addCriterion("apikey =", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("apikey = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andApikeyNotEqualTo(String value) {
      addCriterion("apikey <>", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("apikey <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andApikeyGreaterThan(String value) {
      addCriterion("apikey >", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("apikey > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andApikeyGreaterThanOrEqualTo(String value) {
      addCriterion("apikey >=", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("apikey >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andApikeyLessThan(String value) {
      addCriterion("apikey <", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyLessThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("apikey < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andApikeyLessThanOrEqualTo(String value) {
      addCriterion("apikey <=", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyLessThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("apikey <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andApikeyLike(String value) {
      addCriterion("apikey like", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyNotLike(String value) {
      addCriterion("apikey not like", value, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyIn(List<String> values) {
      addCriterion("apikey in", values, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyNotIn(List<String> values) {
      addCriterion("apikey not in", values, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyBetween(String value1, String value2) {
      addCriterion("apikey between", value1, value2, "apikey");
      return (Criteria) this;
    }

    public Criteria andApikeyNotBetween(String value1, String value2) {
      addCriterion("apikey not between", value1, value2, "apikey");
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

    public Criteria andTenantEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("tenant = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualTo(String value) {
      addCriterion("tenant <>", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("tenant <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThan(String value) {
      addCriterion("tenant >", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("tenant > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualTo(String value) {
      addCriterion("tenant >=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("tenant >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThan(String value) {
      addCriterion("tenant <", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("tenant < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualTo(String value) {
      addCriterion("tenant <=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualToColumn(ApikeyDO.Column column) {
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

    public Criteria andCreatorIsNull() {
      addCriterion("creator is null");
      return (Criteria) this;
    }

    public Criteria andCreatorIsNotNull() {
      addCriterion("creator is not null");
      return (Criteria) this;
    }

    public Criteria andCreatorEqualTo(String value) {
      addCriterion("creator =", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("creator = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorNotEqualTo(String value) {
      addCriterion("creator <>", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("creator <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThan(String value) {
      addCriterion("creator >", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("creator > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThanOrEqualTo(String value) {
      addCriterion("creator >=", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("creator >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorLessThan(String value) {
      addCriterion("creator <", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorLessThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("creator < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorLessThanOrEqualTo(String value) {
      addCriterion("creator <=", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorLessThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("creator <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorLike(String value) {
      addCriterion("creator like", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorNotLike(String value) {
      addCriterion("creator not like", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorIn(List<String> values) {
      addCriterion("creator in", values, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorNotIn(List<String> values) {
      addCriterion("creator not in", values, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorBetween(String value1, String value2) {
      addCriterion("creator between", value1, value2, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorNotBetween(String value1, String value2) {
      addCriterion("creator not between", value1, value2, "creator");
      return (Criteria) this;
    }

    public Criteria andModifierIsNull() {
      addCriterion("modifier is null");
      return (Criteria) this;
    }

    public Criteria andModifierIsNotNull() {
      addCriterion("modifier is not null");
      return (Criteria) this;
    }

    public Criteria andModifierEqualTo(String value) {
      addCriterion("modifier =", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("modifier = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierNotEqualTo(String value) {
      addCriterion("modifier <>", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("modifier <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThan(String value) {
      addCriterion("modifier >", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("modifier > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThanOrEqualTo(String value) {
      addCriterion("modifier >=", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("modifier >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierLessThan(String value) {
      addCriterion("modifier <", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierLessThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("modifier < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierLessThanOrEqualTo(String value) {
      addCriterion("modifier <=", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierLessThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("modifier <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierLike(String value) {
      addCriterion("modifier like", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierNotLike(String value) {
      addCriterion("modifier not like", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierIn(List<String> values) {
      addCriterion("modifier in", values, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierNotIn(List<String> values) {
      addCriterion("modifier not in", values, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierBetween(String value1, String value2) {
      addCriterion("modifier between", value1, value2, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierNotBetween(String value1, String value2) {
      addCriterion("modifier not between", value1, value2, "modifier");
      return (Criteria) this;
    }

    public Criteria andRoleIsNull() {
      addCriterion("`role` is null");
      return (Criteria) this;
    }

    public Criteria andRoleIsNotNull() {
      addCriterion("`role` is not null");
      return (Criteria) this;
    }

    public Criteria andRoleEqualTo(String value) {
      addCriterion("`role` =", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`role` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRoleNotEqualTo(String value) {
      addCriterion("`role` <>", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`role` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRoleGreaterThan(String value) {
      addCriterion("`role` >", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`role` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRoleGreaterThanOrEqualTo(String value) {
      addCriterion("`role` >=", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`role` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRoleLessThan(String value) {
      addCriterion("`role` <", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleLessThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`role` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRoleLessThanOrEqualTo(String value) {
      addCriterion("`role` <=", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleLessThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`role` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRoleLike(String value) {
      addCriterion("`role` like", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleNotLike(String value) {
      addCriterion("`role` not like", value, "role");
      return (Criteria) this;
    }

    public Criteria andRoleIn(List<String> values) {
      addCriterion("`role` in", values, "role");
      return (Criteria) this;
    }

    public Criteria andRoleNotIn(List<String> values) {
      addCriterion("`role` not in", values, "role");
      return (Criteria) this;
    }

    public Criteria andRoleBetween(String value1, String value2) {
      addCriterion("`role` between", value1, value2, "role");
      return (Criteria) this;
    }

    public Criteria andRoleNotBetween(String value1, String value2) {
      addCriterion("`role` not between", value1, value2, "role");
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

    public Criteria andStatusEqualTo(Byte value) {
      addCriterion("`status` =", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`status` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusNotEqualTo(Byte value) {
      addCriterion("`status` <>", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`status` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThan(Byte value) {
      addCriterion("`status` >", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`status` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
      addCriterion("`status` >=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`status` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusLessThan(Byte value) {
      addCriterion("`status` <", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThanColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`status` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusLessThanOrEqualTo(Byte value) {
      addCriterion("`status` <=", value, "status");
      return (Criteria) this;
    }

    public Criteria andStatusLessThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`status` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andStatusIn(List<Byte> values) {
      addCriterion("`status` in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotIn(List<Byte> values) {
      addCriterion("`status` not in", values, "status");
      return (Criteria) this;
    }

    public Criteria andStatusBetween(Byte value1, Byte value2) {
      addCriterion("`status` between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andStatusNotBetween(Byte value1, Byte value2) {
      addCriterion("`status` not between", value1, value2, "status");
      return (Criteria) this;
    }

    public Criteria andDescIsNull() {
      addCriterion("`desc` is null");
      return (Criteria) this;
    }

    public Criteria andDescIsNotNull() {
      addCriterion("`desc` is not null");
      return (Criteria) this;
    }

    public Criteria andDescEqualTo(String value) {
      addCriterion("`desc` =", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescEqualToColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`desc` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDescNotEqualTo(String value) {
      addCriterion("`desc` <>", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescNotEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`desc` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDescGreaterThan(String value) {
      addCriterion("`desc` >", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescGreaterThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`desc` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDescGreaterThanOrEqualTo(String value) {
      addCriterion("`desc` >=", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescGreaterThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`desc` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDescLessThan(String value) {
      addCriterion("`desc` <", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescLessThanColumn(ApikeyDO.Column column) {
      addCriterion(new StringBuilder("`desc` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDescLessThanOrEqualTo(String value) {
      addCriterion("`desc` <=", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescLessThanOrEqualToColumn(ApikeyDO.Column column) {
      addCriterion(
          new StringBuilder("`desc` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDescLike(String value) {
      addCriterion("`desc` like", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescNotLike(String value) {
      addCriterion("`desc` not like", value, "desc");
      return (Criteria) this;
    }

    public Criteria andDescIn(List<String> values) {
      addCriterion("`desc` in", values, "desc");
      return (Criteria) this;
    }

    public Criteria andDescNotIn(List<String> values) {
      addCriterion("`desc` not in", values, "desc");
      return (Criteria) this;
    }

    public Criteria andDescBetween(String value1, String value2) {
      addCriterion("`desc` between", value1, value2, "desc");
      return (Criteria) this;
    }

    public Criteria andDescNotBetween(String value1, String value2) {
      addCriterion("`desc` not between", value1, value2, "desc");
      return (Criteria) this;
    }
  }

  public static class Criteria extends GeneratedCriteria {
    private ApikeyDOExample example;

    protected Criteria(ApikeyDOExample example) {
      super();
      this.example = example;
    }

    public ApikeyDOExample example() {
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
    void example(io.holoinsight.server.common.dao.entity.ApikeyDOExample example);
  }
}
