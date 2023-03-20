/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package extensions.io.restassured.response.ValidatableResponse;

import java.util.function.Function;

import org.hamcrest.Matchers;

import io.restassured.response.ValidatableResponse;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

/**
 * <p>
 * created at 2023/3/12
 *
 * @author xzchaoo
 */
@Extension
public class ValidatableResponseExtension {

  public static ValidatableResponse isSuccess(@This ValidatableResponse spec) {
    return spec.body("success", Matchers.is(true));
  }

  public static ValidatableResponse visit(@This ValidatableResponse spec,
      Function<ValidatableResponse, ValidatableResponse> f) {
    return f.apply(spec);
  }
}
