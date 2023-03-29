/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.DockerComposeContainer;

import io.holoinsight.server.test.it.utils.DockerHolder;
import io.holoinsight.server.test.it.utils.DockerUtils;
import io.holoinsight.server.test.it.utils.HackConditionEvaluationLogger;
import io.restassured.RestAssured;
import io.restassured.specification.Argument;
import io.restassured.specification.RequestSpecification;
import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * <p>
 * Integration test base class, providing many commonly used helper methods.
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT extends Matchers {
  protected static final Matcher<?> IS_TRUE = Matchers.is(true);
  protected static final Matcher<?> IS_FALSE = Matchers.is(false);
  protected static final Matcher<?> IS_NULL = Matchers.nullValue();
  protected static final Matcher<?> NOT_NULL = Matchers.notNullValue();

  protected static final Duration DEFAULT_AWAIT_TIMEOUT = Duration.ofMinutes(5);
  protected static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(5);

  @BeforeAll
  public static void baseETBeforeAll() {
    setupBaseURI();
  }

  protected static RequestSpecification given() {
    return RestAssured.given();
  }

  @SneakyThrows
  private static void setupBaseURI() {
    DockerComposeContainer<?> dcc = DockerHolder.DCC;
    if (dcc != null) {
      // We are running in integration mode.
      RestAssured.baseURI = DockerUtils.getHttpAddr(dcc, "server", 80);
      return;
    }

    String baseURI = System.getProperty("holoinsight.it.baseURI");
    if (StringUtils.isEmpty(baseURI)) {
      baseURI = System.getProperty("HOLOINSIGHT_IT_BASE_URI");
    }

    if (StringUtils.isEmpty(baseURI)) {
      File file = new File(System.getProperty("user.home"), ".holoinsight-IT.properties");
      if (file.exists()) {
        Properties properties = new Properties();
        @Cleanup
        FileInputStream fis = new FileInputStream(file);
        properties.load(fis);
        baseURI = properties.getProperty("baseURI");
      }
    }

    if (StringUtils.isEmpty(baseURI)) {
      baseURI = RestAssured.DEFAULT_URI;
    }

    RestAssured.baseURI = baseURI;
  }

  protected static JSONObject json() {
    return new JSONObject();
  }

  protected static JSONObject json(String key, Object value) {
    return new JSONObject().put(key, value);
  }

  protected static JSONArray jarray() {
    return new JSONArray();
  }

  protected static <T> Matcher<T> eq(T t) {
    return Matchers.equalTo(t);
  }

  protected static <T extends Comparable<T>> Matcher<T> gt(T t) {
    return Matchers.greaterThan(t);
  }

  protected static <T> List<T> newArrayList(T... list) {
    return Lists.newArrayList(list);
  }


  protected static ConditionFactory await(String alias) {
    return Awaitility.await(alias) //
        .conditionEvaluationListener(new HackConditionEvaluationLogger()) //
        .atMost(DEFAULT_AWAIT_TIMEOUT) //
        .pollInterval(DEFAULT_POLL_INTERVAL) //
        // start poll right now
        .pollDelay(Duration.ZERO) //
    ;
  }

  protected static ConditionFactory await() {
    return await(null);
  }

  protected static List<Argument> withArgs(Object firstArgument, Object... additionalArguments) {
    return RestAssured.withArgs(firstArgument, additionalArguments);
  }

  protected static long now() {
    return System.currentTimeMillis();
  }

  protected static InputStream getResourceAsStream(String name) {
    return BaseIT.class.getClassLoader().getResourceAsStream(name);
  }

  protected static JSONObject getJsonFromClasspath(String name) {
    try (InputStream is = getResourceAsStream(name)) {
      return new JSONObject(new JSONTokener(is));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
