/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.web.grpc;

import io.holoinsight.server.home.biz.service.AlarmHistoryDetailService;
import io.holoinsight.server.home.biz.service.AlarmHistoryService;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.proto.alarm.AlarmServiceGrpc;
import io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest;
import io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest;
import io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest;
import io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest;
import io.holoinsight.server.home.proto.base.DataBaseResponse;
import io.holoinsight.server.home.web.common.GrpcFacadeTemplate;
import io.holoinsight.server.home.web.common.GrpcManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmRuleGrpcService.java, v 0.1 2022年06月15日 2:54 下午 jinsong.yjs Exp $
 */
@GrpcService
public class AlarmServiceGrpcImpl extends AlarmServiceGrpc.AlarmServiceImplBase {

    @Autowired
    private GrpcFacadeTemplate  grpcFacadeTemplate;

    @Autowired
    private AlertRuleService    alarmRuleService;

    @Autowired
    private AlarmHistoryService alarmHistoryService;

    @Autowired
    private AlarmHistoryDetailService alarmHistoryDetailService;

    @Override
    public void queryById(QueryOrDeleteRequest request,
                          StreamObserver<DataBaseResponse> responseObserver) {

        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());
        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(request.getId(), "id");
            }

            @Override
            public void doManage() {
                AlarmRuleDTO alarmRuleDTO = alarmRuleService.queryById(request.getId(), request.getTenant());

                builder.setSuccess(true);
                builder.setRowsJson(J.toJson(alarmRuleDTO));
            }

        }, builder);
    }

    @Override
    public void deleteById(QueryOrDeleteRequest request,
                           StreamObserver<DataBaseResponse> responseObserver) {

        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());
        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(request.getId(), "id");
            }

            @Override
            public void doManage() {
                boolean b = alarmRuleService.removeById(request.getId());
                builder.setSuccess(b);
            }

        }, builder);
    }

    @Override
    public void shutdownById(QueryOrDeleteRequest request,
                             StreamObserver<DataBaseResponse> responseObserver) {

        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());
        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(request.getId(), "id");
            }

            @Override
            public void doManage() {
                AlarmRuleDTO alarmRuleDTO = alarmRuleService.queryById(request.getId(), request.getTenant());
                if (null == alarmRuleDTO) {
                    throw new MonitorException("queryById is null");
                }

                alarmRuleDTO.setStatus((byte) 0);
                Boolean aBoolean = alarmRuleService.updateById(alarmRuleDTO);
                builder.setSuccess(aBoolean);
                builder.setRowsJson(J.toJson(alarmRuleService.queryById(request.getId(), request.getTenant())));
            }

        }, builder);
    }

    @Override
    public void create(InsertOrUpdateRequest request,
                       StreamObserver<DataBaseResponse> responseObserver) {
        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());

        String rowsJson = request.getRowsJson();

        AlarmRuleDTO alarmRuleDTO = J.fromJson(rowsJson, AlarmRuleDTO.class);

        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotBlank(alarmRuleDTO.getRuleName(), "ruleName");
                ParaCheckUtil.checkParaNotBlank(alarmRuleDTO.getAlarmLevel(), "alarmLevel");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getRule(), "rule");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getTimeFilter(), "timeFilter");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getStatus(), "status");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getRecover(), "recover");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getIsMerge(), "isMerge");
            }

            @Override
            public void doManage() {
                alarmRuleDTO.setGmtCreate(new Date());
                alarmRuleDTO.setGmtModified(new Date());
                Long id = alarmRuleService.save(alarmRuleDTO);

                AlarmRuleDTO alarmRule = alarmRuleService.queryById(id, alarmRuleDTO.getTenant());

                builder.setSuccess(true);
                builder.setRowsJson(J.toJson(alarmRule));
            }

        }, builder);
    }

    @Override
    public void update(InsertOrUpdateRequest request,
                       StreamObserver<DataBaseResponse> responseObserver) {
        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());

        String rowsJson = request.getRowsJson();

        AlarmRuleDTO alarmRuleDTO = J.fromJson(rowsJson, AlarmRuleDTO.class);

        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getId(), "id");
                ParaCheckUtil.checkParaNotBlank(alarmRuleDTO.getRuleName(), "ruleName");
                ParaCheckUtil.checkParaNotBlank(alarmRuleDTO.getAlarmLevel(), "alarmLevel");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getRule(), "rule");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getTimeFilter(), "timeFilter");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getStatus(), "status");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getRecover(), "recover");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getIsMerge(), "isMerge");
            }

            @Override
            public void doManage() {

                alarmRuleDTO.setGmtModified(new Date());
                boolean save = alarmRuleService.updateById(alarmRuleDTO);
                AlarmRuleDTO alarmRuleDTONew = alarmRuleService.queryById(alarmRuleDTO.getId(), alarmRuleDTO.getTenant());

                builder.setSuccess(save);
                builder.setRowsJson(J.toJson(alarmRuleDTONew));
            }

        }, builder);
    }

    @Override
    public void queryAlarmHistoryByPage(QueryAlarmHistoryPageRequest request,
                                        StreamObserver<DataBaseResponse> responseObserver) {
        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());

        String rowsJson = request.getRowsJson();

        MonitorPageRequest<AlarmHistoryDTO> pageRequest = J.fromJson(rowsJson,
            new TypeToken<MonitorPageRequest<AlarmHistoryDTO>>() {
            }.getType());
        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");

            }

            @Override
            public void doManage() {

                MonitorPageResult<AlarmHistoryDTO> listByPage = alarmHistoryService
                    .getListByPage(pageRequest, null);

                builder.setSuccess(true);
                builder.setRowsJson(J.toJson(listByPage));
            }

        }, builder);
    }

    @Override
    public void queryAlarmHistoryDetailByPage(QueryAlarmHistoryDetailPageRequest request,
                                              StreamObserver<DataBaseResponse> responseObserver) {
        DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
        builder.setTraceId(request.getTraceId());

        String rowsJson = request.getRowsJson();

        MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest = J.fromJson(rowsJson,
                new TypeToken<MonitorPageRequest<AlarmHistoryDetailDTO>>() {
                }.getType());
        grpcFacadeTemplate.manage(responseObserver, new GrpcManageCallback() {

            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");

            }

            @Override
            public void doManage() {

                MonitorPageResult<AlarmHistoryDetailDTO> listByPage = alarmHistoryDetailService
                        .getListByPage(pageRequest);

                builder.setSuccess(true);
                builder.setRowsJson(J.toJson(listByPage));
            }

        }, builder);
    }

}