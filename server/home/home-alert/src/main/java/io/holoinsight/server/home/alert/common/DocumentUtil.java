/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import org.dom4j.io.SAXReader;

public class DocumentUtil {


  public static String EGE = "http://xml.org/sax/features/external-general-entities";
  public static String EPE = "http://xml.org/sax/features/external-parameter-entities";
  public static String LED = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

  public static SAXReader saxReader = new SAXReader();

  public static SAXReader generateSaxReader() {
    try {
      saxReader.setFeature(EPE, false);
      saxReader.setFeature(EGE, false);
      saxReader.setFeature(LED, false);
    } catch (Exception e) {
      // This catch statement is intentionally empty
    }
    return saxReader;
  }
}
