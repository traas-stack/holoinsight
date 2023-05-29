/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : Aggs.java, v 0.1 2023年02月26日 11:41 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Aggs {
    private AggTerms terms;
    private AggDateHistogram dateHistogram;
    private AggHistogram histogram;
    private AggNumericRange numericRange;
    private AggMetric sum;
    private AggMetric min;
    private AggMetric max;
    private AggMetric avg;
    private AggMetric cardinality;
    private AggMetric weightedAvg;
    private Map<String, Aggs> aggs;
}
