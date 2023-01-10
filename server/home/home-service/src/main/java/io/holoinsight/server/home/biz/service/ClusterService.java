/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.dto.ClusterDTO;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterDTOService.java, v 0.1 2022年03月17日 5:45 下午 jinsong.yjs Exp $
 */
public interface ClusterService {

  void upsert(ClusterDTO cluster);

  List<ClusterDTO> getClusterAliveSortedByRole(String role);

  boolean checkBrain(List<ClusterDTO> clusters);

}
