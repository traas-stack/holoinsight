/**
 * Alipay.com Inc. Copyright (c) 2004-2022 All Rights Reserved.
 */
package io.holoinsight.server.query.service.analysis;

/**
 * @author wanpeng.xwp
 * @version : Mergable.java, v 0.1 2022年12月08日 15:03 wanpeng.xwp Exp $
 */
public interface Mergable {
    void merge(Mergable other);
}