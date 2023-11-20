/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.AlertNotifyRecordService;
import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.home.dal.mapper.AlertNotifyRecordMapper;
import io.holoinsight.server.home.dal.model.AlertNotifyRecord;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.NotifyErrorMsg;
import io.holoinsight.server.home.facade.NotifyStage;
import io.holoinsight.server.home.facade.NotifyUser;
import io.holoinsight.server.home.facade.Steps;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.holoinsight.server.home.biz.common.MetaDictType.GLOBAL_CONFIG;

/**
 *
 * @author limengyang
 * @version 2023-07-17 10:27:00
 * @decription Record the whole process of alarm
 */
@Slf4j
public class RecordSucOrFailNotify {
  private static AlertNotifyRecordMapper alertNotifyRecordMapper =
      SpringContext.getBean(AlertNotifyRecordMapper.class);

  private static AlertNotifyRecordService alertNotifyRecordService =
      SpringContext.getBean(AlertNotifyRecordService.class);

  private static String address = AddressUtil.getLocalHostName();

  private static final String NOTIFY_CHAIN = "NotifyChains";

  private static boolean recordEnable() {
    String enable = MetaDictUtil.getStringValue(GLOBAL_CONFIG, "record_enable");
    return StringUtils.isNotEmpty(enable) && StringUtils.equals(enable, "true");
  }

  public static void alertNotifyChannelFail(String errorNode, String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String notifyUser) {
    if (!recordEnable()) {
      return;
    }
    if (Objects.nonNull(alertNotifyRecord)) {
      try {
        if (alertNotifyRecord.getIsRecord()) {
          List<NotifyStage> notifyStageList = alertNotifyRecord.getNotifyStage();
          buildNotifyStage(notifyStageList, NOTIFY_CHAIN, notifyChannel, false);
          alertNotifyRecord.setNotifyStage(notifyStageList);

          buildChannelAndUser(notifyChannel, alertNotifyRecord, notifyUser);

          List<NotifyErrorMsg> notifyErrorMsgList =
              buildErrorMsg(errorNode, notifyChannel, alertNotifyRecord, notifyUser);

          alertNotifyRecord.setNotifyErrorTime(new Date());
          alertNotifyRecord.setIsSuccess((byte) 0);
          alertNotifyRecord.setExtra(J.toJson(notifyStageList));
          alertNotifyRecord.setNotifyErrorNode(J.toJson(notifyErrorMsgList));
          alertNotifyRecord.setNotifyChannel(J.toJson(alertNotifyRecord.getNotifyChannelList()));
          alertNotifyRecord.setNotifyUser(J.toJson(alertNotifyRecord.getNotifyUserList()));

          AlertNotifyRecord alertNotifyRecord1 = new AlertNotifyRecord();
          BeanUtils.copyProperties(alertNotifyRecord, alertNotifyRecord1);
          alertNotifyRecordMapper.insert(alertNotifyRecord1);
        }
      } catch (Exception e) {
        log.error("{} {} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
            alertNotifyRecord.getUniqueId(), NOTIFY_CHAIN, notifyChannel, errorNode, e);
      }
    } else {
      log.warn("{} record fail. alertNotifyRecord is null", errorNode);
    }

  }

  @NotNull
  private static List<NotifyErrorMsg> buildErrorMsg(String errorNode, String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String notifyUser) {
    NotifyErrorMsg notifyErrorMsg = new NotifyErrorMsg();
    notifyErrorMsg.setNotifyType(notifyChannel);
    notifyErrorMsg.setErrMsg(errorNode);
    notifyErrorMsg.setIsSuccess(false);
    notifyErrorMsg.setAddress(address);
    notifyErrorMsg.setNotifyUser(notifyUser);
    List<NotifyErrorMsg> notifyErrorMsgList = alertNotifyRecord.getNotifyErrorMsgList();
    if (CollectionUtils.isEmpty(notifyErrorMsgList)) {
      notifyErrorMsgList = new ArrayList<>();
    }
    notifyErrorMsgList.add(notifyErrorMsg);
    return notifyErrorMsgList;
  }

  public static void alertNotifySuccess(String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String user) {
    if (Objects.isNull(alertNotifyRecord)) {
      log.warn("{} {} record fail. alertNotifyRecord is null", NOTIFY_CHAIN, notifyChannel);
      return;
    }
    try {
      if (alertNotifyRecord.getIsRecord()) {
        List<NotifyStage> notifyStageList = alertNotifyRecord.getNotifyStage();
        buildNotifyStage(notifyStageList, NOTIFY_CHAIN, notifyChannel, true);
        alertNotifyRecord.setNotifyStage(notifyStageList);
        buildChannelAndUser(notifyChannel, alertNotifyRecord, user);
      }
    } catch (Exception e) {
      log.error("{} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), NOTIFY_CHAIN, notifyChannel, e);
    }

  }

  public static void alertNotifyNoEventGenerated(String errorNode, String stage, String step,
      AlertNotifyRecordDTO alertNotifyRecord, List<TriggerResult> noEventGeneratedList) {
    if (Objects.isNull(alertNotifyRecord)) {
      log.warn("{} {} record fail. alertNotifyRecord is null", stage, step);
    }
    try {
      if (alertNotifyRecord.getIsRecord()) {
        List<NotifyStage> notifyStageList = alertNotifyRecord.getNotifyStage();
        buildNotifyStage(notifyStageList, stage, step, true);
        List<NotifyErrorMsg> notifyErrorMsgList =
            buildErrorMsg(errorNode, "", alertNotifyRecord, "");
        alertNotifyRecord.setNotifyStage(notifyStageList);
        alertNotifyRecord.setNotifyErrorMsgList(notifyErrorMsgList);
        alertNotifyRecord.setIsSuccess((byte) 1);
        alertNotifyRecord.setNotifyErrorTime(new Date());
        alertNotifyRecord.setTriggerResult(J.toJson(noEventGeneratedList));
      }
    } catch (Exception e) {
      log.error("{} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), stage, step, e);
    }
  }

  private static void buildChannelAndUser(String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String user) {
    List<NotifyUser> notifyUserList = alertNotifyRecord.getNotifyUserList();
    if (CollectionUtils.isEmpty(notifyUserList)) {
      notifyUserList = new ArrayList<>();
    }
    NotifyUser notifyUser = new NotifyUser();
    notifyUser.setNotifyChannel(notifyChannel);
    notifyUser.setUser(user);
    if (!notifyUserList.contains(notifyUser)) {
      notifyUserList.add(notifyUser);
      alertNotifyRecord.setNotifyUserList(notifyUserList);
    }

    List<String> notifyChannelList = alertNotifyRecord.getNotifyChannelList();
    if (CollectionUtils.isEmpty(notifyChannelList)) {
      notifyChannelList = new ArrayList<>();
    }
    if (!notifyChannelList.contains(notifyChannel)) {
      notifyChannelList.add(notifyChannel);
      alertNotifyRecord.setNotifyChannelList(notifyChannelList);
    }
  }

  public static void alertNotifyProcess(String errorNode, String stage, String step,
      AlertNotifyRecordDTO alertNotifyRecord) {
    if (!recordEnable()) {
      return;
    }
    if (Objects.isNull(alertNotifyRecord)) {
      log.warn("{} {} {} record fail. alertNotifyRecord is null", stage, step, errorNode);
      return;
    }
    try {
      if (alertNotifyRecord.getIsRecord()) {
        alertNotifyRecord.setNotifyErrorTime(new Date());
        List<NotifyStage> notifyStageList = alertNotifyRecord.getNotifyStage();
        buildNotifyStage(notifyStageList, stage, step, false);

        NotifyErrorMsg notifyErrorMsg = new NotifyErrorMsg();
        notifyErrorMsg.setNotifyType("default");
        notifyErrorMsg.setErrMsg(errorNode);
        notifyErrorMsg.setIsSuccess(false);
        notifyErrorMsg.setAddress(address);
        List<NotifyErrorMsg> notifyErrorMsgList = new ArrayList<>();
        notifyErrorMsgList.add(notifyErrorMsg);
        alertNotifyRecord.setNotifyStage(notifyStageList);
        alertNotifyRecord.setExtra(J.toJson(notifyStageList));
        alertNotifyRecord.setNotifyErrorNode(J.toJson(notifyErrorMsgList));
        alertNotifyRecord.setIsSuccess((byte) 0);
        AlertNotifyRecord alertNotifyRecord1 = new AlertNotifyRecord();
        BeanUtils.copyProperties(alertNotifyRecord, alertNotifyRecord1);
        alertNotifyRecordMapper.insert(alertNotifyRecord1);
      }
    } catch (Exception e) {
      log.error("{} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), stage, step, e);
    }
  }

  private static void buildNotifyStage(List<NotifyStage> notifyStageList, String stage, String step,
      boolean isSuccess) {
    if (CollectionUtils.isEmpty(notifyStageList)) {
      notifyStageList = new ArrayList<>();
    }
    List<String> stageList =
        notifyStageList.stream().map(NotifyStage::getStage).collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(stageList) && stageList.contains(stage)) {
      int i = notifyStageList.size() - 1;
      NotifyStage notifyStage = notifyStageList.get(i);
      if (Objects.equals(notifyStage.getStage(), stage)) {
        notifyStageList.remove(i);
        List<Steps> stepsList = notifyStage.getSteps();
        if (CollectionUtils.isEmpty(stepsList)) {
          stepsList = new ArrayList<>();
        }
        Steps steps = new Steps();
        steps.setStep(step);
        steps.setIsSuccess(isSuccess);
        if (!stepsList.contains(steps)) {
          stepsList.add(steps);
        }
        notifyStage.setSteps(stepsList);
        notifyStageList.add(notifyStage);
      }
    } else {
      NotifyStage notifyStage = new NotifyStage();
      notifyStage.setStage(stage);
      List<Steps> stepsList = new ArrayList<>();
      Steps steps = new Steps();
      steps.setStep(step);
      steps.setIsSuccess(isSuccess);
      stepsList.add(steps);
      notifyStage.setSteps(stepsList);
      notifyStageList.add(notifyStage);
    }
  }

  public static void alertNotifyProcessSuc(String stage, String step,
      AlertNotifyRecordDTO alertNotifyRecord) {
    if (Objects.isNull(alertNotifyRecord)) {
      log.warn("{} {} record fail. alertNotifyRecord is null", stage, step);
      return;
    }
    try {
      if (Objects.isNull(alertNotifyRecord.getIsRecord()) || alertNotifyRecord.getIsRecord()) {
        List<NotifyStage> notifyStageList = alertNotifyRecord.getNotifyStage();
        buildNotifyStage(notifyStageList, stage, step, true);
        alertNotifyRecord.setNotifyStage(notifyStageList);
      }
    } catch (Exception e) {
      log.error("{} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), stage, step, e);
    }
  }


  public static void alertNotifyMigrate(ComputeTaskPackage computeTaskPackage) {
    if (Objects.isNull(computeTaskPackage)
        || Objects.isNull(computeTaskPackage.getAlertNotifyRecord())) {
      log.warn("alertNotifyMigrate record fail. computeTaskPackage or alertNotifyRecord is null");
      return;
    }
    try {
      AlertNotifyRecordDTO alertNotifyRecord = computeTaskPackage.getAlertNotifyRecord();
      List<InspectConfig> inspectConfigs = computeTaskPackage.getInspectConfigs();
      List<InspectConfig> inspectConfigList = new ArrayList<>();
      for (int i = 0; i < inspectConfigs.size(); i++) {
        AlertNotifyRecordDTO alertNotifyRecordDTO = new AlertNotifyRecordDTO();
        BeanUtils.copyProperties(alertNotifyRecord, alertNotifyRecordDTO);
        InspectConfig inspectConfig = inspectConfigs.get(i);
        BeanUtils.copyProperties(inspectConfig, alertNotifyRecordDTO);
        alertNotifyRecordDTO.setIsRecord(inspectConfig.isAlertRecord());
        inspectConfig.setAlertNotifyRecord(alertNotifyRecordDTO);
        log.info("alertNotifyRecord switch :{}", alertNotifyRecordDTO.getIsRecord());
        inspectConfigList.add(inspectConfig);
      }
      computeTaskPackage.setInspectConfigs(inspectConfigList);
    } catch (Exception e) {
      log.error("{} alertNotifyMigrate record fail.", computeTaskPackage.getTraceId(), e);
    }
  }

  public static void alertNotifyFailAppendMsg(String errorNode, String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String notifyUser) {
    if (Objects.isNull(alertNotifyRecord)) {
      log.warn("{} {} {} record fail. alertNotifyRecord is null", NOTIFY_CHAIN, notifyChannel,
          errorNode);
      return;
    }
    try {
      if (alertNotifyRecord.getIsRecord()) {
        List<NotifyStage> notifyStageList = alertNotifyRecord.getNotifyStage();
        buildNotifyStage(notifyStageList, NOTIFY_CHAIN, notifyChannel, false);
        alertNotifyRecord.setNotifyStage(notifyStageList);

        buildChannelAndUser(notifyChannel, alertNotifyRecord, notifyUser);

        List<NotifyErrorMsg> notifyErrorMsgList =
            buildErrorMsg(errorNode, notifyChannel, alertNotifyRecord, notifyUser);
        alertNotifyRecord.setNotifyErrorNode(J.toJson(notifyErrorMsgList));
        alertNotifyRecord.setIsSuccess((byte) 0);
      }
    } catch (Exception e) {
      log.error("{} {} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), NOTIFY_CHAIN, notifyChannel, errorNode, e);
    }
  }

  public static void batchInsert(List<AlertNotifyRecordDTO> alertNotifyRecordDTOList) {
    if (!recordEnable()) {
      return;
    }
    try {
      List<AlertNotifyRecord> alertNotifyRecords = new ArrayList<>();
      if (!CollectionUtils.isEmpty(alertNotifyRecordDTOList)) {
        alertNotifyRecordDTOList.forEach(alertNotifyRecord -> {
          if (Objects.nonNull(alertNotifyRecord) && alertNotifyRecord.getIsRecord()) {
            alertNotifyRecord.setExtra(J.toJson(alertNotifyRecord.getNotifyStage()));
            alertNotifyRecord.setNotifyUser(J.toJson(alertNotifyRecord.getNotifyUserList()));
            alertNotifyRecord
                .setNotifyErrorNode(J.toJson(alertNotifyRecord.getNotifyErrorMsgList()));
            alertNotifyRecord.setNotifyChannel(J.toJson(alertNotifyRecord.getNotifyChannelList()));
            alertNotifyRecord.setNotifyUser(J.toJson(alertNotifyRecord.getNotifyUserList()));

            AlertNotifyRecord alertNotifyRecord1 = new AlertNotifyRecord();
            BeanUtils.copyProperties(alertNotifyRecord, alertNotifyRecord1);
            alertNotifyRecords.add(alertNotifyRecord1);
          }
        });
      } else {
        log.warn("batch insert record fail. alertNotifyRecordDTOList is null");
        return;
      }
      log.info("batch insert data size {}", alertNotifyRecords.size());
      alertNotifyRecordService.saveBatch(alertNotifyRecords);
    } catch (Exception e) {
      log.error("batch insert record fail. {}", e.getMessage(), e);
    }

  }
}
