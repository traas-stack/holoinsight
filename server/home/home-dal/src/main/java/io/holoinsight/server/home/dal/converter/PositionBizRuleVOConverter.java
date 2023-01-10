/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.converter;

import io.holoinsight.server.home.dal.model.dto.FieldTypeEnum;
import io.holoinsight.server.home.dal.model.dto.PositionBizRuleDTO;
import io.holoinsight.server.home.dal.model.dto.BizResultConfig;
import io.holoinsight.server.home.dal.model.dto.OperateTypeEnum;
import io.holoinsight.server.home.dal.model.dto.PositionBizRuleVO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * vo与dto转换类
 */
public class PositionBizRuleVOConverter {

  /**
   * 业务成功mvel与错误码配置mvel长度区分
   */
  private static final int BIZ_RESULT_AND_ERROR_CODE_LENGTH = 2;

  /**
   * 单一业务是否成功mvel长度区分：字段名、字段类型、字段值
   */
  private static final int BIZ_RESULT_CONFIG_LENGTH = 4;


  /**
   * &&&
   */
  private static final String TRIPLE_AND = "&&&";

  /**
   * AT
   */
  private static final String TRIPLE_AT = "@@@";

  /**
   * ###
   */
  private static final String TRIPLE_SHARP = "###";

  /**
   * dto to vo
   *
   * @param positionBizRuleDTO
   * @return
   */
  public static PositionBizRuleVO dtoToVO(PositionBizRuleDTO positionBizRuleDTO) {
    if (positionBizRuleDTO == null) {
      return null;
    }
    PositionBizRuleVO positionBizRuleVO = new PositionBizRuleVO();
    positionBizRuleVO.setAppId(positionBizRuleDTO.getAppId());
    positionBizRuleVO.setEnvId(positionBizRuleDTO.getEnvId());
    positionBizRuleVO.setAppName(positionBizRuleDTO.getAppName());
    positionBizRuleVO.setInterfaceType(positionBizRuleDTO.getInterfaceType());
    positionBizRuleVO.setInterfaceName(positionBizRuleDTO.getInterfaceName());
    positionBizRuleVO.setGlobalOpen(positionBizRuleDTO.getGlobalOpen());
    positionBizRuleVO.setResponseType(positionBizRuleDTO.getResponseType());
    positionBizRuleVO.setResponseProperty(positionBizRuleDTO.getResponseProperty());

    // 获取错误码配置长字段，截取适配前端VO
    String errorCodeConfig = positionBizRuleDTO.getErrorCodeConfig();
    if (!checkErrorCodeConfigMvel(errorCodeConfig)) {
      positionBizRuleVO.setBizResult(null);
      positionBizRuleVO.setErrorCode(null);
    } else {
      positionBizRuleVO
          .setBizResult(getErrorCodeConfigMvelForVO((errorCodeConfig.split(TRIPLE_AT)[0])));
      positionBizRuleVO.setErrorCode(errorCodeConfig.split(TRIPLE_AT)[1]);
    }
    return positionBizRuleVO;
  }

  /**
   * vo to dto
   *
   * @param positionBizRuleVO
   * @return
   */
  public static PositionBizRuleDTO voToDTO(PositionBizRuleVO positionBizRuleVO) {
    if (positionBizRuleVO == null) {
      return null;
    }
    PositionBizRuleDTO positionBizRuleDTO = new PositionBizRuleDTO();
    positionBizRuleDTO.setAppId(positionBizRuleVO.getAppId());
    positionBizRuleDTO.setEnvId(positionBizRuleVO.getEnvId());
    positionBizRuleDTO.setAppName(positionBizRuleVO.getAppName());
    positionBizRuleDTO.setInterfaceType(positionBizRuleVO.getInterfaceType());
    positionBizRuleDTO.setInterfaceName(positionBizRuleVO.getInterfaceName());
    positionBizRuleDTO.setGlobalOpen(positionBizRuleVO.getGlobalOpen());
    positionBizRuleDTO.setResponseType(positionBizRuleVO.getResponseType());
    positionBizRuleDTO.setResponseProperty(positionBizRuleVO.getResponseProperty());
    positionBizRuleDTO.setErrorCodeConfig(getErrorCodeConfigStr(positionBizRuleVO));

    return positionBizRuleDTO;
  }

  /**
   * 校验错误码规则配置是否正确
   *
   * @param errorCodeConfig
   * @return
   */
  public static boolean checkErrorCodeConfigMvel(String errorCodeConfig) {
    if (StringUtils.isBlank(errorCodeConfig)) {
      return false;
    }
    // 校验外层的业务成功mvel和错误码
    String[] resArr = errorCodeConfig.split(TRIPLE_AT);
    if (resArr.length != BIZ_RESULT_AND_ERROR_CODE_LENGTH) {
      return false;
    }
    // 开始校验业务成功mvel的配置
    String bizResMvelStr = resArr[0];
    String errorCodeMvel = resArr[1];
    if (StringUtils.isBlank(bizResMvelStr) || StringUtils.isBlank(errorCodeMvel)) {
      return false;
    }
    // 可能有多个mvel，逐一校验
    for (String bizResMvel : bizResMvelStr.split(TRIPLE_AND)) {
      if (StringUtils.isBlank(bizResMvel)) {
        return false;
      }
      String[] bizResArr = bizResMvel.split(TRIPLE_SHARP);
      if (bizResArr.length != BIZ_RESULT_CONFIG_LENGTH) {
        return false;
      }
    }
    return true;
  }


  /**
   * 从dto中获取vo所需的错误码配置
   *
   * @param bizResMvelStr
   * @return
   */
  public static List<BizResultConfig> getErrorCodeConfigMvelForVO(String bizResMvelStr) {
    List<BizResultConfig> list = new ArrayList<>();
    for (String bizResMvel : bizResMvelStr.split(TRIPLE_AND)) {
      BizResultConfig bizResultConfig = new BizResultConfig();
      bizResultConfig.setFieldName(bizResMvel.split(TRIPLE_SHARP)[0]);
      bizResultConfig.setFieldType(
          FieldTypeEnum.getFieldTypeByCode(bizResMvel.split(TRIPLE_SHARP)[1]).getValue());

      bizResultConfig.setOperateType(
          OperateTypeEnum.getOperateTypeByCode(bizResMvel.split(TRIPLE_SHARP)[2]).getValue());

      bizResultConfig.setFieldValue(bizResMvel.split(TRIPLE_SHARP)[3]);
      list.add(bizResultConfig);
    }
    return list;
  }

  /**
   * 配置拼接
   *
   * @param positionBizRuleVO
   * @return
   */
  public static String getErrorCodeConfigStr(PositionBizRuleVO positionBizRuleVO) {
    if (positionBizRuleVO.getBizResult() == null) {
      return null;
    }
    List<String> bizResMvelList = positionBizRuleVO.getBizResult().stream().map(item -> {
      StringBuilder sb = new StringBuilder();
      sb.append(item.getFieldName()).append(TRIPLE_SHARP);
      sb.append(FieldTypeEnum.getFieldTypeByValue(item.getFieldType()).getCode())
          .append(TRIPLE_SHARP);
      sb.append(OperateTypeEnum.getOperateTypeByValue(item.getOperateType()).getCode())
          .append(TRIPLE_SHARP);
      sb.append(item.getFieldValue());
      return sb.toString();
    }).collect(Collectors.toList());
    return StringUtils.join(bizResMvelList, TRIPLE_AND) + TRIPLE_AT
        + positionBizRuleVO.getErrorCode();
  }
}
