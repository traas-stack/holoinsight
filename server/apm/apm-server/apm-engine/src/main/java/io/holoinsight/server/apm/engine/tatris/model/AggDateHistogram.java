/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : AggDateHistogram.java, v 0.1 2023年02月26日 11:47 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AggDateHistogram {
    private String field;
    private String interval;
    private String fixedInterval;
    private String calendarInterval;
    private String timeZone;
    private int minDocCount;
    private String format;
    private String offset;
    private boolean keyed;
    private Object order;
    private String missing;
    private HistogramBound extendedBounds;
    private HistogramBound hardBounds;
}
