/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.measure.ActionType;
import io.holoinsight.server.home.web.measure.ResourceType;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jsy1001de
 * @version 1.0: ManageCallback.java, v 0.1 2022年03月15日 12:21 下午 jinsong.yjs Exp $
 */
public interface ManageCallback {
  void checkParameter();

  void doManage();

  default ResourceType getResourceType() {
    return ResourceType.unknown;
  }

  default ActionType getActionType() {
    return ActionType.unknown;
  }

  default String getResourceTenant() {
    MonitorScope ms = RequestContext.getContext().ms;
    if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
      return ms.tenant;
    }
    return StringUtils.EMPTY;
  }
}
