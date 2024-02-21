/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.common;

import com.google.common.collect.Maps;
import io.holoinsight.server.common.J;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.core.service.bitmap.condition.AndCondition;
import io.holoinsight.server.meta.core.service.bitmap.condition.MetaCondition;
import io.holoinsight.server.meta.core.service.bitmap.condition.OrCondition;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.holoinsight.server.meta.common.util.ConstModel.default_app;
import static io.holoinsight.server.meta.common.util.ConstModel.default_hostname;
import static io.holoinsight.server.meta.common.util.ConstModel.default_ip;

/**
 * @author jinyan.ljw
 * @Description TODO
 * @date 2023/5/20
 */
public class FilterUtil {


  public static final String REGEX_FILTERS_KEY = "regexFilters";
  public static final String EQ_FILTERS_KEY = "eqFilters";
  public static final String IN_FILTERS_KEY = "inFilters";

  public static MetaCondition buildDimCondition(QueryExample queryExample,
      Boolean containRegexFilters) {
    Map<String, Map<String, Object>> filters = buildFilters(queryExample, containRegexFilters);
    MetaCondition metaCondition = new MetaCondition();
    OrCondition orCondition = metaCondition.or();
    if (CollectionUtils.isEmpty(filters)) {
      AndCondition andCondition = orCondition.and();
      andCondition.setAll(true);
    } else {
      filters.forEach((type, entries) -> {
        // type = type.contains(".") ? type.split("\\.")[0] : type;
        if (REGEX_FILTERS_KEY.equals(type)) {
          entries.forEach((k, v) -> {
            AndCondition andCondition = orCondition.and();
            andCondition.setRegex(true);
            andCondition.setExpress(new String[] {k});
            andCondition.setValueRange(Collections.singletonList(v));
          });
        } else if (EQ_FILTERS_KEY.equals(type)) {
          entries.forEach((k, v) -> {
            AndCondition andCondition = orCondition.and();
            andCondition.setExpress(new String[] {k});
            andCondition.setValueRange(Collections.singletonList(v));
          });
        } else if (IN_FILTERS_KEY.equals(type)) {
          entries.forEach((k, v) -> {
            AndCondition andCondition = orCondition.and();
            andCondition.setExpress(new String[] {k});
            andCondition.setValueRange((List) v);
          });
        } else {
          throw new IllegalArgumentException("unsupported filter type: " + type);
        }
      });
    }
    return metaCondition;
  }

  public static Map<String, Map<String, Object>> buildFilters(QueryExample queryExample,
      Boolean containRegexFilters) {
    Map<String, Map<String, Object>> filters = Maps.newHashMap();
    if (CollectionUtils.isEmpty(queryExample.getParams())) {
      return filters;
    }
    for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {
      Object value = entry.getValue();
      if (Objects.isNull(value)) {
        continue;
      }
      String key = entry.getKey();
      if (containRegexFilters
          && (key.equals(default_ip) || key.equals(default_hostname) || key.equals(default_app))) {
        Map<String, Object> regexFilters =
            filters.computeIfAbsent(REGEX_FILTERS_KEY, k -> Maps.newHashMap());
        Pattern pattern = J.json2Bean(J.toJson(value), Pattern.class);
        regexFilters.put(key, pattern);
        continue;
      }
      if (value instanceof String) {
        Map<String, Object> eqFilters =
            filters.computeIfAbsent(EQ_FILTERS_KEY, k -> Maps.newHashMap());
        eqFilters.put(key, value.toString());
      } else if (value instanceof List) {
        if (CollectionUtils.isEmpty((List) value)) {
          continue;
        }
        Map<String, Object> inFilters =
            filters.computeIfAbsent(IN_FILTERS_KEY, k -> Maps.newHashMap());
        inFilters.put(key, value);
      }
    }
    return filters;
  }


  public static <R> List<R> filterData(Collection<Map<String, Object>> items,
      Map<String, Map<String, Object>> filters, Function<Map, R> func) {
    Map<String, Object> eqFilters = filters.get(EQ_FILTERS_KEY);
    Stream<Map<String, Object>> dataStream = items.stream();
    if (eqFilters != null) {
      dataStream = dataStream.filter(item -> {
        for (Entry<String, Object> eqItem : eqFilters.entrySet()) {
          String eqItemKey = eqItem.getKey();
          Object realVal = getRealVal(eqItemKey, item);
          Object eqItemValue = eqItem.getValue();
          if (!Objects.equals(realVal, eqItemValue)) {
            return false;
          }
        }
        return true;
      });
    }
    Map<String, Object> inFilters = filters.get(IN_FILTERS_KEY);
    if (inFilters != null) {
      dataStream = dataStream.filter(item -> {
        for (Entry<String, Object> inItem : inFilters.entrySet()) {
          String inItemKey = inItem.getKey();
          Object realVal = getRealVal(inItemKey, item);
          List<String> inItemValue = (List<String>) inItem.getValue();
          if (Objects.isNull(realVal) || !inItemValue.contains(realVal)) {
            return false;
          }
        }
        return true;
      });
    }
    Map<String, Object> regexFilters = filters.get(REGEX_FILTERS_KEY);
    if (regexFilters != null) {
      dataStream = dataStream.filter(item -> {
        for (Entry<String, Object> regexItem : regexFilters.entrySet()) {
          String regexItemKey = regexItem.getKey();
          Object realVal = getRealVal(regexItemKey, item);
          Pattern pattern = (Pattern) regexItem.getValue();
          if (Objects.isNull(realVal) || !pattern.matcher(realVal.toString()).matches()) {
            return false;
          }
        }
        return true;
      });
    }
    return dataStream.map(func).collect(Collectors.toList());
  }


  private static Object getRealVal(String key, Map<String, Object> map) {
    if (key.contains(".")) {
      int doxIndex = key.indexOf('.');
      String firstKey = key.substring(0, doxIndex);
      String secondKey = key.substring(doxIndex + 1);
      Object value0 = map.get(firstKey);
      if (Objects.isNull(value0)) {
        return null;
      }
      Map<String, Object> keyItem = (Map<String, Object>) value0;
      return keyItem.get(secondKey);
    } else {
      return map.get(key);
    }
  }

  public static List<String> genCartesianStrList(List<List<String>> wordsList) {
    List<String> cartesianStrList = wordsList.get(0);
    for (int i = 1; i < wordsList.size(); i++) {
      List<String> secondList = wordsList.get(i);
      cartesianStrList = cartesianStrList.stream()
          .flatMap(s1 -> secondList.stream().map(s2 -> s1 + ":" + s2)).collect(Collectors.toList());
    }
    return cartesianStrList;
  }
}
