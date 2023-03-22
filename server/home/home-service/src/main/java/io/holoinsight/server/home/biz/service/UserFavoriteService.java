/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.dal.model.UserFavorite;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserFavoriteService.java, v 0.1 2022年03月21日 2:40 下午 jinsong.yjs Exp $
 */
public interface UserFavoriteService extends IService<UserFavorite> {

  UserFavorite queryById(Long id, String tenant, String workspace);

  List<UserFavorite> getByUser(String userLoginName);

  List<UserFavorite> getByUserAndTenant(String userLoginName, String tenant, String workspace);

  List<UserFavorite> getByUserAndTenantAndRelateId(String userLoginName, String tenant,
      String workspace, String relateId, String type);

  List<UserFavorite> getByUserAndTenantAndRelateIds(String userLoginName, String tenant,
      String workspace, List<String> relateIds, String type);

  UserFavorite create(UserFavorite userFavorite);

  void deleteById(Long id);

  void update(UserFavorite userFavorite);

  MonitorPageResult<UserFavorite> getListByPage(
      MonitorPageRequest<UserFavorite> userFavoriteRequest);

}
