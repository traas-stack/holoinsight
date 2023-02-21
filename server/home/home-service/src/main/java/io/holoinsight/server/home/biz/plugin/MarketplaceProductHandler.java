/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import io.holoinsight.server.home.dal.model.dto.MarketplacePluginDTO;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;

/**
 * @author masaimu
 * @version 2023-02-20 17:54:00
 */
public interface MarketplaceProductHandler {

  /**
   * install marketplace product
   * 
   * @param byId marketplace product
   * @return installed marketplace plugin
   */
  MarketplacePluginDTO install(MarketplaceProductDTO byId);

  /**
   * uninstall marketplace product
   * 
   * @param byId uninstalled marketplace product
   * @return result
   */
  Boolean uninstall(MarketplacePluginDTO byId);
}
