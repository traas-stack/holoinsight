/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiwliu
 * @version : Tag.java, v 0.1 2022年09月17日 15:50 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {

  private static final long serialVersionUID = 336711058457422187L;
  private String key;
  private String value;

  @Override
  public String toString() {
    return key + "=" + value;
  }

  public static class Util {
    public static List<String> toStringList(List<Tag> list) {
      if (CollectionUtils.isEmpty(list)) {
        return Collections.emptyList();
      }
      return list.stream().map(Tag::toString).collect(Collectors.toList());
    }
  }
}
