/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.analysis;

import io.holoinsight.server.apm.common.model.specification.sw.ErrorInfo;
import io.holoinsight.server.apm.common.utils.TimeBucket;
import io.holoinsight.server.apm.grpc.trace.SegmentObject;
import io.holoinsight.server.apm.grpc.trace.SpanObject;

import org.springframework.stereotype.Service;

@Service
public class ErrorAnalysis {

  public ErrorInfo analysis(SpanObject span, SegmentObject segmentObject) {
    if (span.getLogsList().size() > 0) {
      ErrorInfo errorInfo = new ErrorInfo();
      errorInfo.setServiceName(segmentObject.getService());
      errorInfo.setServiceInstanceName(segmentObject.getServiceInstance());
      errorInfo.setTraceId(segmentObject.getTraceId());
      errorInfo.setStartTime(span.getStartTime());
      errorInfo.setEndTime(span.getEndTime());
      errorInfo.setTimeBucket(TimeBucket.getRecordTimeBucket(span.getStartTime()));

      span.getLogs(0).getDataList().forEach(keyStringValuePair -> {
        if ("error.kind".equals(keyStringValuePair.getKey())) {
          errorInfo.setErrorKind(keyStringValuePair.getValue());
        }
      });
      return errorInfo;
    }
    return null;
  }
}
