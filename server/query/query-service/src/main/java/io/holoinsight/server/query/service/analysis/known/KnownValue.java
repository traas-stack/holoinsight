/**
 * Alipay.com Inc. Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.holoinsight.server.query.service.analysis.known;

import io.holoinsight.server.query.service.analysis.Mergable;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanpeng.xwp
 * @version : EventDetailValue.java, v 0.1 2020年03月05日 14:34 wanpeng.xwp Exp $
 */
@Data
public class KnownValue implements Mergable, Comparable {

    private static final long serialVersionUID = 1204956131343394884L;

    // 样本
    private String sample;
    private Integer count;
    private Map<String, Integer> ipCountMap = new HashMap<>();

    public KnownValue() {
    }

    public Map<String, Integer> getIpCountMap() {
        return ipCountMap;
    }

    public KnownValue(String sample) {
        super();
        this.sample = sample;
    }

    @Override
    public void merge(Mergable other) {
        if (null == other) {
            return;
        }
        if (other instanceof KnownValue) {
            KnownValue es = (KnownValue) other;
            if (null != es.count) {
                this.count += es.count;
            }

            for (Map.Entry<String, Integer> entry : es.ipCountMap.entrySet()) {
                final Integer oldCount = this.ipCountMap.get(entry.getKey());

                if (oldCount != null) {
                    this.ipCountMap.put(entry.getKey(), oldCount + entry.getValue());
                } else if (this.ipCountMap.size() < 50) {
                    this.ipCountMap.put(entry.getKey(), entry.getValue());
                }
            }

        }
    }

    @Override
    public int compareTo(Object o) {
        if (null == o) {
            return 1;
        }
        if (!(o instanceof KnownValue)) {
            return 1;
        }
        return count - ((KnownValue) o).count;
    }
}
