/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.page;

import io.holoinsight.server.common.service.Measurable;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: PageRequest.java, v 0.1 2022年03月21日 3:11 下午 jinsong.yjs Exp $
 */
@Data
public class MonitorPageResult<T> implements Serializable, Measurable {
  private static final long serialVersionUID = -8352196951709213541L;

  private int pageNum = 1;

  private int pageSize = 10;

  private long totalCount;
  private long totalPage;

  private List<T> items;

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(items)) {
      return 0;
    }
    long size = 0;
    for (T item : items) {
      if (item instanceof Measurable) {
        size += ((Measurable) item).measure();
      } else {
        size += 1;
      }
    }
    return size;
  }
}
