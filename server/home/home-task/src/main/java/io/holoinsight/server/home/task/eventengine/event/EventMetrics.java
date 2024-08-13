/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: EventMetrics.java, v 0.1 2022年04月07日 11:39 上午 jinsong.yjs Exp $
 */
@Data
public class EventMetrics {

  private Date timestamp;

  private List<EventMetric> eventMetrics;

  public void add(EventMetric e) {
    if (null == this.eventMetrics) {
      this.eventMetrics = new ArrayList<EventMetric>();
    }

    this.eventMetrics.add(e);
  }

  public void merge(EventMetrics eventMetrics) {
    if (null == this.eventMetrics) {
      this.eventMetrics = new ArrayList<EventMetric>();
    }

    for (EventMetric eventMetric : eventMetrics.getEventMetrics()) {

      if (this.eventMetrics.contains(eventMetric)) {
        EventMetric metric = this.eventMetrics.get(this.eventMetrics.indexOf(eventMetric));
        metric.setValue(metric.getValue() + eventMetric.getValue());
      } else {
        this.eventMetrics.add(eventMetric);
      }
    }
  }

  public int get(String metricName) {
    if (null != this.eventMetrics) {
      for (EventMetric eventMetric : this.eventMetrics) {
        if (eventMetric.getName().equals(metricName)) {
          return eventMetric.getValue();
        }
      }
    }

    return 0;
  }

  public List<EventMetric> getEventMetrics() {
    if (null == this.eventMetrics) {
      this.eventMetrics = new ArrayList<EventMetric>();
    }

    return this.eventMetrics;
  }
}
