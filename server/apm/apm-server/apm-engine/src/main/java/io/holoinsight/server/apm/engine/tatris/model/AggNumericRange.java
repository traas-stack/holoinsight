/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiangwanpeng
 * @version : AggNumericRange.java, v 0.1 2023年02月26日 11:45 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AggNumericRange {
    private String field;
    private List<NumericRange> ranges;
    private boolean keyed;
}
