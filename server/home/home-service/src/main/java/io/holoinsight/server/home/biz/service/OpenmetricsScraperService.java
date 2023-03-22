/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.OpenmetricsScraper;
import io.holoinsight.server.home.dal.model.dto.OpenmetricsScraperDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OpenmetricsScraperService extends IService<OpenmetricsScraper> {

  OpenmetricsScraperDTO queryById(Long id, String tenant, String workspace);

  void saveByDTO(OpenmetricsScraperDTO openmetricsScraperDTO);

  void deleteById(Long id);

  OpenmetricsScraperDTO toDTO(OpenmetricsScraper model);

  OpenmetricsScraper toDO(OpenmetricsScraperDTO dto);

  MonitorPageResult<OpenmetricsScraperDTO> getListByPage(
      MonitorPageRequest<OpenmetricsScraperDTO> request);
}
