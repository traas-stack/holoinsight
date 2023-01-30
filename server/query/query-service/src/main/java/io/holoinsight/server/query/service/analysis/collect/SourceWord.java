/** Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved. */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wanpeng.xwp
 * @version : ImportantWord.java, v 0.1 2020年01月09日 15:25 wanpeng.xwp Exp $
 */
@Data
public class SourceWord implements Comparable<SourceWord>, Serializable {

    private static final long serialVersionUID = -5287018923494879096L;

    private String source;

    private int count = 0;

    public long calculateSize() {
        return source.length();
    }

    public SourceWord() {
        super();
    }

    public SourceWord(String source) {
        this.source = source;
        this.count = 1;
    }

    public void increase() {
        this.count += 1;
    }

    /** 按count从大到小排序 */
    @Override
    public int compareTo(SourceWord o) {
        if (this.count >= o.count) {
            return -1;
        } else {
            return 1;
        }
    }
}
