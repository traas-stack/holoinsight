/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgentConfigurationDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public AgentConfigurationDOExample() {
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

  public AgentConfigurationDOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public AgentConfigurationDOExample orderBy(String... orderByClauses) {
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

  public AgentConfigurationDOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public AgentConfigurationDOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public AgentConfigurationDOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    AgentConfigurationDOExample example = new AgentConfigurationDOExample();
    return example.createCriteria();
  }

  public AgentConfigurationDOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public AgentConfigurationDOExample when(boolean condition, IExampleWhen then,
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

    public Criteria andTenantEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("tenant = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualTo(String value) {
      addCriterion("tenant <>", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("tenant <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThan(String value) {
      addCriterion("tenant >", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("tenant > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualTo(String value) {
      addCriterion("tenant >=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("tenant >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThan(String value) {
      addCriterion("tenant <", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("tenant < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualTo(String value) {
      addCriterion("tenant <=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualToColumn(AgentConfigurationDO.Column column) {
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

    public Criteria andServiceEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceNotEqualTo(String value) {
      addCriterion("service <>", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceNotEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThan(String value) {
      addCriterion("service >", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThanOrEqualTo(String value) {
      addCriterion("service >=", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceGreaterThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceLessThan(String value) {
      addCriterion("service <", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceLessThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("service < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andServiceLessThanOrEqualTo(String value) {
      addCriterion("service <=", value, "service");
      return (Criteria) this;
    }

    public Criteria andServiceLessThanOrEqualToColumn(AgentConfigurationDO.Column column) {
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

    public Criteria andAppIdIsNull() {
      addCriterion("app_id is null");
      return (Criteria) this;
    }

    public Criteria andAppIdIsNotNull() {
      addCriterion("app_id is not null");
      return (Criteria) this;
    }

    public Criteria andAppIdEqualTo(String value) {
      addCriterion("app_id =", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("app_id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAppIdNotEqualTo(String value) {
      addCriterion("app_id <>", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdNotEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("app_id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAppIdGreaterThan(String value) {
      addCriterion("app_id >", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdGreaterThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("app_id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAppIdGreaterThanOrEqualTo(String value) {
      addCriterion("app_id >=", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdGreaterThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("app_id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAppIdLessThan(String value) {
      addCriterion("app_id <", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdLessThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("app_id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAppIdLessThanOrEqualTo(String value) {
      addCriterion("app_id <=", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdLessThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("app_id <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andAppIdLike(String value) {
      addCriterion("app_id like", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdNotLike(String value) {
      addCriterion("app_id not like", value, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdIn(List<String> values) {
      addCriterion("app_id in", values, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdNotIn(List<String> values) {
      addCriterion("app_id not in", values, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdBetween(String value1, String value2) {
      addCriterion("app_id between", value1, value2, "appId");
      return (Criteria) this;
    }

    public Criteria andAppIdNotBetween(String value1, String value2) {
      addCriterion("app_id not between", value1, value2, "appId");
      return (Criteria) this;
    }

    public Criteria andEnvIdIsNull() {
      addCriterion("env_id is null");
      return (Criteria) this;
    }

    public Criteria andEnvIdIsNotNull() {
      addCriterion("env_id is not null");
      return (Criteria) this;
    }

    public Criteria andEnvIdEqualTo(String value) {
      addCriterion("env_id =", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("env_id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andEnvIdNotEqualTo(String value) {
      addCriterion("env_id <>", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdNotEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("env_id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andEnvIdGreaterThan(String value) {
      addCriterion("env_id >", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdGreaterThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("env_id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andEnvIdGreaterThanOrEqualTo(String value) {
      addCriterion("env_id >=", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdGreaterThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("env_id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andEnvIdLessThan(String value) {
      addCriterion("env_id <", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdLessThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(new StringBuilder("env_id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andEnvIdLessThanOrEqualTo(String value) {
      addCriterion("env_id <=", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdLessThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("env_id <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andEnvIdLike(String value) {
      addCriterion("env_id like", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdNotLike(String value) {
      addCriterion("env_id not like", value, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdIn(List<String> values) {
      addCriterion("env_id in", values, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdNotIn(List<String> values) {
      addCriterion("env_id not in", values, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdBetween(String value1, String value2) {
      addCriterion("env_id between", value1, value2, "envId");
      return (Criteria) this;
    }

    public Criteria andEnvIdNotBetween(String value1, String value2) {
      addCriterion("env_id not between", value1, value2, "envId");
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

    public Criteria andGmtCreateEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualToColumn(AgentConfigurationDO.Column column) {
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

    public Criteria andGmtModifiedEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualToColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanColumn(AgentConfigurationDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualToColumn(AgentConfigurationDO.Column column) {
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
  }

  public static class Criteria extends GeneratedCriteria {
    private AgentConfigurationDOExample example;

    protected Criteria(AgentConfigurationDOExample example) {
      super();
      this.example = example;
    }

    public AgentConfigurationDOExample example() {
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
    void example(io.holoinsight.server.common.dao.entity.AgentConfigurationDOExample example);
  }
}
