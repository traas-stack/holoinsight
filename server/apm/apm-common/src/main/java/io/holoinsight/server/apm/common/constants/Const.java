/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.constants;

public class Const {
  public static final String NONE = "0";
  public static final String SERVICE_ID_CONNECTOR = ".";
  public static final String SERVICE_ID_PARSER_SPLIT = "\\.";
  public static final String ID_CONNECTOR = "_";
  public static final String ID_PARSER_SPLIT = "\\_";
  public static final String RELATION_ID_CONNECTOR = "-";
  public static final String RELATION_ID_PARSER_SPLIT = "\\-";
  public static final String LINE = "-";
  public static final String COMMA = ",";
  public static final String SPACE = " ";
  public static final String KEY_VALUE_SPLIT = ",";
  public static final String ARRAY_SPLIT = "|";
  public static final String ARRAY_PARSER_SPLIT = "\\|";
  public static final String USER_SERVICE_NAME = "User";
  public static final String USER_INSTANCE_NAME = "User";
  public static final String USER_ENDPOINT_NAME = "User";
  public static final String SEGMENT_SPAN_SPLIT = "S";
  public static final String UNKNOWN = "Unknown";
  public static final String EMPTY_STRING = "";
  public static final String POINT = ".";
  public static final String DOUBLE_COLONS_SPLIT = "::";

  public static final String TENANT = "tenant";

  public static final String SW_REF_NETWORK_ADDRESSUSEDATPEER = "network.AddressUsedAtPeer";
  public static final String SW_REF_PARENT_ENDPOINT = "parent.endpoint";
  public static final String SW_REF_PARENT_SERVICE = "parent.service";
  public static final String SW_REF_PARENT_SERVICE_INSTANCE_NAME = "parent.service.instance.name";
  public static final String SW_REF_REFTYPE = "refType";
  public static final String SW_ATTR_COMPONENT = "sw8.component";
  public static final String OTLP_RESOURCE_SERVICE_INSTANCE_NAME = "service.instance.name";

  public static final String OTLP_ATTRIBUTES_PREFIX = "attributes.";

  public static final String OTLP_RESOURCE_PREFIX = "resource.";

  // slow sql latency threshold
  public static final long SLOW_SQL_THRESHOLD = 300;

  public static final String SOURCE = "source";
  public static final String DEST = "dest";

  // custom tags
  public static final String OTLP_SPANLAYER = "spanLayer";

}
