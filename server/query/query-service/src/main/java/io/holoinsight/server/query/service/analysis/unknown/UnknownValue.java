/**
 * Alipay.com Inc. Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.holoinsight.server.query.service.analysis.unknown;

import io.holoinsight.server.query.service.analysis.Mergable;
import io.holoinsight.server.query.service.analysis.collect.MergeData;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanpeng.xwp
 * @version : LogAnalysisData.java, v 0.1 2022年12月09日 15:25 wanpeng.xwp Exp $
 */
@Data
public class UnknownValue implements Mergable, Serializable {

    private static final long serialVersionUID = 1204956131343394884L;

    private MergeData mergeData;
    private Map<String, Integer> ipCountMap = new HashMap<>();

    public UnknownValue() {
    }

    @Override
    public void merge(Mergable other) {
        if (null == other) {
            return;
        }
        if (other instanceof UnknownValue) {
            UnknownValue sv = (UnknownValue) other;
            MergeData newEA = sv.mergeData;
            if (null == this.mergeData) {
                this.mergeData = new MergeData();
            }
            this.mergeData.merge(newEA.getAnalyzedLogs());

            for (Map.Entry<String, Integer> entry : sv.ipCountMap.entrySet()) {
                final Integer oldCount = this.ipCountMap.get(entry.getKey());

                if (oldCount != null) {
                    this.ipCountMap.put(entry.getKey(), oldCount + entry.getValue());
                } else if (this.ipCountMap.size() < 50) {
                    this.ipCountMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
