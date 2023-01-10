/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDO;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.registry.core.utils.Dict;
import io.holoinsight.server.registry.core.utils.ObjectDict;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
public class TemplateUtils {
  public static CollectTemplate build(GaeaCollectConfigDO gcc) {
    // TODO 要对template的合法性做一个基本的校验

    CollectTemplate t = new CollectTemplate();
    t.setId(gcc.getId());
    t.setTableName(gcc.getTableName());

    CollectRange crm = new CollectRange();
    if (gcc.getCollectRange() != null && !"null".equals(gcc.getCollectRange())
        && !"{}".equals(gcc.getCollectRange())) {

      CollectRangeDTO cr = JsonUtils.fromJson(gcc.getCollectRange(), CollectRangeDTO.class);
      cr.setType(Dict.get(cr.getType()));

      switch (cr.getType()) {
        case CollectRange.CLOUDMONITOR:
          CollectRangeDTO.Cloudmonitor c = cr.getCloudmonitor();
          if (c == null) {
            throw new IllegalStateException("collectRange.cloudmonitor is null");
          }
          if (c.condition == null || c.condition.isEmpty()) {
            throw new IllegalArgumentException("condition is null");
          }
          List<QueryExample> ranges = new ArrayList<>(c.condition.size());
          for (Map<String, Object> condition : c.condition) {
            QueryExample qe = new QueryExample();
            qe.getParams().putAll(condition);
            ranges.add(qe);
          }
          CollectRange.Cloudmonitor cm = new CollectRange.Cloudmonitor();
          cm.setTable(c.table);
          cm.setRanges(ranges);
          crm.setType(CollectRange.CLOUDMONITOR);
          crm.setCloudmonitor(cm);
          break;
        case CollectRange.CENTRAL:
        case CollectRange.NONE:
          break;
        default:
          throw new IllegalArgumentException("unsupported collectRange " + gcc.getCollectRange());
      }
    }

    t.setCollectRange(crm);
    t.setTenant(gcc.getTenant());

    t.setVersion(gcc.getVersion().toString());
    t.setType(gcc.getType());
    t.setJson(gcc.getJson());
    t.setGmtModified(gcc.getGmtModified());

    t.setExecutorSelector(JsonUtils.fromJson(gcc.getExecutorSelector(), ExecutorSelector.class));
    t.getExecutorSelector().setType(StringUtils.lowerCase(t.getExecutorSelector().getType()));

    // TODO 临时代码
    if (t.getExecutorSelector().getType() == null) {
      if (t.getType().endsWith("OpenmetricsScraperDTO")) {
        t.getExecutorSelector().setType(ExecutorSelector.SIDECAR);
      }
    }

    if (StringUtils.isEmpty(t.getCollectRange().getType())
        && ExecutorSelector.CENTRAL.equals(t.getExecutorSelector().getType())) {
      t.getCollectRange().setType(CollectRange.CENTRAL);
    }

    t.reuseStrings();
    return t;
  }

  public static void fixForDim(RangeCondition rangeCondition) {
    RangeCondition cur = rangeCondition;
    while (cur != null) {
      String[] exp = cur.getExpress();
      if (exp != null) {
        ObjectDict.replace(exp);
      }
      if (cur.getValueRange() != null) {
        // All类型次数是NULL
        List<Object> adapterValues = new ArrayList<>(cur.getValueRange().size());
        for (Object value : cur.getValueRange()) {
          if (value instanceof Double) {
            adapterValues.add(((Double) value).intValue());
          } else {
            // 这里切记要防止爆炸!
            adapterValues.add(ObjectDict.get(value));
          }
        }
        cur.setValueRange(adapterValues);
      }
      cur = cur.getRight();
    }
  }

  public enum OpType {
    AND, OR, NOT
  }

  @Data
  public static class CloudmonitorRange {
    private List<String> express;
    private List<String> values;
  }

  @Data
  private static class CollectRangeDTO {
    private String type;
    private Dim dim;
    private Cloudmonitor cloudmonitor;

    @Data
    public static class Dim {
      private String sourceDim;
      private RangeCondition condition;
    }

    @Data
    public static class Cloudmonitor {
      private String table;
      private List<Map<String, Object>> condition;
    }
  }

  /**
   * 数据源范围的条件，支持多个条件组合匹配
   */
  @Getter
  @Setter
  @ToString
  public static class RangeCondition implements Serializable {

    private static final long serialVersionUID = 4383192437162433956L;

    /**
     * 取自sourceDim的同级或高级维度，如prince.idc、prince.ip,包含sourceDim字段
     */
    private boolean all = false;
    private boolean not = false;
    private String[] express;
    private List<Object> valueRange;
    // values是valueRange的同义词, 兼容在DB里配错
    // 两个字段任意一个都可以
    private List<Object> values;
    private OpType opType;
    private RangeCondition right;
  }

}
