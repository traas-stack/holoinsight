/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.GroupInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginConfMapper.java, v 0.1 2022年03月14日 8:46 下午 jinsong.yjs Exp $
 */
@Component
public class GroupInfoConfMapper {
  public static String asString(GroupInfoDTO conf) {
    return conf == null ? null : J.toJson(conf.getGroupInfo());
  }

  public static GroupInfoDTO asObj(String conf) {
    GroupInfoDTO groupInfoDTO = new GroupInfoDTO();
    if (StringUtils.isNotBlank(conf)) {
      Map<String, List<String>> groupInfo = J.fromJson(conf, Map.class);
      groupInfoDTO.setGroupInfo(groupInfo);
      groupInfoDTO.setPersonNum(groupInfo.get("person").size());
    }
    return groupInfoDTO;
  }
}
