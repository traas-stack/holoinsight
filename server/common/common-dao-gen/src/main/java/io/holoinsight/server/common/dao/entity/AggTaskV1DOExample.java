/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AggTaskV1DOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public AggTaskV1DOExample() {
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

  public AggTaskV1DOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public AggTaskV1DOExample orderBy(String... orderByClauses) {
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

  public AggTaskV1DOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public AggTaskV1DOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public AggTaskV1DOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    AggTaskV1DOExample example = new AggTaskV1DOExample();
    return example.createCriteria();
  }

  public AggTaskV1DOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public AggTaskV1DOExample when(boolean condition, IExampleWhen then, IExampleWhen otherwise) {
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

    public Criteria andIdEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
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

    public Criteria andGmtCreateEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
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

    public Criteria andGmtModifiedEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
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

    public Criteria andAggIdIsNull() {
      addCriterion("agg_id is null");
      return (Criteria) this;
    }

    public Criteria andAggIdIsNotNull() {
      addCriterion("agg_id is not null");
      return (Criteria) this;
    }

    public Criteria andAggIdEqualTo(String value) {
      addCriterion("agg_id =", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("agg_id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAggIdNotEqualTo(String value) {
      addCriterion("agg_id <>", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("agg_id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAggIdGreaterThan(String value) {
      addCriterion("agg_id >", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("agg_id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAggIdGreaterThanOrEqualTo(String value) {
      addCriterion("agg_id >=", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("agg_id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAggIdLessThan(String value) {
      addCriterion("agg_id <", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("agg_id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAggIdLessThanOrEqualTo(String value) {
      addCriterion("agg_id <=", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("agg_id <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAggIdLike(String value) {
      addCriterion("agg_id like", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdNotLike(String value) {
      addCriterion("agg_id not like", value, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdIn(List<String> values) {
      addCriterion("agg_id in", values, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdNotIn(List<String> values) {
      addCriterion("agg_id not in", values, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdBetween(String value1, String value2) {
      addCriterion("agg_id between", value1, value2, "aggId");
      return (Criteria) this;
    }

    public Criteria andAggIdNotBetween(String value1, String value2) {
      addCriterion("agg_id not between", value1, value2, "aggId");
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

    public Criteria andVersionEqualTo(Long value) {
      addCriterion("version =", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("version = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionNotEqualTo(Long value) {
      addCriterion("version <>", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("version <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThan(Long value) {
      addCriterion("version >", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("version > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanOrEqualTo(Long value) {
      addCriterion("version >=", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("version >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionLessThan(Long value) {
      addCriterion("version <", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("version < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionLessThanOrEqualTo(Long value) {
      addCriterion("version <=", value, "version");
      return (Criteria) this;
    }

    public Criteria andVersionLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("version <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andVersionIn(List<Long> values) {
      addCriterion("version in", values, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotIn(List<Long> values) {
      addCriterion("version not in", values, "version");
      return (Criteria) this;
    }

    public Criteria andVersionBetween(Long value1, Long value2) {
      addCriterion("version between", value1, value2, "version");
      return (Criteria) this;
    }

    public Criteria andVersionNotBetween(Long value1, Long value2) {
      addCriterion("version not between", value1, value2, "version");
      return (Criteria) this;
    }

    public Criteria andRefIdIsNull() {
      addCriterion("ref_id is null");
      return (Criteria) this;
    }

    public Criteria andRefIdIsNotNull() {
      addCriterion("ref_id is not null");
      return (Criteria) this;
    }

    public Criteria andRefIdEqualTo(String value) {
      addCriterion("ref_id =", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("ref_id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRefIdNotEqualTo(String value) {
      addCriterion("ref_id <>", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("ref_id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRefIdGreaterThan(String value) {
      addCriterion("ref_id >", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("ref_id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRefIdGreaterThanOrEqualTo(String value) {
      addCriterion("ref_id >=", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("ref_id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRefIdLessThan(String value) {
      addCriterion("ref_id <", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(new StringBuilder("ref_id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRefIdLessThanOrEqualTo(String value) {
      addCriterion("ref_id <=", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("ref_id <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andRefIdLike(String value) {
      addCriterion("ref_id like", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdNotLike(String value) {
      addCriterion("ref_id not like", value, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdIn(List<String> values) {
      addCriterion("ref_id in", values, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdNotIn(List<String> values) {
      addCriterion("ref_id not in", values, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdBetween(String value1, String value2) {
      addCriterion("ref_id between", value1, value2, "refId");
      return (Criteria) this;
    }

    public Criteria andRefIdNotBetween(String value1, String value2) {
      addCriterion("ref_id not between", value1, value2, "refId");
      return (Criteria) this;
    }

    public Criteria andDeletedIsNull() {
      addCriterion("deleted is null");
      return (Criteria) this;
    }

    public Criteria andDeletedIsNotNull() {
      addCriterion("deleted is not null");
      return (Criteria) this;
    }

    public Criteria andDeletedEqualTo(Integer value) {
      addCriterion("deleted =", value, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("deleted = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDeletedNotEqualTo(Integer value) {
      addCriterion("deleted <>", value, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedNotEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("deleted <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDeletedGreaterThan(Integer value) {
      addCriterion("deleted >", value, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedGreaterThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("deleted > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDeletedGreaterThanOrEqualTo(Integer value) {
      addCriterion("deleted >=", value, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedGreaterThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("deleted >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDeletedLessThan(Integer value) {
      addCriterion("deleted <", value, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedLessThanColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("deleted < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDeletedLessThanOrEqualTo(Integer value) {
      addCriterion("deleted <=", value, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedLessThanOrEqualToColumn(AggTaskV1DO.Column column) {
      addCriterion(
          new StringBuilder("deleted <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andDeletedIn(List<Integer> values) {
      addCriterion("deleted in", values, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedNotIn(List<Integer> values) {
      addCriterion("deleted not in", values, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedBetween(Integer value1, Integer value2) {
      addCriterion("deleted between", value1, value2, "deleted");
      return (Criteria) this;
    }

    public Criteria andDeletedNotBetween(Integer value1, Integer value2) {
      addCriterion("deleted not between", value1, value2, "deleted");
      return (Criteria) this;
    }
  }

  public static class Criteria extends GeneratedCriteria {
    private AggTaskV1DOExample example;

    protected Criteria(AggTaskV1DOExample example) {
      super();
      this.example = example;
    }

    public AggTaskV1DOExample example() {
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
    void example(io.holoinsight.server.common.dao.entity.AggTaskV1DOExample example);
  }
}
