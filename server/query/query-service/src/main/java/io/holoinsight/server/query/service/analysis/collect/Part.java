/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

/**
 * @author xiangwanpeng
 * @version : Part.java, v 0.1 2020年01月09日 15:29 xiangwanpeng Exp $
 */
@Data
public class Part {
  private static final long serialVersionUID = -4805874841620514575L;
  String content;
  boolean source;
  boolean important;
  int count;

  public Part(String content, boolean isSource, boolean importantMode, int count) {
    super();
    this.content = content;
    this.source = isSource;
    this.important = importantMode;
    this.count = count;
  }

  public int partSize() {
    return content.length();
  }

  public void add(int toadd) {
    count += toadd;
  }
}
