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
 * @version : Hit.java, v 0.1 2023年02月26日 11:58 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Hit {
    @JsonProperty("_index")
    private String index;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_source")
    private Map<String, Object> source;
}
