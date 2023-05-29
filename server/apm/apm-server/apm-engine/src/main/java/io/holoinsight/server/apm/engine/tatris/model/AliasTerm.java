/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : AliasTerm.java, v 0.1 2023年02月26日 11:26 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AliasTerm {
    private String index;
    private String alias;
}
