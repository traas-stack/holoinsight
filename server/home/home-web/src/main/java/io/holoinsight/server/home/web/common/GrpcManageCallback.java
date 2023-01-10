/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

/**
 *
 * @author jsy1001de
 * @version 1.0: GrpcManageCallback.java, v 0.1 2022年06月15日 4:18 下午 jinsong.yjs Exp $
 */
public interface GrpcManageCallback {

  void checkParameter();

  void doManage();

}
