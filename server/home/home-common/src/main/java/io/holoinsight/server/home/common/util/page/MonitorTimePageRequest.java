/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.page;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorTimePageRequest.java, v 0.1 2022年03月29日 12:26 下午 jinsong.yjs Exp $
 */
@Data
public class MonitorTimePageRequest<T> implements Serializable {
  private static final long serialVersionUID = -551884919124077414L;
  private Long startTime;
  private Long endTime;

  private T target;

  private int pageNum = 1;

  private int pageSize = 10;

}
