/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.page;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: PageRequest.java, v 0.1 2022年03月21日 3:11 下午 jinsong.yjs Exp $
 */
@Data
public class MonitorPageRequest<T> implements Serializable {
  private static final long serialVersionUID = -8352196951709213541L;

  private int pageNum = 1;

  private int pageSize = 10;

  private T target;
  // 时间范围
  private Long from;
  private Long to;

  public <K> MonitorPageRequest<K> cloneRequest() {
    MonitorPageRequest<K> request = new MonitorPageRequest<>();
    request.setPageSize(getPageSize());
    request.setPageNum(getPageNum());
    request.setFrom(getFrom());
    request.setTo(getTo());
    return request;
  }

}
