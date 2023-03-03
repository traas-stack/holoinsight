/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author jiwliu
 * @version : FailResponse.java, v 0.1 2022年09月20日 16:02 xiangwanpeng Exp $
 */
@ApiModel(description = "当请求失败时返回的数据结构")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2020-01-21T07:37:08.930Z")
@Builder
@Data
public class FailResponse {
  @JsonProperty("code")
  private String code = null;

  @JsonProperty("msg")
  private String msg = null;
}
