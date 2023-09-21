/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/6/9 9:38 下午
 */
public interface AlarmHistoryDetailService extends IService<AlarmHistoryDetail> {


  MonitorPageResult<AlarmHistoryDetailDTO> getListByPage(
      MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest);

  List<Map<String, Object>> count(MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest);

}
