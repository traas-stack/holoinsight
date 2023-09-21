/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.model;

import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.FlatColumn;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static io.holoinsight.server.apm.engine.model.EventDO.INDEX_NAME;


/**
 * @author sw1136562366
 * @version : EventDO.java, v 0.1 2023年08月11日 20:34 sw1136562366 Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class EventDO extends RecordDO {

  public static final String INDEX_NAME = "holoinsight-event";

  public static final String TENANT = "tenant";
  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String TYPE = "type";
  public static final String TAGS = "tags";

  @Column(name = TENANT)
  private String tenant;
  @Column(name = ID)
  private String id;
  @Column(name = NAME)
  private String name;
  @Column(name = TYPE)
  private String type;
  @FlatColumn
  private Map<String, String> tags;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

}
