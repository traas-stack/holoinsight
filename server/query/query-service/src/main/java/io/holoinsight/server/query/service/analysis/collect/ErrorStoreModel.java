/** Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved. */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wanpeng.xwp
 * @version : ErrorStoreModel.java, v 0.1 2020年01月09日 15:25 wanpeng.xwp Exp $
 */
@Data
public class ErrorStoreModel implements Serializable {

    /** */
    private static final long serialVersionUID = 9010901754224383629L;

    private int count;
    private String sourceView;
    private List<SourceWord> errorSourceWords;

    public void reduce() {
        this.sourceView = sourceView.substring(0, sourceView.length() / 2) + "...";
        this.reduceList(errorSourceWords);
    }

    private void reduceList(List<SourceWord> list) {
        if (list != null) {
            int cut = list.size() / 2;
            for (int k = list.size() - 1; k >= cut; k--) {
                list.remove(k);
            }
        }
    }

    @Override
    public String toString() {
        return "ErrorStoreModel [count=" + count + ", sourceView=" + sourceView + ", errorSourceWords="
                + errorSourceWords + "]";
    }
}
