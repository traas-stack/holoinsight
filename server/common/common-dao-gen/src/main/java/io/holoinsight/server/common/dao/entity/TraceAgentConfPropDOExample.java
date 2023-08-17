/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.List;

public class TraceAgentConfPropDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public TraceAgentConfPropDOExample() {
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

  public TraceAgentConfPropDOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public TraceAgentConfPropDOExample orderBy(String... orderByClauses) {
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

  public TraceAgentConfPropDOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public TraceAgentConfPropDOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public TraceAgentConfPropDOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    TraceAgentConfPropDOExample example = new TraceAgentConfPropDOExample();
    return example.createCriteria();
  }

  public TraceAgentConfPropDOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public TraceAgentConfPropDOExample when(boolean condition, IExampleWhen then,
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

    public Criteria andIdEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
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

    public Criteria andTypeEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("`type` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeNotEqualTo(String value) {
      addCriterion("`type` <>", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`type` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThan(String value) {
      addCriterion("`type` >", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("`type` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanOrEqualTo(String value) {
      addCriterion("`type` >=", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeGreaterThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`type` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeLessThan(String value) {
      addCriterion("`type` <", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("`type` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTypeLessThanOrEqualTo(String value) {
      addCriterion("`type` <=", value, "type");
      return (Criteria) this;
    }

    public Criteria andTypeLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
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

    public Criteria andLanguageEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`language` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageNotEqualTo(String value) {
      addCriterion("`language` <>", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`language` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThan(String value) {
      addCriterion("`language` >", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`language` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThanOrEqualTo(String value) {
      addCriterion("`language` >=", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageGreaterThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`language` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageLessThan(String value) {
      addCriterion("`language` <", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`language` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andLanguageLessThanOrEqualTo(String value) {
      addCriterion("`language` <=", value, "language");
      return (Criteria) this;
    }

    public Criteria andLanguageLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
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

    public Criteria andPropKeyIsNull() {
      addCriterion("prop_key is null");
      return (Criteria) this;
    }

    public Criteria andPropKeyIsNotNull() {
      addCriterion("prop_key is not null");
      return (Criteria) this;
    }

    public Criteria andPropKeyEqualTo(String value) {
      addCriterion("prop_key =", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("prop_key = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andPropKeyNotEqualTo(String value) {
      addCriterion("prop_key <>", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("prop_key <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andPropKeyGreaterThan(String value) {
      addCriterion("prop_key >", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("prop_key > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andPropKeyGreaterThanOrEqualTo(String value) {
      addCriterion("prop_key >=", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyGreaterThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("prop_key >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andPropKeyLessThan(String value) {
      addCriterion("prop_key <", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("prop_key < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andPropKeyLessThanOrEqualTo(String value) {
      addCriterion("prop_key <=", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("prop_key <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andPropKeyLike(String value) {
      addCriterion("prop_key like", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyNotLike(String value) {
      addCriterion("prop_key not like", value, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyIn(List<String> values) {
      addCriterion("prop_key in", values, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyNotIn(List<String> values) {
      addCriterion("prop_key not in", values, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyBetween(String value1, String value2) {
      addCriterion("prop_key between", value1, value2, "propKey");
      return (Criteria) this;
    }

    public Criteria andPropKeyNotBetween(String value1, String value2) {
      addCriterion("prop_key not between", value1, value2, "propKey");
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

    public Criteria andNameEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("`name` = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameNotEqualTo(String value) {
      addCriterion("`name` <>", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`name` <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameGreaterThan(String value) {
      addCriterion("`name` >", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("`name` > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualTo(String value) {
      addCriterion("`name` >=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameGreaterThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("`name` >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLessThan(String value) {
      addCriterion("`name` <", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("`name` < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualTo(String value) {
      addCriterion("`name` <=", value, "name");
      return (Criteria) this;
    }

    public Criteria andNameLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
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

    public Criteria andCNameIsNull() {
      addCriterion("c_name is null");
      return (Criteria) this;
    }

    public Criteria andCNameIsNotNull() {
      addCriterion("c_name is not null");
      return (Criteria) this;
    }

    public Criteria andCNameEqualTo(String value) {
      addCriterion("c_name =", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("c_name = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCNameNotEqualTo(String value) {
      addCriterion("c_name <>", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("c_name <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCNameGreaterThan(String value) {
      addCriterion("c_name >", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("c_name > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCNameGreaterThanOrEqualTo(String value) {
      addCriterion("c_name >=", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameGreaterThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("c_name >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCNameLessThan(String value) {
      addCriterion("c_name <", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("c_name < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCNameLessThanOrEqualTo(String value) {
      addCriterion("c_name <=", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(
          new StringBuilder("c_name <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andCNameLike(String value) {
      addCriterion("c_name like", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameNotLike(String value) {
      addCriterion("c_name not like", value, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameIn(List<String> values) {
      addCriterion("c_name in", values, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameNotIn(List<String> values) {
      addCriterion("c_name not in", values, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameBetween(String value1, String value2) {
      addCriterion("c_name between", value1, value2, "cName");
      return (Criteria) this;
    }

    public Criteria andCNameNotBetween(String value1, String value2) {
      addCriterion("c_name not between", value1, value2, "cName");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionIsNull() {
      addCriterion("check_expression is null");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionIsNotNull() {
      addCriterion("check_expression is not null");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionEqualTo(String value) {
      addCriterion("check_expression =", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("check_expression = ").append(column.getEscapedColumnName())
          .toString());
      return (Criteria) this;
    }

    public Criteria andCheckExpressionNotEqualTo(String value) {
      addCriterion("check_expression <>", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionNotEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("check_expression <> ").append(column.getEscapedColumnName())
          .toString());
      return (Criteria) this;
    }

    public Criteria andCheckExpressionGreaterThan(String value) {
      addCriterion("check_expression >", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionGreaterThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("check_expression > ").append(column.getEscapedColumnName())
          .toString());
      return (Criteria) this;
    }

    public Criteria andCheckExpressionGreaterThanOrEqualTo(String value) {
      addCriterion("check_expression >=", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionGreaterThanOrEqualToColumn(
        TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("check_expression >= ").append(column.getEscapedColumnName())
          .toString());
      return (Criteria) this;
    }

    public Criteria andCheckExpressionLessThan(String value) {
      addCriterion("check_expression <", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionLessThanColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("check_expression < ").append(column.getEscapedColumnName())
          .toString());
      return (Criteria) this;
    }

    public Criteria andCheckExpressionLessThanOrEqualTo(String value) {
      addCriterion("check_expression <=", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionLessThanOrEqualToColumn(TraceAgentConfPropDO.Column column) {
      addCriterion(new StringBuilder("check_expression <= ").append(column.getEscapedColumnName())
          .toString());
      return (Criteria) this;
    }

    public Criteria andCheckExpressionLike(String value) {
      addCriterion("check_expression like", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionNotLike(String value) {
      addCriterion("check_expression not like", value, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionIn(List<String> values) {
      addCriterion("check_expression in", values, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionNotIn(List<String> values) {
      addCriterion("check_expression not in", values, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionBetween(String value1, String value2) {
      addCriterion("check_expression between", value1, value2, "checkExpression");
      return (Criteria) this;
    }

    public Criteria andCheckExpressionNotBetween(String value1, String value2) {
      addCriterion("check_expression not between", value1, value2, "checkExpression");
      return (Criteria) this;
    }
  }

  public static class Criteria extends GeneratedCriteria {
    private TraceAgentConfPropDOExample example;

    protected Criteria(TraceAgentConfPropDOExample example) {
      super();
      this.example = example;
    }

    public TraceAgentConfPropDOExample example() {
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
    void example(io.holoinsight.server.common.dao.entity.TraceAgentConfPropDOExample example);
  }
}
