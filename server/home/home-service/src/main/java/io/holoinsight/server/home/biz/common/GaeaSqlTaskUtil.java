/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;

import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.model.dto.conf.*;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.AfterFilter;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.LogSampleRule;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.Metric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.ExtraConfig;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SplitCol;
import io.holoinsight.server.home.dal.model.dto.conf.Translate.TranslateTransform;
import io.holoinsight.server.registry.model.Elect;
import io.holoinsight.server.registry.model.Elect.RefIndex;
import io.holoinsight.server.registry.model.Elect.RefMeta;
import io.holoinsight.server.registry.model.Elect.RefName;
import io.holoinsight.server.registry.model.Elect.TransFormFilter;
import io.holoinsight.server.registry.model.Elect.TransFormFilterAppend;
import io.holoinsight.server.registry.model.Elect.TransFormFilterConst;
import io.holoinsight.server.registry.model.Elect.TransFormFilterMapping;
import io.holoinsight.server.registry.model.Elect.TransFormFilterRegexpReplace;
import io.holoinsight.server.registry.model.Elect.TransFormFilterSubstring;
import io.holoinsight.server.registry.model.Elect.TransFormFilterSwitch;
import io.holoinsight.server.registry.model.Elect.TransFormFilterSwitchCase;
import io.holoinsight.server.registry.model.Elect.Transform;
import io.holoinsight.server.registry.model.ExecuteRule;
import io.holoinsight.server.registry.model.From;
import io.holoinsight.server.registry.model.From.Log;
import io.holoinsight.server.registry.model.From.Log.Multiline;
import io.holoinsight.server.registry.model.From.Log.Parse;
import io.holoinsight.server.registry.model.From.Log.Separator;
import io.holoinsight.server.registry.model.GroupBy;
import io.holoinsight.server.registry.model.GroupBy.LogAnalysis;
import io.holoinsight.server.registry.model.GroupBy.LogAnalysisPattern;
import io.holoinsight.server.registry.model.Output;
import io.holoinsight.server.registry.model.Output.Gateway;
import io.holoinsight.server.registry.model.Select;
import io.holoinsight.server.registry.model.Select.LogSamples;
import io.holoinsight.server.registry.model.Select.SelectItem;
import io.holoinsight.server.registry.model.TimeParse;
import io.holoinsight.server.registry.model.Where;
import io.holoinsight.server.registry.model.Where.Contains;
import io.holoinsight.server.registry.model.Where.ContainsAny;
import io.holoinsight.server.registry.model.Where.In;
import io.holoinsight.server.registry.model.Where.NumberOp;
import io.holoinsight.server.registry.model.Where.Regexp;
import io.holoinsight.server.registry.model.Window;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaSqlTaskUtil.java, v 0.1 2022年11月21日 下午9:39 jinsong.yjs Exp $
 */
public class GaeaSqlTaskUtil {

  private static final String contains = "CONTAINS";
  private static final String leftRight = "LR";
  private static final String separator = "SEP";
  private static final String regexp = "REGEXP";
  private static final String json = "JSON";
  private static final String sls = "sls";
  private static final String dimColType = "DIM";
  private static final String valColType = "VALUE";
  private static final String extendColType = "EXTEND";

  private static List<String> timeZones =
      Arrays.asList("UTC", "Asia/Shanghai", "America/Los_Angeles");

  public static Map<String, Map<String, SplitCol>> convertSplitColMap(List<SplitCol> splitCols) {
    Map<String, Map<String, SplitCol>> splitColMap = new HashMap<>();
    if (CollectionUtils.isEmpty(splitCols))
      return splitColMap;
    splitCols.forEach(splitCol -> {
      if (!splitColMap.containsKey(splitCol.getColType())) {
        splitColMap.put(splitCol.getColType(), new HashMap<>());
      }

      Map<String, SplitCol> stringSplitColMap = splitColMap.get(splitCol.getColType());
      if (!stringSplitColMap.containsKey(splitCol.name)) {
        stringSplitColMap.put(splitCol.name, splitCol);
      }
    });

    return splitColMap;
  }


  public static Select buildSelect(LogParse logParse,
      Map<String, Map<String, SplitCol>> splitColMap, CollectMetric collectMetric) {

    List<Metric> metrics = collectMetric.getMetrics();

    Select select = new Select();
    metrics.forEach(m -> {
      SelectItem selectItem = new SelectItem();

      if (collectMetric.getMetricType().equalsIgnoreCase("select")) {

        SplitCol value = splitColMap.get(valColType).get(m.name);
        if (null == value) {
          return;
        }
        Rule rule = value.rule;

        if (null != rule) {
          Elect elect = buildElect(rule, logParse.splitType);
          selectItem.setElect(elect);
        }
      }

      selectItem.setAgg(m.func);
      selectItem.setAs(m.name);

      select.getValues().add(selectItem);
    });

    if (collectMetric.checkLogSample()) {
      LogSamples logSamples = new LogSamples();
      logSamples.setEnabled(true);
      logSamples.setMaxCount(collectMetric.getSampleMaxCount());
      logSamples.setMaxLength(collectMetric.getSampleMaxLength());

      if (!CollectionUtils.isEmpty(collectMetric.getLogSampleRules())) {
        logSamples.setWhere(
            buildSampleWhere(logParse.splitType, splitColMap, collectMetric.getLogSampleRules()));
      }
      select.setLogSamples(logSamples);
    }

    return select;
  }

  public static From buildFrom(List<LogPath> logPaths, LogParse logParse, ExtraConfig extraConfig,
      List<Filter> whiteFilters, List<Filter> blackFilters, List<SplitCol> splitCols) {

    Log fromLog = new Log();
    {
      Parse parse = new Parse();

      if (StringUtil.isBlank(logParse.splitType)) {
        parse.setType("none");
      } else {
        switch (logParse.splitType) {
          case leftRight:
            parse.setType("none");
            break;
          case separator:
            parse.setType("separator");
            parse.setSeparator(new Separator());
            parse.getSeparator().setSeparator(logParse.separator.separatorPoint);
            break;
          case regexp:
            parse.setType("regexp");
            parse.setRegexp(new Log.Regexp());
            parse.getRegexp().setExpression(logParse.regexp.expression);
            break;
          case json:
            parse.setType("json");
            break;
          default:
            parse.setType("none");
        }
      }

      if (null != logParse.getMultiLine() && logParse.getMultiLine().multi) {
        Multiline logMultiLine = new Multiline();
        logMultiLine.setEnabled(true);
        parse.setMultiline(logMultiLine);
        parse.getMultiline().setWhat(
            logParse.getMultiLine().lineType.equalsIgnoreCase("logHead") ? "previous" : "next");

        Regexp regexp = new Regexp();
        regexp.setExpression(logParse.getMultiLine().logRegexp);
        regexp.setElect(new Elect());
        regexp.getElect().setType("line");

        Where not = new Where();
        not.setRegexp(regexp);

        Where where = new Where();
        where.setNot(not);

        parse.getMultiline().setWhere(where);

      }

      parse.setWhere(buildFrontFilterWhere(whiteFilters, blackFilters));

      List<Log.LogPath> pathList = new ArrayList<>();
      if (!CollectionUtils.isEmpty(logPaths)) {
        logPaths.forEach(logPath -> {
          Log.LogPath path = new Log.LogPath();
          if (StringUtils.isNotBlank(logPath.path) && logPath.path.equalsIgnoreCase(sls)) {
            path.setType("sls");
            pathList.add(path);
            return;
          }

          path.setPattern(logPath.path);
          path.setType(logPath.type);
          path.setDir(logPath.dir);
          pathList.add(path);
        });
      }

      fromLog.setPath(pathList);
      fromLog.setCharset(extraConfig.getCharset());
      fromLog.setParse(parse);

      MultiLine multiLine = logParse.multiLine;
      if (null != multiLine && null != multiLine.multi && multiLine.multi) {
        Multiline logMultiLine = new Multiline();
        logMultiLine.setEnabled(true);
        if (multiLine.lineType.equalsIgnoreCase("logHead")) {
          logMultiLine.setWhat("previous");
        } else {
          logMultiLine.setWhat("next");
        }

        Regexp regexp = new Regexp();
        regexp.setElect(new Elect());
        regexp.getElect().setType("line");

        Where not = new Where();
        not.setRegexp(regexp);

        Where where = new Where();
        where.setNot(not);

        regexp.setExpression(multiLine.logRegexp);
        logMultiLine.setWhere(where);
        fromLog.setMultiline(logMultiLine);
      }

      TimeParse timeParse = new TimeParse();
      timeParse.setType("auto");
      fromLog.setTime(timeParse);

      boolean jsonTimeSelect = false;
      if (!CollectionUtils.isEmpty(splitCols)) {
        for (CustomPluginConf.SplitCol splitCol : splitCols) {
          if ("TIME".equals(splitCol.colType)) {
            timeParse.setFormat("auto");
            timeParse.setElect(buildElect(splitCol.rule, logParse.splitType));
            timeParse.setType("elect");
            if (!StringUtils.isEmpty(splitCol.name)) {
              String timeAndZone = splitCol.name.substring(3, splitCol.name.length() - 1);
              String[] cols = timeAndZone.split("##");
              if (cols.length != 2) {
                continue;
              }
              if (timeZones.contains(cols[1])) {
                timeParse.setTimezone(cols[1]);
              } else if (!Constants.DEFAULT_CH.equals(cols[1])) {
                continue;
              }
              String layout = convertTimeLayout(cols[0]);
              if (StringUtils.isEmpty(layout)) {
                continue;
              }
              timeParse.setLayout(layout);
              timeParse.setFormat("golangLayout");
            }
            jsonTimeSelect = true;
          }
        }
        fromLog.setTime(timeParse);
      }
      if (StringUtils.isNotBlank(logParse.splitType) && logParse.splitType.equalsIgnoreCase(json)
          && !jsonTimeSelect) {
        timeParse.setType("processTime");
        fromLog.setTime(timeParse);
      }
    }

    From from = new From();
    from.setType("log");
    from.setLog(fromLog);
    return from;
  }

  // 前置过滤
  public static Where buildFrontFilterWhere(List<Filter> whiteFilters, List<Filter> blackFilters) {


    Where where = new Where();
    List<Where> ands = new ArrayList<>();
    if (!CollectionUtils.isEmpty(whiteFilters)) {
      whiteFilters.forEach(w -> {

        Where and = new Where();
        Elect elect = new Elect();
        Where.In in = new Where.In();
        switch (w.type) {
          case leftRight:
            Elect.LeftRight leftRight = new Elect.LeftRight();
            leftRight.setLeft(w.rule.left);
            leftRight.setLeftIndex(w.rule.leftIndex);
            leftRight.setRight(w.rule.right);

            elect.setType("leftRight");
            elect.setLeftRight(leftRight);

            in.setValues(w.values);
            in.setElect(elect);
            and.setIn(in);
            break;

          case contains:
            ContainsAny contains = new ContainsAny();
            elect.setType("line");
            contains.setElect(elect);
            contains.setValues(w.values);
            and.setContainsAny(contains);
            break;

          case json:
            Elect.RefName refName = new Elect.RefName();
            refName.setName(w.rule.jsonPathSyntax);
            elect.setRefName(refName);
            elect.setType("refName");

            in.setValues(w.values);
            in.setElect(elect);
            and.setIn(in);
            break;

          default:
            break;
        }

        ands.add(and);
      });
    }

    if (!CollectionUtils.isEmpty(blackFilters)) {
      blackFilters.forEach(w -> {
        Where and = new Where();
        Where not = new Where();
        Where.In in = new Where.In();

        Elect elect = new Elect();
        switch (w.getType()) {
          case leftRight:
            Elect.LeftRight leftRight = new Elect.LeftRight();
            leftRight.setLeft(w.rule.left);
            leftRight.setLeftIndex(w.rule.leftIndex);
            leftRight.setRight(w.rule.right);
            elect.setType("leftRight");
            elect.setLeftRight(leftRight);
            in.setValues(w.values);
            in.setElect(elect);

            not.setIn(in);
            and.setNot(not);
            break;
          case contains:
            ContainsAny contains = new ContainsAny();
            elect.setType("line");
            contains.setElect(elect);
            contains.setValues(w.values);

            not.setContainsAny(contains);
            and.setNot(not);
            break;
          case json:
            Elect.RefName refName = new Elect.RefName();
            refName.setName(w.rule.jsonPathSyntax);
            elect.setType("refName");
            elect.setRefName(refName);

            in.setValues(w.values);
            in.setElect(elect);

            not.setIn(in);
            and.setNot(not);
            break;
          default:
            break;
        }

        ands.add(and);
      });
    }

    where.setAnd(ands);
    return where;
  }

  public static Where buildSampleWhere(String splitType,
      Map<String, Map<String, SplitCol>> splitColMap, List<LogSampleRule> logSampleRules) {

    Where where = new Where();
    List<Where> ands = new ArrayList<>();

    for (LogSampleRule logSampleRule : logSampleRules) {
      Map<String, SplitCol> colMap = splitColMap.get(dimColType);
      if (logSampleRule.keyType.equals(valColType)) {
        colMap = splitColMap.get(valColType);
      }
      SplitCol splitCol = colMap.get(logSampleRule.getName());

      Rule rule = splitCol.rule;
      Elect elect = new Elect();

      switch (splitType) {
        case leftRight:
          Elect.LeftRight leftRight = new Elect.LeftRight();
          leftRight.setLeft(rule.left);
          leftRight.setLeftIndex(rule.leftIndex);
          leftRight.setRight(rule.right);

          elect.setType("leftRight");
          elect.setLeftRight(leftRight);
          break;

        case separator:
          elect.setType("refIndex");
          elect.setRefIndex(new RefIndex());
          elect.getRefIndex().setIndex(rule.pos);
          break;
        case regexp:
          if (StringUtils.isNotEmpty(rule.regexpName)) {
            elect.setType("refName");
            elect.setRefName(new RefName());
            elect.getRefName().setName(rule.regexpName);
          } else if (rule.pos != null) {
            elect.setType("refIndex");
            elect.setRefIndex(new RefIndex());
            elect.getRefIndex().setIndex(rule.pos);
          }
          break;
        case json:
          elect.setType("refName");
          elect.setRefName(new RefName());
          elect.getRefName().setName(rule.jsonPathSyntax);
          break;
        default:
          break;
      }

      Where and = new Where();

      Where.In in = new In();
      Where.NumberOp numberOp = new NumberOp();

      switch (logSampleRule.filterType) {
        case IN:
          in.setElect(elect);
          in.setValues(logSampleRule.getValues());
          and.setIn(in);
          break;
        case NOT_IN:
          Where not = new Where();
          in.setValues(logSampleRule.getValues());
          in.setElect(elect);
          not.setIn(in);
          and.setNot(not);
          break;
        case GT:
          numberOp.setElect(elect);
          numberOp.setGt(logSampleRule.getValue());
          and.setNumberOp(numberOp);
          break;
        case GTE:
          numberOp.setElect(elect);
          numberOp.setGte(logSampleRule.getValue());
          and.setNumberOp(numberOp);
          break;
        case LT:
          numberOp.setElect(elect);
          numberOp.setLt(logSampleRule.getValue());
          and.setNumberOp(numberOp);
          break;
        case LTE:
          numberOp.setElect(elect);
          numberOp.setLte(logSampleRule.getValue());
          and.setNumberOp(numberOp);
          break;
        default:
          break;
      }
      ands.add(and);
    }
    where.setAnd(ands);
    return where;
  }

  public static Where buildWhere(LogParse logParse, Map<String, Map<String, SplitCol>> splitColMap,
      CollectMetric collectMetric) {

    Where where = new Where();
    List<Where> ands = new ArrayList<>();

    Map<String, SplitCol> dimColMap = splitColMap.get(dimColType);
    if (!CollectionUtils.isEmpty(collectMetric.afterFilters)
        && !CollectionUtils.isEmpty(dimColMap)) {
      for (AfterFilter afterFilter : collectMetric.afterFilters) {
        if (!dimColMap.containsKey(afterFilter.getName()))
          continue;
        SplitCol dim = dimColMap.get(afterFilter.getName());

        Rule rule = dim.rule;
        Elect elect = new Elect();

        switch (logParse.splitType) {
          case leftRight:
            Elect.LeftRight leftRight = new Elect.LeftRight();
            leftRight.setLeft(rule.left);
            leftRight.setLeftIndex(rule.leftIndex);
            leftRight.setRight(rule.right);

            elect.setType("leftRight");
            elect.setLeftRight(leftRight);
            break;

          case separator:
            elect.setType("refIndex");
            elect.setRefIndex(new RefIndex());
            elect.getRefIndex().setIndex(rule.pos);
            break;
          case regexp:
            if (StringUtils.isNotEmpty(rule.regexpName)) {
              elect.setType("refName");
              elect.setRefName(new RefName());
              elect.getRefName().setName(rule.regexpName);
            } else if (rule.pos != null) {
              elect.setType("refIndex");
              elect.setRefIndex(new RefIndex());
              elect.getRefIndex().setIndex(rule.pos);
            }
            break;
          case json:
            elect.setType("refName");
            elect.setRefName(new RefName());
            elect.getRefName().setName(rule.jsonPathSyntax);
            break;
          default:
            break;
        }

        if (null != rule.translate && !CollectionUtils.isEmpty(rule.translate.transforms)) {
          Transform transform = new Transform();
          transform.setFilters(convertTransFormFilters(rule.translate.transforms));
          elect.setTransform(transform);
        }

        if (StringUtils.isNotEmpty(rule.defaultValue)) {
          elect.setDefaultValue(rule.defaultValue);
        }

        Where and = new Where();
        Where.In in = new In();

        switch (afterFilter.filterType) {
          case IN:
            in.setElect(elect);
            in.setValues(afterFilter.getValues());
            and.setIn(in);
            break;
          case NOT_IN:
            Where not = new Where();
            in.setValues(afterFilter.getValues());
            in.setElect(elect);
            not.setIn(in);
            and.setNot(not);
            break;
          default:
            break;
        }
        ands.add(and);
      }
    }

    if (StringUtils.isNotBlank(collectMetric.metricType)
        && collectMetric.metricType.equalsIgnoreCase("contains")) {
      Where and = new Where();
      Elect elect = new Elect();
      Contains contains = new Contains();
      elect.setType("line");
      contains.setElect(elect);
      contains.setValue(collectMetric.containValue);
      and.setContains(contains);
      ands.add(and);
    }
    where.setAnd(ands);
    return where;
  }

  public static GroupBy buildLogPatternParse(LogParse logParse, ExtraConfig extraConfig) {
    GroupBy groupBy = new GroupBy();
    groupBy.setMaxKeySize(extraConfig.getMaxKeySize());

    LogPattern logPattern = logParse.pattern;

    LogAnalysis logAnalysis = new LogAnalysis();
    logAnalysis.setMaxLogLength(logPattern.getMaxLogLength());
    logAnalysis.setMaxUnknownPatterns(logPattern.getMaxUnknownPatterns());
    List<LogAnalysisPattern> logAnalysisPatterns = new ArrayList<>();
    if (!CollectionUtils.isEmpty(logPattern.getLogKnownPatterns())) {
      logPattern.getLogKnownPatterns().forEach(logKnownPattern -> {
        if (CollectionUtils.isEmpty(logKnownPattern.values))
          return;
        LogAnalysisPattern logAnalysisPattern = new LogAnalysisPattern();
        logAnalysisPattern.setName(logKnownPattern.eventName);
        Where where = new Where();
        Elect elect = new Elect();
        switch (logKnownPattern.type) {
          case leftRight:
            Elect.LeftRight leftRight = new Elect.LeftRight();
            leftRight.setLeft(logKnownPattern.rule.left);
            leftRight.setLeftIndex(logKnownPattern.rule.leftIndex);
            leftRight.setRight(logKnownPattern.rule.right);

            elect.setType("leftRight");
            elect.setLeftRight(leftRight);

            Where.In in = new Where.In();
            in.setValues(logKnownPattern.values);
            in.setElect(elect);
            where.setIn(in);
            break;

          case contains:
            ContainsAny contains = new ContainsAny();
            elect.setType("line");
            contains.setElect(elect);
            contains.setValues(logKnownPattern.values);
            where.setContainsAny(contains);
            break;
          case regexp:
            List<Where> orList = new ArrayList<>();
            logKnownPattern.values.forEach(v -> {
              Regexp regexp = new Regexp();
              elect.setType("line");
              regexp.setElect(elect);
              regexp.setExpression(v);

              Where or = new Where();
              or.setRegexp(regexp);
              orList.add(or);
            });
            where.setOr(orList);
            break;
          default:
            break;
        }
        logAnalysisPattern.setWhere(where);
        logAnalysisPatterns.add(logAnalysisPattern);
      });
    }
    logAnalysis.setPatterns(logAnalysisPatterns);
    groupBy.setLogAnalysis(logAnalysis);
    return groupBy;
  }

  public static GroupBy buildGroupBy(LogParse logParse, ExtraConfig extraConfig,
      Map<String, Map<String, SplitCol>> splitColMap, CollectMetric collectMetric) {

    if (logParse.checkIsPattern() && collectMetric.checkLogPattern()) {
      return buildLogPatternParse(logParse, extraConfig);
    }
    List<String> tags = collectMetric.getTags();
    List<String> refTags = collectMetric.getRefTags();

    GroupBy groupBy = new GroupBy();
    groupBy.setGroups(new ArrayList<>());
    if (CollectionUtils.isEmpty(tags) && CollectionUtils.isEmpty(refTags)) {
      return groupBy;
    }
    if (!CollectionUtils.isEmpty(tags)) {
      tags.forEach(t -> {

        SplitCol dim = splitColMap.get(dimColType).get(t);
        if (null == dim) {
          return;
        }
        Rule rule = dim.rule;

        Elect elect = new Elect();

        switch (logParse.splitType) {
          case leftRight:
            Elect.LeftRight leftRight = new Elect.LeftRight();
            leftRight.setLeft(rule.left);
            leftRight.setLeftIndex(rule.leftIndex);
            leftRight.setRight(rule.right);

            elect.setType("leftRight");
            elect.setLeftRight(leftRight);
            break;

          case separator:
            elect.setType("refIndex");
            elect.setRefIndex(new RefIndex());
            elect.getRefIndex().setIndex(rule.pos);
            break;
          case regexp:
            if (StringUtils.isNotEmpty(rule.regexpName)) {
              elect.setType("refName");
              elect.setRefName(new RefName());
              elect.getRefName().setName(rule.regexpName);
            } else if (rule.pos != null) {
              elect.setType("refIndex");
              elect.setRefIndex(new RefIndex());
              elect.getRefIndex().setIndex(rule.pos);
            }
            break;
          case json:
            elect.setType("refName");
            elect.setRefName(new RefName());
            elect.getRefName().setName(rule.jsonPathSyntax);
            break;
          default:
            break;
        }

        if (null != rule.translate && !CollectionUtils.isEmpty(rule.translate.transforms)) {
          Transform transform = new Transform();
          transform.setFilters(convertTransFormFilters(rule.translate.transforms));
          elect.setTransform(transform);
        }

        if (StringUtils.isNotEmpty(rule.defaultValue)) {
          elect.setDefaultValue(rule.defaultValue);
        }

        GroupBy.Group group = new GroupBy.Group();
        {
          group.setName(t);
          group.setElect(elect);
        }
        groupBy.setMaxKeySize(extraConfig.getMaxKeySize());
        groupBy.getGroups().add(group);
      });
    }

    if (!CollectionUtils.isEmpty(refTags)) {
      refTags.forEach(t -> {

        Elect elect = new Elect();
        elect.setType("refMeta");
        elect.setRefMeta(new RefMeta());
        elect.getRefMeta().setName(t);

        GroupBy.Group group = new GroupBy.Group();
        {
          group.setName(t);
          group.setElect(elect);
        }
        groupBy.setMaxKeySize(extraConfig.getMaxKeySize());
        groupBy.getGroups().add(group);
      });
    }

    Map<String, SplitCol> extendColTypes = splitColMap.get(extendColType);
    if (!CollectionUtils.isEmpty(extendColTypes)) {
      extendColTypes.values().forEach(splitCol -> {
        GroupBy.Group group = new GroupBy.Group();
        {
          group.setName(splitCol.name);
          Elect elect = new Elect();
          elect.setType("refName");
          elect.setRefName(new RefName());
          elect.getRefName().setName(splitCol.rule.refName);
          group.setElect(elect);
        }
        groupBy.getGroups().add(group);
      });
    }

    return groupBy;
  }

  public static Window buildWindow(int dataUnitMs) {

    Window window = new Window();
    window.setInterval(dataUnitMs);
    return window;
  }

  public static Output buildOutput(String targetTableName) {
    Output output = new Output();
    output.setType("gateway");
    output.setGateway(new Gateway());
    output.getGateway().setMetricName(targetTableName);
    return output;
  }

  public static ExecuteRule buildExecuteRule() {
    return new ExecuteRule();
  }

  public static Map<String, Object> convertExecutorSelector() {

    Map<String, Object> executorSelector = new HashMap<>();
    executorSelector.put("type", "sidecar");
    executorSelector.put("sidecar", new HashMap<>());

    return executorSelector;
  }

  public static Elect buildElect(Rule rule, String splitType) {
    Elect elect = new Elect();
    switch (splitType) {
      case leftRight:
        Elect.LeftRight leftRight = new Elect.LeftRight();
        leftRight.setLeft(rule.left);
        leftRight.setLeftIndex(rule.leftIndex);
        leftRight.setRight(rule.right);
        elect.setType("leftRight");
        elect.setLeftRight(leftRight);
        break;
      case separator:
        elect.setType("refIndex");
        elect.setRefIndex(new RefIndex());
        elect.getRefIndex().setIndex(rule.pos);
        break;
      case regexp:
        if (StringUtils.isNotEmpty(rule.regexpName)) {
          elect.setType("refName");
          elect.setRefName(new RefName());
          elect.getRefName().setName(rule.regexpName);
        } else if (rule.pos != null) {
          elect.setType("refIndex");
          elect.setRefIndex(new RefIndex());
          elect.getRefIndex().setIndex(rule.pos);
        }
        break;
      case json:
        elect.setType("refName");
        elect.setRefName(new RefName());
        elect.getRefName().setName(rule.jsonPathSyntax);
        break;
      default:
        break;
    }
    if (null != rule.translate && !CollectionUtils.isEmpty(rule.translate.transforms)) {
      Transform transform = new Transform();
      transform.setFilters(convertTransFormFilters(rule.translate.transforms));
      elect.setTransform(transform);
    }
    if (StringUtils.isNotEmpty(rule.defaultValue)) {
      elect.setDefaultValue(rule.defaultValue);
    }
    return elect;
  }

  private static List<TransFormFilter> convertTransFormFilters(
      List<TranslateTransform> transforms) {
    List<TransFormFilter> filters = new ArrayList<>();
    transforms.forEach(transform -> {
      TransFormFilter transFormFilter = new TransFormFilter();

      switch (transform.getType().toLowerCase()) {
        case "append":
          if (StringUtils.isBlank(transform.getDefaultValue())) {
            break;
          }
          TransFormFilterAppend append = new TransFormFilterAppend();
          append.setValue(transform.getDefaultValue());
          append.setAppendIfMissing(false);
          transFormFilter.setAppendV1(append);
          break;
        case "substring":
          if (StringUtils.isBlank(transform.getDefaultValue())) {
            break;
          }
          String[] array = StringUtils.split(transform.getDefaultValue(), ",");
          if (array.length < 2) {
            break;
          }
          TransFormFilterSubstring substring = new TransFormFilterSubstring();
          substring.setBegin(Integer.parseInt(array[0]));
          substring.setEnd(Integer.parseInt(array[1]));
          substring.setEmptyIfError(true);
          transFormFilter.setSubstringV1(substring);
          break;
        case "mapping":
          if (CollectionUtils.isEmpty(transform.getMappings())) {
            break;
          }
          TransFormFilterMapping mapping = new TransFormFilterMapping();
          mapping.setMappings(transform.getMappings());
          mapping.setDefaultValue(transform.getDefaultValue());
          transFormFilter.setMappingV1(mapping);
          break;
        case "const":
          TransFormFilterConst aConst = new TransFormFilterConst();
          aConst.setValue(transform.getDefaultValue());
          transFormFilter.setConstV1(aConst);
          break;
        case "contains":
        case "regexp":
          if (CollectionUtils.isEmpty(transform.getMappings())) {
            break;
          }

          if (transform.getMappings().size() == 1 && transform.getType().equalsIgnoreCase("regexp")
              && StringUtils.isBlank(transform.getDefaultValue())) {
            TransFormFilterRegexpReplace regexpReplace = new TransFormFilterRegexpReplace();
            Map<String, String> mappings = transform.getMappings();
            regexpReplace.setExpression(mappings.keySet().iterator().next());
            regexpReplace.setReplacement(mappings.values().iterator().next());
            transFormFilter.setRegexpReplaceV1(regexpReplace);
            break;
          }

          TransFormFilterSwitch aSwitch = new TransFormFilterSwitch(); {
          TransFormFilterConst defaultConst = new TransFormFilterConst();
          defaultConst.setValue(transform.getDefaultValue());
          TransFormFilter defaultFilter = new TransFormFilter();
          defaultFilter.setConstV1(defaultConst);
          aSwitch.setDefaultAction(defaultFilter);
        }
          List<TransFormFilterSwitchCase> switchCases = new ArrayList<>();

          transform.getMappings().forEach((k, v) -> {
            TransFormFilterSwitchCase switchCase = new TransFormFilterSwitchCase();
            TransFormFilter filter = new TransFormFilter();

            Where caseWhere = new Where();
            if (transform.getType().equalsIgnoreCase("regexp")) {
              Regexp regexp = new Regexp();
              regexp.setExpression(k);
              regexp.setCatchGroups(true);
              caseWhere.setRegexp(regexp);
              TransFormFilterRegexpReplace regexpReplace = new TransFormFilterRegexpReplace();
              regexpReplace.setReplacement(v);
              filter.setRegexpReplaceV1(regexpReplace);
            } else if (transform.getType().equalsIgnoreCase("contains")) {
              Contains contains = new Contains();
              contains.setValue(k);
              caseWhere.setContains(contains);
              TransFormFilterConst vConst = new TransFormFilterConst();
              vConst.setValue(v);
              filter.setConstV1(vConst);
            }

            switchCase.setCaseWhere(caseWhere);
            switchCase.setAction(filter);
            switchCases.add(switchCase);
          });

          aSwitch.setCases(switchCases);
          transFormFilter.setSwitchCaseV1(aSwitch);
          break;
      }

      filters.add(transFormFilter);
    });
    return filters;
  }

  public static String convertTimeLayout(String time) {

    Map<String, String> logTimeLayoutMap = MetaDictUtil.getLogTimeLayoutMap();
    if (!CollectionUtils.isEmpty(logTimeLayoutMap) && logTimeLayoutMap.containsKey(time)) {
      return logTimeLayoutMap.get(time);
    }

    switch (time) {
      case "yyyy-MM-dd HH:mm:ss":
        return "2006-01-02 15:04:05";
      case "yyyy-MM-ddTHH:mm:ss":
        return "2006-01-02T15:04:05";
      case "dd/MMM/yyyy:HH:mm:ss Z":
        return "02/Jan/2006:15:04:05 Z0700";
      case "MM-dd-yyyy HH:mm:ss":
        return "01-02-2006 15:04:05";
      case "MMM dd yyyy HH:mm:ss":
        return "Jan 02 2006 15:04:05";
      default:
        return StringUtils.EMPTY;
    }
  }

}
