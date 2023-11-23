/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import io.holoinsight.server.agg.v1.core.conf.OutputField;
import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.conf.SelectItem;
import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Slf4j
@Data
public class Group {
  private FixedSizeTags tags;
  private GroupField[] fields;

  public void reuseStrings() {}

  public Map<String, Object> getFinalFields1(XAggTask aggTask) {
    List<XSelectItem> items = aggTask.getSelect().getItems();
    Preconditions.checkArgument(items.size() == fields.length);

    Map<String, Object> finalFields = Maps.newHashMapWithExpectedSize(fields.length);

    for (int i = 0; i < fields.length; i++) {
      GroupField gf = fields[i];
      XSelectItem xsi = items.get(i);
      finalFields.put(xsi.getInner().getAs(), gf.getFinalValue());
    }

    return finalFields;
  }

  public Map<String, Object> getFinalFields(OutputItem oi, Map<String, Object> env) {
    Map<String, Object> finalFields = Maps.newHashMapWithExpectedSize(oi.getFields().size());

    for (OutputField of : oi.getFields()) {
      try {
        Object value = of.getCompiledExpression().execute(env);
        finalFields.put(of.getName(), value);
      } catch (Exception e) {
        log.error("expression error", e);
      }
    }

    return finalFields;
  }

  public void initGroupFields(XSelect select) {
    List<SelectItem> items = select.getInner().getItems();
    fields = new GroupField[items.size()];
    for (int i = 0; i < items.size(); i++) {
      SelectItem item = items.get(i);
      GroupField gf = new GroupField();
      gf.setAgg(item.getAgg());
      fields[i] = gf;
    }
  }

  public void agg(XAggTask rule, AggProtos.AggTaskValue aggTaskValue, DataAccessor da) {
    Map<String, List<XSelectItem>> m = rule.getSelect().getSelectItem(aggTaskValue.getMetric());
    if (m == null) {
      return;
    }

    if (da.isSingleValue()) {
      processField(m, da, "value");
    } else {
      for (String fieldName : da.getFieldNames()) {
        processField(m, da, fieldName);
      }
    }

    processField(m, da, XSelect.NO_FIELD);
  }

  private void processField(Map<String, List<XSelectItem>> fieldSelectItems, DataAccessor da,
      String fieldName) {
    List<XSelectItem> items = fieldSelectItems.get(fieldName);
    if (items != null) {
      for (XSelectItem item : items) {
        da.bindFieldName(item.getInner().getElect().getField());
        if (!item.getWhere().test(da)) {
          continue;
        }
        fields[item.getIndex()].add(da, item.getInner());
      }
    }
  }
}
