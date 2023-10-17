/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import io.holoinsight.server.agg.v1.core.conf.OutputField;
import io.holoinsight.server.agg.v1.core.conf.OutputItem;
import io.holoinsight.server.agg.v1.core.conf.SelectItem;
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

  public void agg(XAggTask rule, AggProtos.AggTaskValue aggTaskValue, AggProtos.InDataNode in) {
    Map<String, List<XSelectItem>> m = rule.getSelect().getSelectItem(aggTaskValue.getMetric());
    if (m == null) {
      return;
    }
    List<XSelectItem> records = m.get("-");
    if (records != null) {
      for (XSelectItem item : records) {
        fields[item.getIndex()].add(in, item.getInner(), null);
      }
    }

    // TODO hard code
    switch (in.getType()) {
      case 0:
        // single float64
        // fallthrough
      case 1:
      // single bytes
      {
        List<XSelectItem> m2 = m.get("value");
        if (CollectionUtils.isEmpty(m2)) {
          return;
        }

        BasicFieldAccessors.Accessor accessor = BasicFieldAccessors.direct(in);
        for (XSelectItem item : m2) {
          if (!item.getWhere().test(in)) {
            continue;
          }
          fields[item.getIndex()].add(in, item.getInner(), accessor);
        }
      }
        break;

      case 2:
        for (Map.Entry<String, AggProtos.BasicField> e : in.getFieldsMap().entrySet()) {
          String fieldName = e.getKey();
          List<XSelectItem> m2 = m.get(fieldName);
          if (m2 == null) {
            continue;
          }

          BasicFieldAccessors.Accessor accessor = BasicFieldAccessors.field(e.getValue());
          for (XSelectItem item : m2) {
            if (!item.getWhere().test(in)) {
              continue;
            }
            fields[item.getIndex()].add(in, item.getInner(), accessor);
          }
        }
        break;
    }
  }

}
