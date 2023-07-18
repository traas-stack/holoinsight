/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/4/14 7:41 下午
 */
@Data
public class AlarmGroupDTO {

  private Long id;

  private String tenant;

  private String workspace;

  private String groupName;

  private GroupInfoDTO groupInfo;

  private String creator;

  private String modifier;

  private Date gmtCreate;

  private Date gmtModified;

  private String emailAddress;

  private String ddWebhook;

  private String dyvmsPhone;

  private String smsPhone;

  /**
   * 环境类型
   */
  private String envType;

  public List<String> getUserList() {
    if (this.groupInfo == null) {
      return Collections.emptyList();
    }
    Object data = this.groupInfo.getGroupInfo();
    if (!(data instanceof Map)) {
      return Collections.emptyList();
    }
    Map<String, List<String>> map = (Map<String, List<String>>) data;
    List<String> personList = map.get("person");
    if (CollectionUtils.isEmpty(personList)) {
      return Collections.emptyList();
    }
    return personList;
  }
}
