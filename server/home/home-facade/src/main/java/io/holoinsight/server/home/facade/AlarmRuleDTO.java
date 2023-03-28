/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:38 下午
 */
@Data
public class AlarmRuleDTO {

  /**
   * id
   */
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 规则名称
   */
  private String ruleName;

  /**
   * 规则类型（ai、rule、pql）
   */
  private String ruleType;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

  /**
   * 告警级别
   */
  private String alarmLevel;

  /**
   * 规则描述
   */
  private String ruleDescribe;

  /**
   * 规则是否生效
   */
  private Byte status;

  /**
   * 合并是否开启
   */
  private Byte isMerge;

  /**
   * 合并方式
   */
  private String mergeType;

  /**
   * 恢复通知是否开启
   */
  private Byte recover;

  /**
   * 通知方式
   */
  private String noticeType;

  /**
   * 触发方式简述
   */
  private List<String> alarmContent;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 告警规则
   */
  private Map<String, Object> rule;

  /**
   * 生效时间
   */
  private Map<String, Object> timeFilter;

  /**
   * 屏蔽id
   */
  private Long blockId;

  /**
   * 来源类型
   */
  private String sourceType;

  /**
   * 来源id
   */
  private Long sourceId;

  /**
   * 额外信息
   */
  private AlertRuleExtra extra;

  /**
   * 环境类型
   */
  private String envType;

  /**
   * pql
   */
  private String pql;

  public static void tryParseLink(AlarmRuleDTO alarmRuleDTO, String domain,
      Map<String /* metric */, Map<String /* type */, String /* page */>> systemMetrics,
      String parentId) {
    if (alarmRuleDTO.isCreate()) {
      if (StringUtils.isNotEmpty(alarmRuleDTO.sourceType)) {
        if (alarmRuleDTO.extra == null) {
          alarmRuleDTO.extra = new AlertRuleExtra();
        }
        switch (alarmRuleDTO.sourceType) {
          case "log":
            alarmRuleDTO.extra.sourceLink =
                domain + "log/metric/" + alarmRuleDTO.sourceId + "?tenant=" + alarmRuleDTO.tenant;
            if (StringUtils.isNotEmpty(parentId)) {
              alarmRuleDTO.extra.sourceLink =
                  alarmRuleDTO.extra.sourceLink + "&parentId=" + parentId;
            }
            break;
          case "dashboard":
            alarmRuleDTO.extra.sourceLink = domain + "m/dashboard/preview/" + alarmRuleDTO.sourceId;
            break;
          default:
            if (alarmRuleDTO.sourceType.startsWith("apm_")) {
              alarmRuleDTO.extra.sourceLink = getApmLink(alarmRuleDTO, domain, systemMetrics);
            }
        }
      }
    }
  }

  private boolean isCreate() {
    return id == null;
  }

  protected static String getApmLink(AlarmRuleDTO alarmRuleDTO, String domain,
      Map<String /* metric */, Map<String /* type */, String /* page */>> systemMetrics) {
    String app = alarmRuleDTO.sourceType.split("_")[1];
    String metric = alarmRuleDTO.getMetric();
    boolean isServer = alarmRuleDTO.checkIsServer();
    if (StringUtils.isEmpty(metric) || CollectionUtils.isEmpty(systemMetrics)) {
      return StringUtils.EMPTY;
    }
    Map<String /* type */, String /* page */> pages = systemMetrics.get(metric);
    if (CollectionUtils.isEmpty(pages)) {
      return StringUtils.EMPTY;
    }
    String type = isServer ? "server" : "app";
    String page = pages.get(type);
    return domain + page + "&app=" + app;
  }

  private boolean checkIsServer() {
    if (CollectionUtils.isEmpty(this.rule)) {
      return false;
    }
    List<Map<String, Object>> triggers = (List<Map<String, Object>>) this.rule.get("triggers");
    Map<String, Object> trigger = triggers.get(0);
    List<Map<String, Object>> datasources = (List<Map<String, Object>>) trigger.get("datasources");
    if (CollectionUtils.isEmpty(datasources)) {
      return false;
    }
    Map<String, Object> datasource = datasources.get(0);
    List<String> groupBy = (List<String>) datasource.get("groupBy");
    return CollectionUtils.isEmpty(groupBy) || groupBy.contains("hostname")
        || groupBy.contains("pod") || groupBy.contains("ip");
  }

  private String getMetric() {
    if (CollectionUtils.isEmpty(this.rule)) {
      return StringUtils.EMPTY;
    }
    List<Map<String, Object>> triggers = (List<Map<String, Object>>) this.rule.get("triggers");
    if (CollectionUtils.isEmpty(triggers)) {
      return StringUtils.EMPTY;
    }
    Map<String, Object> trigger = triggers.get(0);
    List<Map<String, Object>> datasources = (List<Map<String, Object>>) trigger.get("datasources");
    if (CollectionUtils.isEmpty(datasources)) {
      return StringUtils.EMPTY;
    }
    Map<String, Object> datasource = datasources.get(0);
    return (String) datasource.get("metric");
  }
}
