/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.common;

import io.holoinsight.server.common.J;
import org.bson.Document;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DocumentUtil.java, v 0.1 2022年03月07日 9:19 下午 jinsong.yjs Exp $
 */
public class DocumentUtil {

  public static List<Document> parseList(List<Map<String, Object>> rows) {
    List<Document> list = new ArrayList<>();
    rows.forEach(row -> list.add(Document.parse(J.toJson(row))));
    return list;
  }

  public static List<Map<String, Object>> toMapList(List<Document> documents) {
    List<Map<String, Object>> mapList = new ArrayList<>();

    if (CollectionUtils.isEmpty(documents)) {
      return mapList;
    }

    for (Document document : documents) {
      mapList.add(new HashMap<String, Object>(document));
    }

    return mapList;
  }
}
