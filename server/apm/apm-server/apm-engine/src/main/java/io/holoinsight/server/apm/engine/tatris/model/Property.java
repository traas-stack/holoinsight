/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwanpeng
 * @version : Property.java, v 0.1 2023年02月24日 19:46 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Property {
    private String type;
    private String dynamic;
}
