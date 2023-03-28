/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.Folder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderService.java, v 0.1 2022年05月23日 8:31 下午 jinsong.yjs Exp $
 */
public interface FolderService extends IService<Folder> {

  List<Folder> getListByKeyword(String keyword, String tenant, String workspace);

  List<Folder> getListByNameLike(String name, String tenant, String workspace);

  List<Folder> findByIds(List<String> ids);

  Folder queryById(Long id, String tenant, String workspace);

  Folder queryById(Long id, String tenant);

  Long create(Folder folder);

}
