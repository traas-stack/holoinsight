/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : QueryResponse.java, v 0.1 2023年02月26日 11:56 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QueryResponse {
    private long took;
    private boolean timedOut;
    @JsonProperty("_shards")
    private Shards shards;
    private Hits hits;
    private Object error;
    private int status;
    private Map<String, AggsResponse> aggregations;
}
