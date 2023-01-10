/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import io.holoinsight.server.home.common.util.MonitorException;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetContextLogsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zanghaibo
 * @time 2022-08-09 9:08 下午
 */

@Service
@Slf4j
public class MiniProgramLogService {

    @Autowired
    private SlsLogService slsLogService;

    @Autowired
    private AntLogService antLogService;

    /**
     * 查询日志总量
     * @param request
     * @return
     * @throws LogException
     * @throws URISyntaxException
     * @throws HttpException
     */
    public MiniProgramLogCountQueryResponse queryMiniProgramLogCount(String appId, String envId, MiniProgramLogCountQueryRequest request) throws LogException, URISyntaxException, HttpException {
        String app = AntLogUtils.buildAntLogInfoApp(appId, envId, request.getServiceId());

        AntLogInfo info = null;
        String logPath = "";

        if (StringUtils.isEmpty(request.getLogPath()) || request.getLogPath().equals(LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT)) {
            logPath = LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT;
            info = antLogService.queryStdoutLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);
        } else {
            logPath = request.getLogPath();
            info = antLogService.queryCustomLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);
        }

        if (info == null) {
            throw new MonitorException("queryMiniProgramLogCount can not get log info from :" + app);
        }

        String query = AntLogUtils.buildQuery(request.getQuery(), request.getInstances(), logPath);
        query = query + LogConstants.SLS_QUERY_COUNT;
        GetLogsResponse slsLogResponse = slsLogService.getGetLogsResponse(info.getProjectName(), info.getLogstoreName(), request.getFrom(), request.getTo(), query, 0, 100, false);
        return convert(slsLogResponse, appId, envId, request);
    }

    /**
     * 查询sls日志
     * @param request
     * @return
     * @throws LogException
     * @throws URISyntaxException
     * @throws HttpException
     */
    public MiniProgramLogQueryResponse queryMiniProgramLog(String appId, String envId, MiniProgramLogQueryRequest request) throws LogException, URISyntaxException, HttpException {
        String app = AntLogUtils.buildAntLogInfoApp(appId, envId, request.getServiceId());
        AntLogInfo info = null;
        String logPath = "";

        if (StringUtils.isEmpty(request.getLogPath()) || request.getLogPath().equals(LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT)) {
            logPath = LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT;
            info = antLogService.queryStdoutLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);
        } else {
            logPath = request.getLogPath();
            info = antLogService.queryCustomLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);
        }

        if (info == null) {
            throw new MonitorException("queryMiniProgramLog can not get log info from :" + app);
        }

        String query = AntLogUtils.buildQuery(request.getQuery(), request.getInstances(), logPath);
        query = query + LogConstants.SLS_QUERY_META;
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        Integer offset = 0;
        if (pageNum != null && pageSize != null && pageNum >= 1) {
            offset = (pageNum-1) * pageSize;
        }
        GetLogsResponse slsLogResponse = slsLogService.getGetLogsResponse(info.getProjectName(), info.getLogstoreName(), request.getFrom(), request.getTo(), query, offset, request.getPageSize(), request.isReverse());
        return convert(slsLogResponse, appId, envId, request);
    }

    /**
     * 查询日志上下文
     * @param request
     * @return
     * @throws LogException
     * @throws URISyntaxException
     * @throws HttpException
     */
    public MiniProgramLogContextQueryResponse queryMiniProgramLogContext(String appId, String envId, MiniProgramLogContextQueryRequest request) throws LogException, URISyntaxException, HttpException {
        String app = AntLogUtils.buildAntLogInfoApp(appId, envId, request.getServiceId());

        AntLogInfo info = null;
        if (StringUtils.isEmpty(request.getLogPath()) || request.getLogPath().equals(LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT)) {
            info = antLogService.queryStdoutLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);
        } else {
            info = antLogService.queryCustomLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);
        }

        if (info == null) {
            throw new MonitorException("queryMiniProgramLogContext can not get log info from :" + app);
        }

        GetContextLogsResponse slsLogContextResponse = slsLogService.getSlsContextLogs(info.getProjectName(), info.getLogstoreName(), request.getPackID(), request.getPackMeta(), request.getBackLines(), request.getForwardLines());
        return convert(slsLogContextResponse, appId, envId, request);
    }

    /**
     * 查询某服务下的日志路径列表
     * @param appId
     * @param envId
     * @param serviceId
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public List<String> queryMiniProgramLogPath(String appId, String envId, String serviceId, Integer from, Integer to) throws URISyntaxException, HttpException, LogException {
        String app = AntLogUtils.buildAntLogInfoApp(appId, envId, serviceId);
        AntLogInfo logInfo = antLogService.queryCustomLogInfo(LogConstants.MINI_PROGRAM_LOG_REGION, app);

        // 这里小程序云默认开启了__tag__:__path__作为日志索引
        String query = "* | select distinct \"__tag__:__path__\"";
        Integer offset = 0;
        GetLogsResponse slsLogResponse = slsLogService.getGetLogsResponse(logInfo.getProjectName(), logInfo.getLogstoreName(), from, to, query, offset, 100, false);

        return distinctLogPaths(slsLogResponse);
    }

    public List<String> distinctLogPaths(GetLogsResponse source) {

        // 默认添加stdout日志路径
        Set<String> paths = new HashSet<>();
        paths.add(LogConstants.MINI_PROGRAM_LOG_PATH_STDOUT);
        if (CollectionUtils.isEmpty(source.getLogs())) {
            return new ArrayList<>(paths);
        }

        // 添加无盘采集到的日志路径
        for (QueriedLog log : source.getLogs()) {
            for (LogContent logContent : log.mLogItem.mContents) {
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PATH)) {
                    paths.add(logContent.mValue);
                }
            }
        }

        //日志路径默认使用自然序
        List<String> orderPaths = new ArrayList<>(paths);
        orderPaths.sort(Comparator.naturalOrder());

        return orderPaths;
    }

    public MiniProgramLogCountQueryResponse convert(GetLogsResponse source, String appId, String envId, MiniProgramLogCountQueryRequest request) {
        MiniProgramLogCountQueryResponse dest = new MiniProgramLogCountQueryResponse();
        dest.setAppId(appId);
        dest.setEnvId(envId);
        dest.setServiceId(request.getServiceId());
        dest.setStart(request.getFrom());
        dest.setEnd(request.getTo());
        dest.setCount(source.getProcessedRow());
        return dest;
    }

    public static MiniProgramLogContextQueryResponse convert(GetContextLogsResponse source, String appId, String envId, MiniProgramLogContextQueryRequest request) {
        MiniProgramLogContextQueryResponse dest = new MiniProgramLogContextQueryResponse();
        dest.setCompleted(source.isCompleted());
        dest.setBackLines(source.getBackLines());
        dest.setForwardLines(source.getForwardLines());
        dest.setCount(source.getTotalLines());
        ArrayList<SlsLog> slsLogs = new ArrayList<>();
        for (QueriedLog log : source.getLogs()) {
            SlsLog slsLog = new SlsLog();
            slsLog.setAppId(appId);
            slsLog.setEnvId(envId);
            slsLog.setServiceId(request.getServiceId());
            slsLog.setSource(log.mSource);
            slsLog.setTime(log.mLogItem.mLogTime);
            for (LogContent logContent : log.mLogItem.mContents) {
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_CONTENT)) {
                    slsLog.setContent(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_CUSTOM_CONTENT)) {
                    slsLog.setContent(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_HOST_NAME)) {
                    slsLog.setPodName(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PATH)) {
                    slsLog.setLogPath(logContent.mValue);
                    continue;
                }

                // Pack Info
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_NUM)) {
                    slsLog.setPackNum(Integer.valueOf(logContent.mValue));
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_OFFSET)) {
                    slsLog.setPackNum(Integer.valueOf(logContent.mValue));
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_ID)) {
                    slsLog.setPackId(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_SHARD_ID)) {
                    slsLog.setPackShardId(Integer.valueOf(logContent.mValue));
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_CURSOR)) {
                    slsLog.setPackCursor(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_META)) {
                    slsLog.setPackMeta(logContent.mValue);
                    continue;
                }
            }
            slsLogs.add(slsLog);
        }
        dest.setLogs(slsLogs);
        return dest;
    }

    public static MiniProgramLogQueryResponse convert(GetLogsResponse source, String appId, String envId, MiniProgramLogQueryRequest request) {
        MiniProgramLogQueryResponse dest = new MiniProgramLogQueryResponse();
        dest.setIsCompleted(source.IsCompleted());
        dest.setAggQuery(source.getAggQuery());
        dest.setWhereQuery(source.getWhereQuery());
        dest.setHasSQL(source.isHasSQL());
        dest.setMKeys(source.getKeys());
        dest.setStart(request.getFrom());
        dest.setEnd(request.getTo());
        dest.setCount(Integer.valueOf(source.GetAllHeaders().get("x-log-count")));
        ArrayList<SlsLog> slsLogs = new ArrayList<>();
        for (QueriedLog log : source.GetLogs()) {
            SlsLog slsLog = new SlsLog();
            slsLog.setAppId(appId);
            slsLog.setEnvId(envId);
            slsLog.setServiceId(request.getServiceId());
            slsLog.setSource(log.mSource);
            slsLog.setTime(log.mLogItem.mLogTime);
            for (LogContent logContent : log.mLogItem.mContents) {
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_CONTENT)) {
                    slsLog.setContent(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_CUSTOM_CONTENT)) {
                    slsLog.setContent(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_HOST_NAME)) {
                    slsLog.setPodName(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PATH)) {
                    slsLog.setLogPath(logContent.mValue);
                    continue;
                }

                // Pack Info
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_NUM)) {
                    slsLog.setPackNum(Integer.valueOf(logContent.mValue));
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_OFFSET)) {
                    slsLog.setPackNum(Integer.valueOf(logContent.mValue));
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_ID)) {
                    slsLog.setPackId(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_SHARD_ID)) {
                    slsLog.setPackShardId(Integer.valueOf(logContent.mValue));
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_CURSOR)) {
                    slsLog.setPackCursor(logContent.mValue);
                    continue;
                }
                if (StringUtils.equals(logContent.mKey, LogConstants.SLS_KEY_PACK_META)) {
                    slsLog.setPackMeta(logContent.mValue);
                    continue;
                }
            }
            slsLogs.add(slsLog);
        }
        dest.setLogs(slsLogs);
        return dest;
    }

}
