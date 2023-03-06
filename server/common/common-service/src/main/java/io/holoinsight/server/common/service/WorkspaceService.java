/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.dao.entity.Workspace;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: WorkspaceService.java, v 0.1 2023年03月03日 下午5:58 jsy1001de Exp $
 */
public interface WorkspaceService extends IService<Workspace> {
  List<Workspace> getByTenant(String tenant);
}
