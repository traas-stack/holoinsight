/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package extensions.io.restassured.specification.RequestSpecification;

import org.hamcrest.Matchers;
import org.json.JSONObject;

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
  public static RequestSpecification body(@This RequestSpecification spec, JSONObject json) {
    return spec.body(json.toString()).contentType(ContentType.JSON);
  }
}
