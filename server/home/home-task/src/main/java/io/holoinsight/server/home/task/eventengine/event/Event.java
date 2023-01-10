/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.task.eventengine.event;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jsy1001de
 * @version 1.0: Event.java, v 0.1 2022年04月07日 11:36 上午 jinsong.yjs Exp $
 */
@Data
public class Event {
    private String          topic;

    private String          data;

    private EventStatusEnum status;

    private int             retryTimes;

    public Event(String topic) {
        this.topic = topic;
    }

    public Event(String topic, String data) {
        this.topic = topic;
        this.data = data;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(topic);
    }

}