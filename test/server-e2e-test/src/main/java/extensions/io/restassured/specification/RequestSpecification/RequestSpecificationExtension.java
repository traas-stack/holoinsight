/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package extensions.io.restassured.specification.RequestSpecification;

import org.json.JSONObject;

import io.holoinsight.server.common.J;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

/**
 * <p>
 * created at 2023/3/11
 *
 * @author xzchaoo
 */
@Extension
public class RequestSpecificationExtension {
  /**
   * <p>
   * Add extension method to {@link RequestSpecification}.
   * <p>
   * Usage:
   * 
   * <pre>
   *     JSONObject body = new ...;
   *     given().body(body);
   * </pre>
   * 
   * @param spec
   * @param json
   * @return
   */
  public static RequestSpecification body(@This RequestSpecification spec, JSONObject json) {
    return spec.body(json.toString()).contentType(ContentType.JSON);
  }

  /**
   * <p>
   * Add extension method to {@link RequestSpecification}.
   * <p>
   * Usage:
   * 
   * <pre>
   *     Object pojo = new ...;
   *     given().jsonBody(polo);
   * </pre>
   * 
   * @param spec
   * @param body
   * @return
   */
  public static RequestSpecification jsonBody(@This RequestSpecification spec, Object body) {
    return spec.body(J.toJson(body)).contentType(ContentType.JSON);
  }
}
