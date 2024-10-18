/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.dao.entity.AlarmHistory;
import io.holoinsight.server.common.dao.entity.AlarmHistoryDetail;
import io.holoinsight.server.common.dao.entity.dto.AlarmHistoryDetailDTO;

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

  List<AlarmHistoryDetail> queryByTime(long from, long to);

}
