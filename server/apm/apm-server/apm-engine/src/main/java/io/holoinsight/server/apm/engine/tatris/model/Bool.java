/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiangwanpeng
 * @version : Bool.java, v 0.1 2023年02月26日 11:38 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Bool {
    private List<Query> must;
    private List<Query> mustNot;
    private List<Query> should;
    private List<Query> filter;
    private String minimumShouldMatch;
}
