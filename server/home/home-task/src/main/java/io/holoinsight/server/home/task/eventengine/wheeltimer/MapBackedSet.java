/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.wheeltimer;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MapBackedSet.java, v 0.1 2022年04月07日 11:47 上午 jinsong.yjs Exp $
 */
public class MapBackedSet<E> extends AbstractSet<E> implements Serializable {

  private static final long serialVersionUID = -6761513279741915432L;

  private final Map<E, Boolean> map;

  MapBackedSet(Map<E, Boolean> map) {
    this.map = map;
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean contains(Object o) {
    return map.containsKey(o);
  }

  @Override
  public boolean add(E o) {
    return map.put(o, Boolean.TRUE) == null;
  }

  @Override
  public boolean remove(Object o) {
    return map.remove(o) != null;
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }
}
