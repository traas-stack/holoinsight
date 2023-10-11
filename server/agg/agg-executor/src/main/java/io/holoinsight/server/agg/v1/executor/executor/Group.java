/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.Arrays;
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
      if (gf.getInput() == 0) {
        continue;
      }
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

    // TODO hard code
    switch (in.getType()) {
      case 0:
        // 单值 metric
      case 1:
      // 单值 bytes
      {
        List<XSelectItem> m2 = m.get("value");
        if (CollectionUtils.isEmpty(m2)) {
          return;
        }

        for (XSelectItem item : m2) {
          if (!item.getWhere().test(in)) {
            continue;
          }
          fields[item.getIndex()].add(in);
        }
      }
        break;

      case 2:
        if (true) {
          throw new UnsupportedOperationException("unsupported");
        }

        for (String fieldName : Arrays.asList("field1", "field2")) {
          List<XSelectItem> m2 = m.get(fieldName);
          if (m2 == null) {
            return;
          }

          for (XSelectItem item : m2) {
            if (!item.getWhere().test(in)) {
              continue;
            }
            fields[item.getIndex()].add(in);
          }
        }
    }
  }

}
