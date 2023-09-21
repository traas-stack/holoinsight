/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TraceAgentConfigurationDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public TraceAgentConfigurationDOExample() {
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

  public TraceAgentConfigurationDOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public TraceAgentConfigurationDOExample orderBy(String... orderByClauses) {
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

  public TraceAgentConfigurationDOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public TraceAgentConfigurationDOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public TraceAgentConfigurationDOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    TraceAgentConfigurationDOExample example = new TraceAgentConfigurationDOExample();
    return example.createCriteria();
  }

  public TraceAgentConfigurationDOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public TraceAgentConfigurationDOExample when(boolean condition, IExampleWhen then,
      IExampleWhen otherwise) {
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

    public Criteria andIdEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
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

    public Criteria andTenantEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("tenant = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualTo(String value) {
      addCriterion("tenant <>", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("tenant <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThan(String value) {
      addCriterion("tenant >", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("tenant > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualTo(String value) {
      addCriterion("tenant >=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("tenant >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThan(String value) {
      addCriterion("tenant <", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("tenant < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualTo(String value) {
      addCriterion("tenant <=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
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

    public Criteria andServiceIsNull() {
      addCriterion("service is null");
      return (Criteria) this;
    }

    public Criteria andServiceIsNotNull() {
      addCriterion("service is not null");
      return (Criteria) this;
    }

    public Criteria andServiceEqualTo(String value) {
      addCriterion("service =", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceNotEqualTo(String value) {
      addCriterion("service <>", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThan(String value) {
      addCriterion("service >", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThanOrEqualTo(String value) {
      addCriterion("service >=", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceLessThan(String value) {
      addCriterion("service <", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceLessThanOrEqualTo(String value) {
      addCriterion("service <=", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceLike(String value) {
      addCriterion("service like", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceNotLike(String value) {
      addCriterion("service not like", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceIn(List<String> values) {
      addCriterion("service in", values, "service");
      return (Criteria) this;
    }

    public Criteria andServiceNotIn(List<String> values) {
      addCriterion("service not in", values, "service");
      return (Criteria) this;
    }

    public Criteria andServiceBetween(String value1, String value2) {
      addCriterion("service between", value1, value2, "service");
      return (Criteria) this;
    }

    public Criteria andServiceNotBetween(String value1, String value2) {
      addCriterion("service not between", value1, value2, "service");
      return (Criteria) this;
    }

    public Criteria andTypeIsNull() {
      addCriterion("`type` is null");
      return (Criteria) this;
    }

    public Criteria andTypeIsNotNull() {
      addCriterion("`type` is not null");
      return (Criteria) this;
    }

    public Criteria andTypeEqualTo(String value) {
      addCriterion("`type` =", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("`type` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeNotEqualTo(String value) {
      addCriterion("`type` <>", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`type` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThan(String value) {
      addCriterion("`type` >", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("`type` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanOrEqualTo(String value) {
      addCriterion("`type` >=", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`type` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeLessThan(String value) {
      addCriterion("`type` <", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("`type` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeLessThanOrEqualTo(String value) {
      addCriterion("`type` <=", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`type` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeLike(String value) {
      addCriterion("`type` like", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotLike(String value) {
      addCriterion("`type` not like", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeIn(List<String> values) {
      addCriterion("`type` in", values, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotIn(List<String> values) {
      addCriterion("`type` not in", values, "type");
      return (Criteria) this;
    }

    public Criteria andTypeBetween(String value1, String value2) {
      addCriterion("`type` between", value1, value2, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotBetween(String value1, String value2) {
      addCriterion("`type` not between", value1, value2, "type");
      return (Criteria) this;
    }

    public Criteria andLanguageIsNull() {
      addCriterion("`language` is null");
      return (Criteria) this;
    }

    public Criteria andLanguageIsNotNull() {
      addCriterion("`language` is not null");
      return (Criteria) this;
    }

    public Criteria andLanguageEqualTo(String value) {
      addCriterion("`language` =", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`language` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageNotEqualTo(String value) {
      addCriterion("`language` <>", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`language` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThan(String value) {
      addCriterion("`language` >", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`language` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThanOrEqualTo(String value) {
      addCriterion("`language` >=", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`language` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageLessThan(String value) {
      addCriterion("`language` <", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`language` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageLessThanOrEqualTo(String value) {
      addCriterion("`language` <=", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("`language` <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageLike(String value) {
      addCriterion("`language` like", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageNotLike(String value) {
      addCriterion("`language` not like", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageIn(List<String> values) {
      addCriterion("`language` in", values, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageNotIn(List<String> values) {
      addCriterion("`language` not in", values, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageBetween(String value1, String value2) {
      addCriterion("`language` between", value1, value2, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageNotBetween(String value1, String value2) {
      addCriterion("`language` not between", value1, value2, "language");
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

    public Criteria andGmtCreateEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualToColumn(
        TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
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

    public Criteria andGmtModifiedEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualToColumn(
        TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
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

    public Criteria andCreatorEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("creator = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorNotEqualTo(String value) {
      addCriterion("creator <>", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("creator <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThan(String value) {
      addCriterion("creator >", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("creator > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThanOrEqualTo(String value) {
      addCriterion("creator >=", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("creator >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorLessThan(String value) {
      addCriterion("creator <", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("creator < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCreatorLessThanOrEqualTo(String value) {
      addCriterion("creator <=", value, "creator");
      return (Criteria) this;
    }

    public Criteria andCreatorLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
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

    public Criteria andModifierEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("modifier = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierNotEqualTo(String value) {
      addCriterion("modifier <>", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierNotEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("modifier <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThan(String value) {
      addCriterion("modifier >", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("modifier > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThanOrEqualTo(String value) {
      addCriterion("modifier >=", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierGreaterThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("modifier >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierLessThan(String value) {
      addCriterion("modifier <", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierLessThanColumn(TraceAgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("modifier < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andModifierLessThanOrEqualTo(String value) {
      addCriterion("modifier <=", value, "modifier");
      return (Criteria) this;
    }

    public Criteria andModifierLessThanOrEqualToColumn(TraceAgentConfigurationDO.Column column) {
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
  }

  public static class Criteria extends GeneratedCriteria {
    private TraceAgentConfigurationDOExample example;

    protected Criteria(TraceAgentConfigurationDOExample example) {
      super();
      this.example = example;
    }

    public TraceAgentConfigurationDOExample example() {
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
    void example(io.holoinsight.server.common.dao.entity.TraceAgentConfigurationDOExample example);
  }
}
