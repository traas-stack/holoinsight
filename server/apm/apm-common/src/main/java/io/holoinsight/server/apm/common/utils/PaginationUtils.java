/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.utils;

import io.holoinsight.server.apm.common.model.query.Pagination;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author jiwliu
 * @version : PaginationUtils.java, v 0.1 2022年09月21日 11:35 xiangwanpeng Exp $
 */
public class PaginationUtils {

  public static Page exchange(Pagination paging) {
    int limit = paging.getPageSize();
    int from = paging.getPageSize() * ((paging.getPageNum() == 0 ? 1 : paging.getPageNum()) - 1);

    return new Page(from, limit);
  }

  @Data
  @RequiredArgsConstructor
  public static class Page {
    private final int from;
    private final int limit;
  }
}
