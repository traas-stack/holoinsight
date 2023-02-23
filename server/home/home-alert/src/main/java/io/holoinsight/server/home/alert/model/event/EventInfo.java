/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.facade.PqlRule;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:11 下午
 */
@Data
public class EventInfo {

  private String uniqueId;

  private Long alarmTime;

  private BoolOperationEnum boolOperation;

  private Boolean isRecover;

  private Boolean isPql;

  private PqlRule pqlRule;

  private Map<Trigger, List<TriggerResult>> alarmTriggerResults = new HashMap<>();

  private String envType;
}
