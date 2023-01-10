/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GaeaConfigDOExample {
  protected String orderByClause;

  protected boolean distinct;

  protected List<Criteria> oredCriteria;

  protected Integer offset;

  protected Integer rows;

  public GaeaConfigDOExample() {
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

  public GaeaConfigDOExample orderBy(String orderByClause) {
    this.setOrderByClause(orderByClause);
    return this;
  }

  public GaeaConfigDOExample orderBy(String... orderByClauses) {
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

  public GaeaConfigDOExample limit(Integer rows) {
    this.rows = rows;
    return this;
  }

  public GaeaConfigDOExample limit(Integer offset, Integer rows) {
    this.offset = offset;
    this.rows = rows;
    return this;
  }

  public GaeaConfigDOExample page(Integer page, Integer pageSize) {
    this.offset = page * pageSize;
    this.rows = pageSize;
    return this;
  }

  public static Criteria newAndCreateCriteria() {
    GaeaConfigDOExample example = new GaeaConfigDOExample();
    return example.createCriteria();
  }

  public GaeaConfigDOExample when(boolean condition, IExampleWhen then) {
    if (condition) {
      then.example(this);
    }
    return this;
  }

  public GaeaConfigDOExample when(boolean condition, IExampleWhen then, IExampleWhen otherwise) {
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

    public Criteria andIdEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("id = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdNotEqualTo(Long value) {
      addCriterion("id <>", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdNotEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("id <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThan(Long value) {
      addCriterion("id >", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("id > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualTo(Long value) {
      addCriterion("id >=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdGreaterThanOrEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("id >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThan(Long value) {
      addCriterion("id <", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("id < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualTo(Long value) {
      addCriterion("id <=", value, "id");
      return (Criteria) this;
    }

    public Criteria andIdLessThanOrEqualToColumn(GaeaConfigDO.Column column) {
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

    public Criteria andGmtCreateEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualTo(Date value) {
      addCriterion("gmt_create <>", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateNotEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThan(Date value) {
      addCriterion("gmt_create >", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_create >=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateGreaterThanOrEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThan(Date value) {
      addCriterion("gmt_create <", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_create < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualTo(Date value) {
      addCriterion("gmt_create <=", value, "gmtCreate");
      return (Criteria) this;
    }

    public Criteria andGmtCreateLessThanOrEqualToColumn(GaeaConfigDO.Column column) {
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

    public Criteria andGmtModifiedEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualTo(Date value) {
      addCriterion("gmt_modified <>", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedNotEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThan(Date value) {
      addCriterion("gmt_modified >", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualTo(Date value) {
      addCriterion("gmt_modified >=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedGreaterThanOrEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThan(Date value) {
      addCriterion("gmt_modified <", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("gmt_modified < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualTo(Date value) {
      addCriterion("gmt_modified <=", value, "gmtModified");
      return (Criteria) this;
    }

    public Criteria andGmtModifiedLessThanOrEqualToColumn(GaeaConfigDO.Column column) {
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

    public Criteria andTenantEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("tenant = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualTo(String value) {
      addCriterion("tenant <>", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantNotEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("tenant <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThan(String value) {
      addCriterion("tenant >", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("tenant > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualTo(String value) {
      addCriterion("tenant >=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantGreaterThanOrEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("tenant >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThan(String value) {
      addCriterion("tenant <", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanColumn(GaeaConfigDO.Column column) {
      addCriterion(new StringBuilder("tenant < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualTo(String value) {
      addCriterion("tenant <=", value, "tenant");
      return (Criteria) this;
    }

    public Criteria andTenantLessThanOrEqualToColumn(GaeaConfigDO.Column column) {
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

    public Criteria andConfKeyIsNull() {
      addCriterion("conf_key is null");
      return (Criteria) this;
    }

    public Criteria andConfKeyIsNotNull() {
      addCriterion("conf_key is not null");
      return (Criteria) this;
    }

    public Criteria andConfKeyEqualTo(String value) {
      addCriterion("conf_key =", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("conf_key = ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andConfKeyNotEqualTo(String value) {
      addCriterion("conf_key <>", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyNotEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("conf_key <> ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andConfKeyGreaterThan(String value) {
      addCriterion("conf_key >", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyGreaterThanColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("conf_key > ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andConfKeyGreaterThanOrEqualTo(String value) {
      addCriterion("conf_key >=", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyGreaterThanOrEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("conf_key >= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andConfKeyLessThan(String value) {
      addCriterion("conf_key <", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyLessThanColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("conf_key < ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andConfKeyLessThanOrEqualTo(String value) {
      addCriterion("conf_key <=", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyLessThanOrEqualToColumn(GaeaConfigDO.Column column) {
      addCriterion(
          new StringBuilder("conf_key <= ").append(column.getEscapedColumnName()).toString());
      return (Criteria) this;
    }

    public Criteria andConfKeyLike(String value) {
      addCriterion("conf_key like", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyNotLike(String value) {
      addCriterion("conf_key not like", value, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyIn(List<String> values) {
      addCriterion("conf_key in", values, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyNotIn(List<String> values) {
      addCriterion("conf_key not in", values, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyBetween(String value1, String value2) {
      addCriterion("conf_key between", value1, value2, "confKey");
      return (Criteria) this;
    }

    public Criteria andConfKeyNotBetween(String value1, String value2) {
      addCriterion("conf_key not between", value1, value2, "confKey");
      return (Criteria) this;
    }
  }

  public static class Criteria extends GeneratedCriteria {
    private GaeaConfigDOExample example;

    protected Criteria(GaeaConfigDOExample example) {
      super();
      this.example = example;
    }

    public GaeaConfigDOExample example() {
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
    void example(io.holoinsight.server.common.dao.entity.GaeaConfigDOExample example);
  }
}
