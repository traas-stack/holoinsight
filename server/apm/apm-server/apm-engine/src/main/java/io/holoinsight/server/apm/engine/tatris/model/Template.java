/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : Template.java, v 0.1 2023年02月26日 11:27 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Template {
    private Settings settings;
    private Mappings mappings;
    private Map<String, AliasTerm> aliases;
}
