/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : NumericRange.java, v 0.1 2023年02月26日 11:45 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NumericRange {
    private double from;
    private double to;
}
