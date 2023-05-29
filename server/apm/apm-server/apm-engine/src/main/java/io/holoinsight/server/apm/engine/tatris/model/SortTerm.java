/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author xiangwanpeng
 * @version : SortTerm.java, v 0.1 2023年02月26日 11:46 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SortTerm {
    private String order;
    private String missing;
}