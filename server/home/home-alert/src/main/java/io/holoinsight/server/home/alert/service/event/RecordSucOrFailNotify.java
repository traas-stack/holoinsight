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

  private static AlertNotifyRecordService alertNotifyRecordService =
      SpringContext.getBean(AlertNotifyRecordService.class);

  private static String address = AddressUtil.getLocalHostName();

  private static final String NOTIFY_CHAIN = "NotifyChains";

  private static boolean recordEnable() {
    String enable = MetaDictUtil.getStringValue(GLOBAL_CONFIG, "record_enable");
    return StringUtils.isNotEmpty(enable) && StringUtils.equals(enable, "true");
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

        InspectConfig inspectConfig = inspectConfigs.get(i);
        List<NotifyStage> notifyStage = new ArrayList<>(alertNotifyRecord.getNotifyStage());
        AlertNotifyRecordDTO alertNotifyRecordDTO = new AlertNotifyRecordDTO();
        alertNotifyRecordDTO.setGmtCreate(alertNotifyRecord.getGmtCreate());
        alertNotifyRecordDTO.setIsSuccess(alertNotifyRecord.getIsSuccess());
        alertNotifyRecordDTO.setTraceId(alertNotifyRecord.getTraceId());
        alertNotifyRecordDTO.setNotifyStage(notifyStage);
        alertNotifyRecordDTO.setIsRecord(inspectConfig.isAlertRecord());
        alertNotifyRecordDTO.setTenant(inspectConfig.getTenant());
        alertNotifyRecordDTO.setUniqueId(inspectConfig.getUniqueId());
        alertNotifyRecordDTO.setRuleName(inspectConfig.getRuleName());
        alertNotifyRecordDTO.setEnvType(inspectConfig.getEnvType());

        inspectConfig.setAlertNotifyRecord(alertNotifyRecordDTO);
        inspectConfigList.add(inspectConfig);
      }
      computeTaskPackage.setInspectConfigs(inspectConfigList);
    } catch (Exception e) {
      log.error("{} alertNotifyMigrate record fail.", computeTaskPackage.getTraceId(), e);
    }
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
          alertNotifyRecord.setNotifyErrorNode(notifyErrorMsgList);

          alertNotifyRecordService.insert(alertNotifyRecord);
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
    notifyErrorMsg.setAddress(address);
    notifyErrorMsg.setNotifyUser(notifyUser);
    List<NotifyErrorMsg> notifyErrorMsgList = alertNotifyRecord.getNotifyErrorNode();
    if (CollectionUtils.isEmpty(notifyErrorMsgList)) {
      notifyErrorMsgList = new ArrayList<>();
    }
    notifyErrorMsgList.add(notifyErrorMsg);
    return notifyErrorMsgList;
  }

  public static void alertNotifyChannelSuccess(String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String user) {
    if (!recordEnable()) {
      return;
    }
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
        alertNotifyRecord.setNotifyErrorNode(new ArrayList<>());
        alertNotifyRecordService.insert(alertNotifyRecord);
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
        alertNotifyRecord.setNotifyErrorNode(notifyErrorMsgList);
        alertNotifyRecord.setIsSuccess((byte) 1);
        alertNotifyRecord.setTriggerResult(J.toJson(noEventGeneratedList));
      }
      log.info("{} {} {} {} no alarm event generated, {}", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), stage, step, errorNode);
    } catch (Exception e) {
      log.error("{} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), stage, step, e);
    }
  }

  private static void buildChannelAndUser(String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String user) {
    NotifyUser notifyUser = new NotifyUser();
    notifyUser.setNotifyChannel(notifyChannel);
    notifyUser.setUser(user);

    List<NotifyUser> notifyUserList = new ArrayList<>();
    notifyUserList.add(notifyUser);

    alertNotifyRecord.setNotifyUser(notifyUserList);
    alertNotifyRecord.setNotifyChannel(notifyChannel);
  }

  public static void alertNotifyProcessFail(String errorNode, String stage, String step,
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
        notifyErrorMsg.setNotifyType("-");
        notifyErrorMsg.setErrMsg(errorNode);
        notifyErrorMsg.setAddress(address);
        List<NotifyErrorMsg> notifyErrorMsgList = new ArrayList<>();
        notifyErrorMsgList.add(notifyErrorMsg);

        alertNotifyRecord.setNotifyStage(notifyStageList);
        alertNotifyRecord.setNotifyErrorNode(notifyErrorMsgList);
        alertNotifyRecord.setIsSuccess((byte) 0);
        alertNotifyRecordService.insert(alertNotifyRecord);
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

  public static void alertNotifyFailAppendMsg(String errorNode, String notifyChannel,
      AlertNotifyRecordDTO alertNotifyRecord, String notifyUser) {

    if (!recordEnable()) {
      return;
    }
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
        alertNotifyRecord.setNotifyErrorNode(notifyErrorMsgList);
        alertNotifyRecord.setIsSuccess((byte) 0);

        alertNotifyRecordService.insert(alertNotifyRecord);
      }
    } catch (Exception e) {
      log.error("{} {} {} {} {} record fail. ", alertNotifyRecord.getTraceId(),
          alertNotifyRecord.getUniqueId(), NOTIFY_CHAIN, notifyChannel, errorNode, e);
    }
  }

  // public static void batchInsert(List<AlertNotifyRecordDTO> alertNotifyRecordDTOList) {
  // if (!recordEnable()) {
  // return;
  // }
  // try {
  // if (!CollectionUtils.isEmpty(alertNotifyRecordDTOList)) {
  // alertNotifyRecordDTOList.forEach(alertNotifyRecord -> {
  // if (Objects.nonNull(alertNotifyRecord) && alertNotifyRecord.getIsRecord()) {
  // alertNotifyRecordService.insert(alertNotifyRecord);
  // }
  // });
  // } else {
  // log.warn("batch insert record fail. alertNotifyRecordDTOList is null");
  // }
  // } catch (Exception e) {
  // log.error("batch insert record fail. {}", e.getMessage(), e);
  // }
  //
  // }
}
