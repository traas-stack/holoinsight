/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiangwanpeng
 * @version : AggPercentiles.java, v 0.1 2023年02月26日 11:43 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AggPercentiles {
    private String field;
    private List<Double> percents;
    private double compression;
}
