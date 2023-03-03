/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.installer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author jiwliu
 * @version : Mappings.java, v 0.1 2022年10月12日 10:21 xiangwanpeng Exp $
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicTemplate {

  private String matchMappingType;
  private String match;
  private String matchPattern;
  private String unmatch;
  private String pathMatch;
  private String pathUnmatch;
  private Map<String, String> mapping;
}
