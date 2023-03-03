/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiwliu
 * @version : Segment.java, v 0.1 2022年09月16日 16:12 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Segment extends Source {

  private static final long serialVersionUID = 7257195431774605710L;

  private String agentVersion;
  private String segmentId;
  private String serviceName;
  private String serviceInstanceName;
  private String endpointName;
  private int latency;
  private int isError;
  private byte[] dataBinary;
  private List<Tag> tags = new ArrayList<>();
  private int hasEntry;

  // for bizops
  private String entryLayer;
  private String entryRootErrorCode;
  private String entryErrorCode;
  private String stamp;

  @Override
  public String getEntityId() {
    return segmentId;
  }

}
