/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.web.controller.model.open.GrafanaJsonResult;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

/**
 * @author jsy1001de
 * @version 1.0: ManageTemplateImpl.java, v 0.1 2022年03月15日 12:23 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class FacadeTemplateImpl implements FacadeTemplate {

    @Override
    @SuppressWarnings("unchecked")
    public void manage(JsonResult result, ManageCallback callback) {
        //检验参数
        callback.checkParameter();
        //执行管理方法
        callback.doManage();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void manage(JsonResult result, ManageCallback callback, String trace) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            //检验参数
            callback.checkParameter();
            //执行管理方法
            callback.doManage();

        } finally {
            stopWatch.stop();
            log.info(trace + ", clientResult=[" + result.isSuccess() + "], clientCost=[" + stopWatch.getTime() + "]");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void manage(GrafanaJsonResult result, ManageCallback callback) {
        //检验参数
        callback.checkParameter();
        //执行管理方法
        callback.doManage();

    }
}