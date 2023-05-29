/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version : AliasManageRequest.java, v 0.1 2023年02月28日 10:36 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AliasManageRequest {
    private List<Map<String, AliasTerm>> actions;
}
