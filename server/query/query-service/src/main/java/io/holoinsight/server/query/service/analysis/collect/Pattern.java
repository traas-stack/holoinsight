/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanpeng.xwp
 * @version : Pattern.java, v 0.1 2022年12月08日 20:11 wanpeng.xwp Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pattern {

    private String name;
    private int count;

}