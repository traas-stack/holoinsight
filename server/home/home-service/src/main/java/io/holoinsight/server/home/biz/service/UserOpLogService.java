/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.common.util.page.MonitorTimePageRequest;
import io.holoinsight.server.home.dal.model.UserOpLog;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserOpLogService.java, v 0.1 2022年10月19日 下午6:14 jinsong.yjs Exp $
 */
public interface UserOpLogService extends IService<UserOpLog> {

    UserOpLog queryById(Long id, String tenant);

    UserOpLog create(UserOpLog userOpLog);

    MonitorPageResult<UserOpLog> getListByPage(MonitorTimePageRequest<UserOpLog> userOpLogRequest);

    UserOpLog append(String tableName, String tableEntityId, String opType, String user,
                     String tenant, String before, String after, String relate, String name);
}