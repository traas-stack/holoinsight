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
 * @version : Query.java, v 0.1 2023年02月26日 11:39 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Query {
    private Object matchAll;
    private Map<String, Object> match;
    private Map<String, Object> matchPhrase;
    private Map<String, Object> queryString;
    private Ids ids;
    private Map<String, Object> term;
    private Map<String, List<Object>> terms;
    private Map<String, RangeVal> range;
    private Bool bool;
}
