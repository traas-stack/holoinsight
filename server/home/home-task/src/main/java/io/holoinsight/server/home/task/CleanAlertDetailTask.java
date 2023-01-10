/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task;

import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.MetaDataDictValueService;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryDetailMapper;
import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author masaimu
 * @version 2022-12-05 14:12:00
 */
@Service
@TaskHandler(TaskEnum.CLEAN_ALERT_HISTORY)
public class CleanAlertDetailTask extends AbstractMonitorTask {

    private static Logger LOGGER = LoggerFactory.getLogger(CleanAlertDetailTask.class);

    @Resource
    private AlarmHistoryDetailMapper detailMapper;
    @Autowired
    private MetaDataDictValueService metaDataDictValueService;

    public CleanAlertDetailTask() {
        super(1, 10, TaskEnum.CLEAN_ALERT_HISTORY);
    }

    private void doClean() {
        Date date = null;
        int deleteSum = 0;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -7);
            date = c.getTime();
            QueryWrapper<AlarmHistoryDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.lt("gmt_create", date);
            Page<AlarmHistoryDetail> page = new Page();

            page.setSize(getPageSize());
            long current = 0;
            boolean keepDelete = true;

            while (keepDelete) {
                page.setCurrent(current);
                Page<AlarmHistoryDetail> pageRes = detailMapper.selectPage(page, detailWrapper);
                if (pageRes == null || CollectionUtils.isEmpty(pageRes.getRecords())) {
                    LOGGER.info("finish clean alert history detail.");
                    keepDelete = false;
                } else {
                    List<Long> ids = pageRes.getRecords().stream().map(AlarmHistoryDetail::getId).collect(Collectors.toList());
                    int deleteCount = detailMapper.deleteBatchIds(ids);
                    LOGGER.info("delete current {} count {}", current, deleteCount);
                    deleteSum += deleteCount;
                    current++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("[CleanAlertDetailTask] fail_to_delete_alarm_detail before {} sum {} for {}", date.toString(), deleteSum, e.getMessage(), e);
        } finally {
            LOGGER.info("[CleanAlertDetailTask] delete_alarm_detail before {} sum {}", date.toString(), deleteSum);
        }
    }

    private long getPageSize() {
        long pageSize = 1000;
        String value = MetaDictUtil.getStringValue("clean_task", "alert_detail_page_size");
        if(StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)){
            pageSize = Long.parseLong(value);
        }
        return pageSize;
    }

    @Override
    public boolean needRun() {
        String value = MetaDictUtil.getStringValue("clean_task", "alert_detail_enable");
        return StringUtils.isNotEmpty(value) && value.equals("true");
    }

    @Override
    public long getTaskPeriod() {
        return 6 * FIVE_SECOND;
    }

    @Override
    public List<MonitorTaskJob> buildJobs(long period) throws Throwable {
        MonitorTaskJob job = new MonitorTaskJob() {
            @Override
            public boolean run() throws Throwable {
                doClean();
                return true;
            }

            @Override
            public String id() {
                return "CleanAlertDetailTask";
            }
        };
        return Arrays.asList(job);
    }
}
