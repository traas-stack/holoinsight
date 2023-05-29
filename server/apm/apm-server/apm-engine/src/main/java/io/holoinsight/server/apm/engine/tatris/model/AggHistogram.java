/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : AggHistogram.java, v 0.1 2023年02月26日 11:49 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AggHistogram {
    private String field;
    private double interval;
    private int minDocCount;
    private double offset;
    private boolean keyed;
    private Object order;
    private String missing;
    private HistogramBound extendedBounds;
    private HistogramBound hardBounds;
}
