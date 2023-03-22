/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.home.dal.model.AlarmBlock;
import io.holoinsight.server.home.dal.model.dto.AlarmBlockDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/6/15 4:56 下午
 */
public interface AlertBlockService extends IService<AlarmBlock> {

  Long save(AlarmBlockDTO AlarmBlockDTO);

  Boolean updateById(AlarmBlockDTO AlarmBlockDTO);

  AlarmBlockDTO queryById(Long id, String tenant, String workspace);

  MonitorPageResult<AlarmBlockDTO> getListByPage(MonitorPageRequest<AlarmBlockDTO> pageRequest);

  List<AlarmBlockDTO> getListByKeyword(String keyword, String tenant, String workspace);
}
