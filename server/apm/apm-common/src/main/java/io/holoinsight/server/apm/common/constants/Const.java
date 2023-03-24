/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.constants;

import java.util.ArrayList;
import java.util.List;

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
  public static final String SW_REF_PARENT_SERVICE_INSTANCE_ID = "parent.service.instance";
  public static final String SW_REF_PARENT_SERVICE_INSTANCE_NAME = "parent.service.instance.name";
  public static final String SW_REF_REFTYPE = "refType";
  public static final String SW_ATTR_COMPONENT = "sw8.component";
  public static final String OTLP_RESOURCE_SERVICE_INSTANCE_NAME = "service.instance.name";
  public static final String OTLP_RESOURCE_HOST_NAME = "host.name";
  public static final String OTLP_SPANLAYER = "spanLayer";

  // 小程序云自定义的tag
  public static final String BIZSTAMP = "bizStamp";
  public static final String STAMP = "stamp";
  public static final String ISENTRY = "isEntry";
  public static final String ISENTRYTAG = "isEntry=1";

  public static final String ERRORCODE = "errorCode";
  public static final String ROOTERRORCODE = "rootErrorCode";
  public static final String APPID = "appId";
  public static final String ENVID = "envId";

  // slow sql latency threshold
  public static final long SLOW_SQL_THRESHOLD = 300;

  public static final List<String> searchableTagKeys = new ArrayList() {
    {
      add("http.method");
      add("http.status_code");
      add("rpc.status_code");
      add("db.type");
      add("db.instance");
      add("mq.queue");
      add("mq.topic");
      add("mq.broker");
      add(ISENTRY);
      add(ERRORCODE);
      add(ROOTERRORCODE);
      add(STAMP);
      add(APPID);
      add(ENVID);
    }
  };

  public static final String SOURCE = "source";
  public static final String DEST = "dest";


}
