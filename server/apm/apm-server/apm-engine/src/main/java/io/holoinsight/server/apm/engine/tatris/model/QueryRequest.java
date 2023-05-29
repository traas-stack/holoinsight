/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : QueryRequest.java, v 0.1 2023年02月26日 11:35 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QueryRequest {
    private String index;
    private Query query;
    private Map<String, Aggs> aggs;
    private List<Map<String, SortTerm>> sort;
    private long size;
    private long from;
}
