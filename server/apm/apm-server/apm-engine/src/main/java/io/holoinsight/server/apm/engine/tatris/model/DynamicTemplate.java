/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : DynamicTemplate.java, v 0.1 2023年02月24日 19:46 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DynamicTemplate {
    private DynamicTemplateMapping mapping;
    private String matchMappingType;
    private String matchPattern;
    private String match;
    private String unmatch;
    private String pathMatch;
    private String pathUnmatch;
}
