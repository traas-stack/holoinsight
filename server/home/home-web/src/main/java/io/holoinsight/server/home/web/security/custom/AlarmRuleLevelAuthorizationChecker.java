/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.mapper.AlertTemplateMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.AlertTemplate;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.AlertRuleExtra;
import io.holoinsight.server.home.facade.AlertSilenceConfig;
import io.holoinsight.server.home.facade.NotificationConfig;
import io.holoinsight.server.home.facade.NotificationTemplate;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.TimeFilter;
import io.holoinsight.server.home.facade.emuns.TimeFilterEnum;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.holoinsight.server.home.facade.utils.ParaCheckUtil.sqlCnNameCheck;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-01-02 11:29:00
 */
@Slf4j
@Service
public class AlarmRuleLevelAuthorizationChecker extends AbstractQueryChecker
    implements AbstractResourceChecker {

  @Autowired
  private RequestContextAdapter requestContextAdapter;
  @Resource
  private AlarmRuleMapper alarmRuleMapper;
  @Resource
  private AlertTemplateMapper alertTemplateMapper;
  @Autowired
  private AlarmHistoryFacadeImplChecker historyFacadeImplChecker;
  private static final Pattern timePattern =
      Pattern.compile("^(?:[01]\\d|2[0-3]):(?:[0-5]\\d):(?:[0-5]\\d)$");

  private static final Pattern downsamplePattern = Pattern.compile("^\\d+[mMhH]$");
  private static final Pattern calculatePattern = Pattern.compile("^[a-zA-Z]([+\\-*/][a-zA-Z])*$");
  private static final Set<String> timeModes =
      new HashSet<>(Arrays.asList(TimeFilterEnum.DAY.getDesc(), TimeFilterEnum.WEEK.getDesc(),
          TimeFilterEnum.MONTH.getDesc()));
  private static final Set<String> ruleTypes = new HashSet<>(Arrays.asList("ai", "rule", "pql"));
  private static final Set<String> alarmLevels =
      new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
  private static final Set<String> sourceTypes =
      new HashSet<>(Arrays.asList("custom", "cloudbase", "log", "hosting_system", "hosting_port",
          "hosting_tbase", "hosting_spanner", "hosting_ob", "hosting_apm_ai", "hosting_spanner_ai",
          "hosting_ob_ai", "hosting_tbase_ai", "hosting_system_ai", "hosting_disk", "iot",
          "miniapp", "antiTemplate", "template", "hosting"));

  private static final Set<String> silenceModes =
      new HashSet<>(Arrays.asList("default", "gradual", "fixed"));
  private static final Set<String> aggregators = new HashSet<>(Arrays.asList("sum", "avg", "mix",
      "max", "count", "none", "SUM", "AVG", "MIX", "MAX", "COUNT", "NONE"));
  private static final Set<String> metricTypes =
      new HashSet<>(Arrays.asList("app", "cache", "log", "oss", "trace", "system", "metric",
          "service", "function", "pg", "mongodb", "db", "miniProgram"));
  private static final Set<String> products = new HashSet<>(Arrays.asList("JVM", "Function",
      "OceanBase", "Tbase", "PortCheck", "System", "MiniProgram", "Spanner", "IoT"));

  @Override
  public LevelAuthorizationCheckResult check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    RequestContext.Context context = RequestContext.getContext();
    String workspace = this.requestContextAdapter.getWorkspaceFromContext(context);
    String tenant = this.requestContextAdapter.getTenantFromContext(context);

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkParameters(String methodName, List<String> parameters,
      String tenant, String workspace) {
    switch (methodName) {
      case "create":
      case "update":
        return checkAlarmRuleDTO(methodName, parameters, tenant, workspace);
      case "queryById":
      case "deleteById":
        return checkIdExists(parameters, tenant, workspace);
      case "queryByIds":
        return checkIdsExists(parameters, tenant, workspace);
      case "pageQuery":
        return checkPageRequest(methodName, parameters, tenant, workspace);
      case "querySubscribeList":
        return checkSelfBool(methodName, parameters, tenant, workspace);
      case "querySubAlarmHistory":
        return checkQuerySubAlarmHistory(methodName, parameters, tenant, workspace);
      default:
        return successCheckResult();
    }
  }

  private LevelAuthorizationCheckResult checkQuerySubAlarmHistory(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (parameters.size() == 2) {
      LevelAuthorizationCheckResult checkResult1 = checkSelfBool(methodName,
          Collections.singletonList(parameters.get(0)), tenant, workspace);
      if (!checkResult1.isSuccess()) {
        return checkResult1;
      }
      LevelAuthorizationCheckResult checkResult2 = historyFacadeImplChecker.checkPageRequest(
          methodName, Collections.singletonList(parameters.get(1)), tenant, workspace);
      if (!checkResult2.isSuccess()) {
        return checkResult2;
      }
    } else if (parameters.size() == 1) {
      LevelAuthorizationCheckResult checkResult2 = historyFacadeImplChecker.checkPageRequest(
          methodName, Collections.singletonList(parameters.get(0)), tenant, workspace);
      if (!checkResult2.isSuccess()) {
        return checkResult2;
      }
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkSelfBool(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters)) {
      return successCheckResult();
    }
    String req = J.fromJson(parameters.get(0), String.class);
    if (StringUtils.equals("true", req) || StringUtils.equals("false", req)) {
      return successCheckResult();
    }
    return failCheckResult("invalid req %s", req);
  }

  private LevelAuthorizationCheckResult checkIdsExists(List<String> parameters, String tenant,
      String workspace) {
    String[] idArray = StringUtils.split(parameters.get(0), ",");
    for (String id : idArray) {
      LevelAuthorizationCheckResult checkResult =
          checkIdExists(Collections.singletonList(id), tenant, workspace);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkPageRequest(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("empty parameters");
    }
    String parameter = parameters.get(0);
    MonitorPageRequest<AlarmRuleDTO> pageRequest =
        J.fromJson(parameter, new TypeToken<MonitorPageRequest<AlarmRuleDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        return failCheckResult("fail to check time range for start %s larger than end %s",
            pageRequest.getFrom(), pageRequest.getTo());
      }
    }

    AlarmRuleDTO target = pageRequest.getTarget();
    if (target == null) {
      return failCheckResult("fail to check target, target can not be null");
    }
    return checkAlarmRuleDTO(methodName, target, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlarmRuleDTO(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlarmRuleDTO alarmRuleDTO = J.fromJson(parameters.get(0), AlarmRuleDTO.class);
    return checkAlarmRuleDTO(methodName, alarmRuleDTO, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlarmRuleDTO(String methodName,
      AlarmRuleDTO alarmRuleDTO, String tenant, String workspace) {
    if (methodName.equals("create")) {
      if (alarmRuleDTO.getId() != null) {
        return failCheckResult("fail to check %s for id is not null", methodName);
      }
      if (alarmRuleDTO.getStatus() == null) {
        return failCheckResult("status can not be empty");
      }
      if (alarmRuleDTO.getIsMerge() == null) {
        return failCheckResult("isMerge can not be empty");
      }
      if (alarmRuleDTO.getRecover() == null) {
        return failCheckResult("recover can not be empty");
      }
    }

    if (methodName.equals("update")) {
      if (alarmRuleDTO.getId() == null) {
        return failCheckResult("fail to check %s for id is null", methodName);
      }
      LevelAuthorizationCheckResult updateCheckResult =
          checkIdExists(alarmRuleDTO.getId(), tenant, workspace);
      if (!updateCheckResult.isSuccess()) {
        return updateCheckResult;
      }
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getRuleName())
        && !checkSqlName(alarmRuleDTO.getRuleName())) {
      return failCheckResult(
          "invalid ruleName %s, please use a-z A-Z 0-9 Chinese - _ , . : spaces ",
          alarmRuleDTO.getRuleName());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getRuleType())
        && !ruleTypes.contains(alarmRuleDTO.getRuleType())) {
      return failCheckResult("invalid rule type %s", alarmRuleDTO.getRuleType());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getAlarmLevel())
        && !alarmLevels.contains(alarmRuleDTO.getAlarmLevel())) {
      return failCheckResult("invalid rule level %s", alarmRuleDTO.getAlarmLevel());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getRuleName())
        && !checkSqlName(alarmRuleDTO.getRuleName())) {
      return failCheckResult("invalid ruleName %s", alarmRuleDTO.getRuleName());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getRuleDescribe())
        && !sqlCnNameCheck(alarmRuleDTO.getRuleDescribe())) {
      return failCheckResult(
          "invalid ruleDescribe %s, please use a-z A-Z 0-9 Chinese - _ , . : spaces ",
          alarmRuleDTO.getRuleDescribe());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getMergeType())) {
      return failCheckResult("mergeType %s should be empty", alarmRuleDTO.getMergeType());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getNoticeType())) {
      return failCheckResult("noticeType %s should be empty", alarmRuleDTO.getMergeType());
    }

    if (!CollectionUtils.isEmpty(alarmRuleDTO.getAlarmContent())) {
      return failCheckResult("alarmContent %s should be empty", alarmRuleDTO.getMergeType());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getTenant())
        && !StringUtils.equals(alarmRuleDTO.getTenant(), tenant)) {
      return failCheckResult("invalid tenant %s for right tenant %s", alarmRuleDTO.getTenant(),
          tenant);
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getWorkspace())
        && !StringUtils.equals(alarmRuleDTO.getWorkspace(), workspace)) {
      return failCheckResult("invalid workspace %s for right workspace %s",
          alarmRuleDTO.getWorkspace(), workspace);
    }

    if (!CollectionUtils.isEmpty(alarmRuleDTO.getRule())) {
      LevelAuthorizationCheckResult ruleCheckResult =
          checkRule(alarmRuleDTO.getRule(), tenant, workspace);
      if (!ruleCheckResult.isSuccess()) {
        return ruleCheckResult;
      }
    }

    if (!CollectionUtils.isEmpty(alarmRuleDTO.getTimeFilter())) {
      LevelAuthorizationCheckResult timeFilterCheckResult =
          checkTimeFilter(alarmRuleDTO.getTimeFilter(), tenant, workspace);
      if (!timeFilterCheckResult.isSuccess()) {
        return timeFilterCheckResult;
      }
    }

    if (alarmRuleDTO.getBlockId() != null) {
      return failCheckResult("blockId %d should be empty", alarmRuleDTO.getBlockId());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getSourceType())
        && !checkSourceType(alarmRuleDTO.getSourceType())) {
      return failCheckResult("invalid source type %s", alarmRuleDTO.getSourceType());
    }

    if (alarmRuleDTO.getExtra() != null) {
      LevelAuthorizationCheckResult checkResult =
          checkExtra(alarmRuleDTO.getExtra(), tenant, workspace);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getEnvType())
        && !checkEnvType(alarmRuleDTO.getEnvType(), workspace)) {
      return failCheckResult("invalid env type %s", alarmRuleDTO.getEnvType());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getPql())) {
      return failCheckResult("pql %s should be empty", alarmRuleDTO.getPql());
    }

    if (alarmRuleDTO.getAlertNotificationTemplateId() != null) {
      return failCheckResult("alertNotificationTemplateId %d should be empty",
          alarmRuleDTO.getAlertNotificationTemplateId());
    }

    if (StringUtils.isNotEmpty(alarmRuleDTO.getAlertTemplateUuid())
        && !checkAlertTemplateUuid(alarmRuleDTO.getAlertTemplateUuid(), tenant, workspace)) {
      return failCheckResult("invalid alertTemplateUuid %s", alarmRuleDTO.getAlertTemplateUuid());
    }

    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkRule(Map<String, Object> ruleMap, String tenant,
      String workspace) {
    Rule rule = J.fromJson(J.toJson(ruleMap), Rule.class);
    if (CollectionUtils.isEmpty(rule.getTriggers())) {
      return successCheckResult();
    }
    for (Trigger trigger : rule.getTriggers()) {
      if (!CollectionUtils.isEmpty(trigger.getDatasources())) {
        LevelAuthorizationCheckResult checkResult =
            checkDatasources(trigger.getDatasources(), tenant, workspace);
        if (!checkResult.isSuccess()) {
          return checkResult;
        }
      }

      if (StringUtils.isNotEmpty(trigger.getQuery()) && !checkQuery(trigger.getQuery())) {
        return failCheckResult("fail to check query %s", trigger.getQuery());
      }

      if (StringUtils.isNotEmpty(trigger.getAggregator())
          && !aggregators.contains(trigger.getAggregator())) {
        return failCheckResult("fail to check aggregator %s", trigger.getAggregator());
      }

      if (StringUtils.isNotEmpty(trigger.getTriggerContent())
          && !sqlCnNameCheck(trigger.getTriggerContent())) {
        return failCheckResult("fail to check triggerContent %s", trigger.getTriggerContent());
      }

      if (!CollectionUtils.isEmpty(trigger.getDataResult())) {
        return failCheckResult("dataResult in trigger should be empty");
      }

      if (StringUtils.isNotEmpty(trigger.getTriggerTitle())
          && !checkSqlName(trigger.getTriggerTitle())) {
        return failCheckResult("fail to check triggerTitle %s", trigger.getTriggerTitle());
      }

      if (!CollectionUtils.isEmpty(trigger.getCompareConfigs())) {
        LevelAuthorizationCheckResult checkResult =
            checkCompareConfigs(trigger.getCompareConfigs());
        if (!checkResult.isSuccess()) {
          return checkResult;
        }
      }

      if (trigger.getRuleConfig() != null && !checkSqlField(trigger.getRuleConfig().keySet())) {
        return failCheckResult("key of ruleConfig %s is invalid",
            J.toJson(trigger.getRuleConfig().keySet()));
      }
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkDatasources(List<DataSource> datasources,
      String tenant, String workspace) {
    for (DataSource dataSource : datasources) {
      if (StringUtils.isNotEmpty(dataSource.getMetricType())
          && !metricTypes.contains(dataSource.getMetricType())) {
        return failCheckResult("invalid metric type %s", dataSource.getMetricType());
      }
      if (StringUtils.isNotEmpty(dataSource.getProduct())
          && !products.contains(dataSource.getProduct())) {
        return failCheckResult("invalid product %s", dataSource.getProduct());
      }
      if (StringUtils.isNotEmpty(dataSource.getMetric())) {
        LevelAuthorizationCheckResult checkResult = checkMetricTableWithAlarmFilter(
            dataSource.getMetric(), tenant, workspace, dataSource.getFilters());
        if (!checkResult.isSuccess()) {
          return checkResult;
        }
      }
      if (!CollectionUtils.isEmpty(dataSource.getGroupBy())) {
        LevelAuthorizationCheckResult checkResult =
            checkGroupBy(dataSource.getMetric(), dataSource.getGroupBy());
        if (!checkResult.isSuccess()) {
          return checkResult;
        }
      }
      if (StringUtils.isNotEmpty(dataSource.getDownsample())
          && !checkDownsample(dataSource.getDownsample())) {
        return failCheckResult("fail to check downsample %s", dataSource.getDownsample());
      }
      if (StringUtils.isNotEmpty(dataSource.getAggregator())
          && !aggregators.contains(dataSource.getAggregator())) {
        return failCheckResult("fail to check aggregator %s", dataSource.getAggregator());
      }
    }
    return successCheckResult();
  }

  private boolean checkDownsample(String downsample) {
    String[] arr = downsample.split("-");
    String timeDownsample = arr[0];
    String agg = null;
    if (arr.length > 1) {
      agg = arr[1];
    }
    if (StringUtils.isNotEmpty(agg) && !aggregators.contains(agg)) {
      return false;
    }
    Matcher matcher = downsamplePattern.matcher(timeDownsample);
    return matcher.matches();
  }

  private LevelAuthorizationCheckResult checkGroupBy(String metric, List<String> groupBys) {
    MetricInfo metricInfo = apiSecurityService.getMetricInfo(metric);
    if (metricInfo == null) {
      return failCheckResult("metricInfo is null for metric %s", metric);
    }
    List<String> tags = J.toList(metricInfo.getTags());
    for (String groupBy : groupBys) {
      if (!tags.contains(groupBy)) {
        return failCheckResult("groupby %s cannot be found in %s", groupBy, metricInfo.getTags());
      }
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkCompareConfigs(List<CompareConfig> compareConfigs) {
    for (CompareConfig config : compareConfigs) {
      if (StringUtils.isNotEmpty(config.getTriggerLevel())
          && !checkSqlName(config.getTriggerLevel())) {
        return failCheckResult("fail to check triggerLevel in compareConfigs %s",
            config.getTriggerLevel());
      }
      if (StringUtils.isNotEmpty(config.getTriggerContent())
          && !checkSqlName(config.getTriggerContent())) {
        return failCheckResult("fail to check triggerContent in compareConfigs %s",
            config.getTriggerContent());
      }
    }
    return successCheckResult();
  }

  protected static boolean checkQuery(String query) {
    Matcher matcher = calculatePattern.matcher(query);
    return matcher.matches();
  }

  private LevelAuthorizationCheckResult checkTimeFilter(Map<String, Object> timeFilter,
      String tenant, String workspace) {
    TimeFilter tf = J.fromJson(J.toJson(timeFilter), TimeFilter.class);
    if (StringUtils.isNotEmpty(tf.getFrom()) && !validTime(tf.getFrom())) {
      return failCheckResult("from %s in timeFilter is invalid", tf.getFrom());
    }
    if (StringUtils.isNotEmpty(tf.getTo()) && !validTime(tf.getTo())) {
      return failCheckResult("to %s in timeFilter is invalid", tf.getTo());
    }
    if (StringUtils.isNotEmpty(tf.getModel()) && !timeModes.contains(tf.getModel())) {
      return failCheckResult("mode %s in timeFilter is invalid", tf.getModel());
    }
    return successCheckResult();
  }

  protected static boolean validTime(String time) {
    Matcher matcher = timePattern.matcher(time);
    return matcher.matches();
  }

  private LevelAuthorizationCheckResult checkExtra(AlertRuleExtra extra, String tenant,
      String workspace) {
    if (StringUtils.isNotEmpty(extra.sourceLink)) {
      return failCheckResult("sourceLink %s should be empty", extra.sourceLink);
    }
    if (StringUtils.isNotEmpty(extra.md5)) {
      return failCheckResult("md5 %s should be empty", extra.md5);
    }
    if (!CollectionUtils.isEmpty(extra.alertTags) && !checkSqlField(extra.alertTags)) {
      return failCheckResult("fail to check sql field in alertTags %s", J.toJson(extra.alertTags));
    }

    if (!CollectionUtils.isEmpty(extra.tagAlias)
        && (!checkSqlField(extra.tagAlias.keySet()) || !checkSqlField(extra.tagAlias.values()))) {
      return failCheckResult("fail to check sql field in tagAlias %s", J.toJson(extra.tagAlias));
    }

    if (extra.notificationConfig != null) {
      LevelAuthorizationCheckResult checkResult = checkNotificationConfig(extra.notificationConfig);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }

    if (extra.alertSilenceConfig != null) {
      LevelAuthorizationCheckResult checkResult = checkAlertSilenceConfig(extra.alertSilenceConfig);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkAlertSilenceConfig(
      AlertSilenceConfig alertSilenceConfig) {
    if (StringUtils.isNotEmpty(alertSilenceConfig.getSilenceMode())
        && !silenceModes.contains(alertSilenceConfig.getSilenceMode())) {
      return failCheckResult("invalid silenceMode %s ", alertSilenceConfig.getSilenceMode());
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkNotificationConfig(
      NotificationConfig notificationConfig) {
    if (notificationConfig.dingtalkTemplate != null) {
      LevelAuthorizationCheckResult checkResult =
          checkNotificationTemplate(notificationConfig.dingtalkTemplate);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }
    if (notificationConfig.webhookTemplate != null) {
      LevelAuthorizationCheckResult checkResult =
          checkNotificationTemplate(notificationConfig.webhookTemplate);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }
    return successCheckResult();
  }

  private LevelAuthorizationCheckResult checkNotificationTemplate(NotificationTemplate template) {
    if (!CollectionUtils.isEmpty(template.fieldMap) && !checkSqlField(template.fieldMap.keySet())) {
      return failCheckResult("invalid sql field in NotificationTemplate fieldMap %s",
          J.toJson(template.fieldMap.keySet()));
    }
    if (!CollectionUtils.isEmpty(template.tagMap)
        && (!checkSqlField(template.tagMap.keySet()) || !checkSqlField(template.tagMap.values()))) {
      return failCheckResult("fail to check sql field in NotificationTemplate tagMap %s",
          J.toJson(template.tagMap));
    }
    if (StringUtils.isNotEmpty(template.text)
        && !StringUtils.equals(template.text, template.getTemplateJson())) {
      return failCheckResult("text %s in template not equal to fieldMap", template.text);
    }
    return successCheckResult();
  }

  private boolean checkSqlField(Collection<String> collection) {
    for (String item : collection) {
      if (!checkSqlField(item)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkEnvType(String envType, String workspace) {
    String[] arr = envType.split("_");
    if (arr.length != 2) {
      return false;
    }
    return StringUtils.equals(String.join("__", arr[1], arr[0]), workspace);
  }


  private boolean checkSourceType(String sourceType) {
    return checkSqlField(sourceType)
        && (sourceTypes.contains(sourceType) || sourceType.startsWith("apm_"));
  }

  private boolean checkAlertTemplateUuid(String alertTemplateUuid, String tenant,
      String workspace) {
    QueryWrapper<AlertTemplate> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("uuid", alertTemplateUuid);
    queryWrapper.eq("tenant", tenant);
    queryWrapper.eq("workspace", workspace);
    return !CollectionUtils.isEmpty(this.alertTemplateMapper.selectList(queryWrapper));
  }

  @Override
  public LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);
    List<AlarmRule> exist = this.alarmRuleMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      return failCheckResult("fail to check id for no existed %s %s %s", id, tenant, workspace);
    }
    return successCheckResult();
  }
}
