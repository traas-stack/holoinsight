/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * Pair class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: Pair.java, v 0.1 2022年02月24日 5:05 下午 jinsong.yjs Exp $
 */
@EqualsAndHashCode
@ToString
public class Pair<L, R> implements Serializable {

  private static final long serialVersionUID = -1292525629741844984L;

  private L left;

  private R right;

  /**
   * <p>
   * Constructor for Pair.
   * </p>
   */
  public Pair() {}

  /**
   * <p>
   * Constructor for Pair.
   * </p>
   */
  public Pair(L l, R r) {
    this.left = l;
    this.right = r;
  }

  /**
   * <p>
   * left.
   * </p>
   */
  public L left() {
    return left;
  }

  /**
   * <p>
   * right.
   * </p>
   */
  public R right() {
    return right;
  }

}
