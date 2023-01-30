/** Alipay.com Inc. Copyright (c) 2004-2011 All Rights Reserved. */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wanpeng.xwp
 * @version : ErrorLog.java, v 0.1 2020年01月09日 15:25 wanpeng.xwp Exp $
 */
@Data
public abstract class ErrorLog implements Serializable {
    private static final long serialVersionUID = 5165772790853632061L;
    /** 错误数量 */
    protected int count;

    public ErrorLog() {
        super();
        this.count = 1; // 来一条日志就是1
    }

    /** 是否类似 */
    public abstract boolean isSimilarToMe(ErrorLog otherLog);

    /**
     * 创建一个count为0的初始化槽位用于merge
     *
     * @return
     */
    public abstract ErrorLog initEmpty();

    /** 合并 线程安全 */
    public void mergeLog(ErrorLog otherLog) {
        count += otherLog.getCount();
        this.doMerge(otherLog);
    }

    /** 合并 */
    protected abstract void doMerge(ErrorLog otherLog);

    /** 构建为存储结构 */
    public abstract ErrorStoreModel buildStoreModel();

    /** 需要严格管理error model的大小 */
    public int logSize() {
        return Long.SIZE + Integer.SIZE;
    }
}
