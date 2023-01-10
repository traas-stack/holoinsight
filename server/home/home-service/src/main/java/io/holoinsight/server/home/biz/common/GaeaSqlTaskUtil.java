/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.biz.common;

import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.Metric;
import io.holoinsight.server.home.dal.model.dto.conf.ColumnCalExpr;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SplitCol;
import io.holoinsight.server.home.dal.model.dto.conf.Filter;
import io.holoinsight.server.home.dal.model.dto.conf.LogParse;
import io.holoinsight.server.home.dal.model.dto.conf.LogPath;
import io.holoinsight.server.home.dal.model.dto.conf.MultiLine;
import io.holoinsight.server.home.dal.model.dto.conf.Rule;
import io.holoinsight.server.home.dal.model.dto.conf.Translate;
import io.holoinsight.server.registry.model.Elect;
import io.holoinsight.server.registry.model.Elect.RefIndex;
import io.holoinsight.server.registry.model.Elect.RefName;
import io.holoinsight.server.registry.model.Elect.Transform;
import io.holoinsight.server.registry.model.Elect.TransformItem;
import io.holoinsight.server.registry.model.ExecuteRule;
import io.holoinsight.server.registry.model.From;
import io.holoinsight.server.registry.model.From.Log;
import io.holoinsight.server.registry.model.From.Log.Multiline;
import io.holoinsight.server.registry.model.From.Log.Parse;
import io.holoinsight.server.registry.model.From.Log.Separator;
import io.holoinsight.server.registry.model.GroupBy;
import io.holoinsight.server.registry.model.Output;
import io.holoinsight.server.registry.model.Output.Gateway;
import io.holoinsight.server.registry.model.Select;
import io.holoinsight.server.registry.model.Select.SelectItem;
import io.holoinsight.server.registry.model.TimeParse;
import io.holoinsight.server.registry.model.Where;
import io.holoinsight.server.registry.model.Where.ContainsAny;
import io.holoinsight.server.registry.model.Where.NotIn;
import io.holoinsight.server.registry.model.Where.Regexp;
import io.holoinsight.server.registry.model.Window;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaSqlTaskUtil.java, v 0.1 2022年11月21日 下午9:39 jinsong.yjs Exp $
 */
public class GaeaSqlTaskUtil {

    private static final String contains   = "CONTAINS";
    private static final String leftRight  = "LR";
    private static final String separator  = "SEP";
    private static final String regexp     = "REGEXP";
    private static final String dimColType = "DIM";
    private static final String valColType = "VALUE";

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
                                     Map<String, Map<String, SplitCol>> splitColMap,
                                     CollectMetric collectMetric) {

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
                            if(StringUtils.isNotEmpty(rule.regexpName)){
                                elect.setType("refName");
                                elect.setRefName(new RefName());
                                elect.getRefName().setName(rule.regexpName);
                            }else if(rule.pos != null){
                                elect.setType("refIndex");
                                elect.setRefIndex(new RefIndex());
                                elect.getRefIndex().setIndex(rule.pos);
                            }
                            break;
                        default:
                            break;
                    }

                    if (null != rule.translate) {
                        Translate translate = rule.translate;
                        List<ColumnCalExpr> exprs = translate.exprs;

                        Transform transform = new Transform();
                        List<TransformItem> transformItems = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(exprs)) {
                            exprs.forEach(expr -> {
                                TransformItem transformItem = new TransformItem();
                                transformItem.setArg(expr.getParams());
                                transformItem.setFunc(expr.getFunc());
                                transformItems.add(transformItem);
                            });
                        }

                        transform.setTransforms(transformItems);
                        elect.setTransform(transform);
                    }

                    if (StringUtils.isNotEmpty(rule.defaultValue)) {
                        elect.setDefaultValue(rule.defaultValue);
                    }
                    selectItem.setElect(elect);
                }
            }

            selectItem.setAgg(m.func);
            selectItem.setAs(m.name);

            select.getValues().add(selectItem);
        });

        return select;
    }

    public static From buildFrom(List<LogPath> logPaths, LogParse logParse, List<Filter> whiteFilters, List<Filter> blackFilters) {

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
                    default:
                        parse.setType("none");
                }
            }

            if (null != logParse.getMultiLine() && logParse.getMultiLine().multi) {
                parse.setMultiline(new Multiline());
                parse.getMultiline().setWhat(
                        logParse.getMultiLine().lineType.equalsIgnoreCase("logHead") ? "previous"
                                : "next");

                Regexp regexp = new Regexp();
                regexp.setExpression(logParse.getMultiLine().logRegexp);
                regexp.setElect(new Elect());
                regexp.getElect().setType("line");

                Where where = new Where();
                where.setRegexp(regexp);
                parse.getMultiline().setMatch(where);

            }

            parse.setWhere(buildFrontFilterWhere(whiteFilters, blackFilters, logParse.splitType));

            TimeParse timeParse = new TimeParse();
            timeParse.setType("auto");

            fromLog.setTime(timeParse);

            List<Log.LogPath> pathList = new ArrayList<>();
            logPaths.forEach(logPath -> {
                Log.LogPath path = new Log.LogPath();
                path.setPattern(logPath.path);
                path.setType(logPath.type);
                path.setDir(logPath.dir);
                pathList.add(path);
            });
            fromLog.setPath(pathList);
            fromLog.setCharset(logPaths.get(0).charset);
            fromLog.setParse(parse);

            MultiLine multiLine = logParse.multiLine;
            if (null != multiLine && null != multiLine.multi && multiLine.multi) {
                Multiline logMultiLine = new Multiline();

                if (multiLine.lineType.equalsIgnoreCase("logHead")) {
                    logMultiLine.setWhat("next");
                } else {
                    logMultiLine.setWhat("previous");
                }
                Where match = new Where();
                Regexp regexp = new Regexp();
                regexp.setElect(new Elect());
                regexp.getElect().setType("line");
                match.setRegexp(regexp);
                regexp.setExpression(multiLine.logRegexp);
                logMultiLine.setMatch(match);
                fromLog.setMultiline(logMultiLine);
            }

        }

        From from = new From();
        from.setType("log");
        from.setLog(fromLog);
        return from;
    }

    // 前置过滤
    public static Where buildFrontFilterWhere(List<Filter> whiteFilters, List<Filter> blackFilters, String splitType) {


        Where where = new Where();
        List<Where> ands = new ArrayList<>();
        if (!CollectionUtils.isEmpty(whiteFilters)) {
            whiteFilters.forEach(w -> {

                Where and = new Where();
                Elect elect = new Elect();
                switch (w.type) {
                    case leftRight:
                        Elect.LeftRight leftRight = new Elect.LeftRight();
                        leftRight.setLeft(w.rule.left);
                        leftRight.setLeftIndex(w.rule.leftIndex);
                        leftRight.setRight(w.rule.right);

                        elect.setType("leftRight");
                        elect.setLeftRight(leftRight);

                        Where.In in = new Where.In();
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

                    default:
                        break;
                }

                ands.add(and);
            });
        }

        if (!CollectionUtils.isEmpty(blackFilters)) {
            blackFilters.forEach(w -> {
                Where and = new Where();

                Elect elect = new Elect();
                switch (splitType) {
                    case leftRight:
                        Elect.LeftRight leftRight = new Elect.LeftRight();
                        leftRight.setLeft(w.rule.left);
                        leftRight.setLeftIndex(w.rule.leftIndex);
                        leftRight.setRight(w.rule.right);
                        elect.setType("leftRight");
                        elect.setLeftRight(leftRight);
                        NotIn notIn = new NotIn();
                        notIn.setValues(w.values);
                        notIn.setElect(elect);

                        and.setNotIn(notIn);
                        break;
                    case contains:
                        ContainsAny contains = new ContainsAny();
                        elect.setType("line");
                        contains.setElect(elect);
                        contains.setValues(w.values);

                        Where not = new Where();
                        not.setContainsAny(contains);

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

    public static Where buildWhere(LogParse logParse, Map<String, Map<String, SplitCol>> splitColMap,
                             CollectMetric collectMetric) {

        //List<String> tags = collectMetric.tags;
        //
        Where where = new Where();
        //List<Where> ands = new ArrayList<>();
        //tags.forEach(t -> {
        //
        //    SplitCol dim = splitColMap.get(dimColType).get(t);
        //    if (null == dim) {
        //        return;
        //    }
        //    Rule rule = dim.rule;
        //    Where and = new Where();
        //    Elect elect = new Elect();
        //    switch (logParse.splitType) {
        //        case leftRight:
        //            Elect.LeftRight leftRight = new Elect.LeftRight();
        //            leftRight.setLeft(rule.left);
        //            leftRight.setLeftIndex(rule.leftIndex);
        //            leftRight.setRight(rule.right);
        //
        //            elect.setType("leftRight");
        //            elect.setLeftRight(leftRight);
        //            break;
        //
        //        case separator:
        //            elect.setType("refIndex");
        //            elect.setRefIndex(new RefIndex());
        //            elect.getRefIndex().setIndex(rule.pos);
        //            break;
        //        case regexp:
        //            elect.setType("refName");
        //            elect.setRefName(new RefName());
        //            elect.getRefName().setName(rule.regexpName);
        //            break;
        //        default:
        //            break;
        //    }
        //
        //    if (null != rule.translate) {
        //        Translate translate = rule.translate;
        //        List<ColumnCalExpr> exprs = translate.exprs;
        //
        //        Transform transform = new Transform();
        //        List<Elect.TransformItem> transformItems = new ArrayList<>();
        //        if (!CollectionUtils.isEmpty(exprs)) {
        //            exprs.forEach(expr -> {
        //                Elect.TransformItem transformItem = new TransformItem();
        //                transformItem.setArg(expr.getParams());
        //                transformItem.setFunc(expr.getFunc());
        //                transformItems.add(transformItem);
        //            });
        //        }
        //
        //        transform.setTransforms(transformItems);
        //        elect.setTransform(transform);
        //    }
        //
        //    if (!StringUtils.isEmpty(rule.defaultValue)) {
        //        elect.setDefaultValue(rule.defaultValue);
        //    }
        //
        //    Where.In in = new Where.In();
        //    in.setValues(t.values);
        //    in.setElect(elect);
        //    and.setIn(in);
        //
        //    ands.add(and);
        //});
        //
        //if (collectMetric.metricType.equalsIgnoreCase("contains")) {
        //    Where and = new Where();
        //    Elect elect = new Elect();
        //    Contains contains = new Contains();
        //    elect.setType("line");
        //    contains.setElect(elect);
        //    contains.setValue(collectMetric.containValue);
        //    and.setContains(contains);
        //}
        //
        //where.setAnd(ands);
        return where;
    }

    public static GroupBy buildGroupBy(LogParse logParse, Map<String, Map<String, SplitCol>> splitColMap,
                                 CollectMetric collectMetric) {

        List<String> tags = collectMetric.getTags();

        GroupBy groupBy = new GroupBy();
        groupBy.setGroups(new ArrayList<>());
        if (CollectionUtils.isEmpty(tags)) {
            return groupBy;
        }
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
                    if(StringUtils.isNotEmpty(rule.regexpName)){
                        elect.setType("refName");
                        elect.setRefName(new RefName());
                        elect.getRefName().setName(rule.regexpName);
                    }else if(rule.pos != null){
                        elect.setType("refIndex");
                        elect.setRefIndex(new RefIndex());
                        elect.getRefIndex().setIndex(rule.pos);
                    }
                    break;
                default:
                    break;
            }

            if (null != rule.translate) {
                Translate translate = rule.translate;
                List<ColumnCalExpr> exprs = translate.exprs;

                Transform transform = new Transform();
                List<TransformItem> transformItems = new ArrayList<>();
                if (!CollectionUtils.isEmpty(exprs)) {
                    exprs.forEach(expr -> {
                        TransformItem transformItem = new TransformItem();
                        transformItem.setArg(expr.getParams());
                        transformItem.setFunc(expr.getFunc());
                        transformItems.add(transformItem);
                    });
                }

                transform.setTransforms(transformItems);
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
            groupBy.setMaxKeys(logParse.maxKeySize);
            groupBy.getGroups().add(group);
        });

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
}