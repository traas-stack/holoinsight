/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The part obtained after log analysis can be considered as a keyword in the log.
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
class LAPart {
  private String content;
  private boolean source;
  private boolean important;
  private int count;

  /**
   * The content of latterContent is content with all non-letter characters removed.
   */
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient String letterContent;

  public String getLetterContent(StringBuilder reuse) {
    if (letterContent != null) {
      return letterContent;
    }

    StringBuilder sb = reuse;
    if (sb == null) {
      sb = new StringBuilder();
    }
    sb.setLength(0);

    for (int k = 0; k < content.length(); k++) {
      char fc = content.charAt(k);
      if (Character.isLetter(fc)) {
        sb.append(fc);
      }
    }

    letterContent = sb.toString();
    return letterContent;
  }

  public boolean isSimilarTo(StringBuilder reuse, LAPart t) {
    String a = this.getLetterContent(reuse);
    String b = t.getLetterContent(reuse);
    return a.equals(b);
  }
}
