/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.Filter;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import io.holoinsight.server.test.it.env.DockerComposeEnv;
import io.holoinsight.server.test.it.env.Env;
import io.holoinsight.server.test.it.utils.DockerHolder;
import io.holoinsight.server.test.it.utils.MemoryTestExecutionListener;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
@Slf4j
public class IntegrationBootstrapHelper {
  protected static void run0(Supplier<Env> envProvider, Class<?>... classes) throws Exception {
    run0(envProvider, Arrays.stream(classes).collect(Collectors.toList()));
  }

  protected static void run0(Supplier<Env> envProvider, List<DiscoverySelector> selectors,
      List<Filter<?>> filters) throws Exception {
    LauncherDiscoveryRequestBuilder b = LauncherDiscoveryRequestBuilder.request();
    b.selectors(selectors).filters(filters.toArray(new Filter[0]));
    LauncherDiscoveryRequest request = b.build();
    run0(envProvider, request);
  }

  protected static void run0(Supplier<Env> envProvider, List<Class<?>> classes) throws Exception {
    LauncherDiscoveryRequestBuilder b = LauncherDiscoveryRequestBuilder.request();
    classes.stream().map(DiscoverySelectors::selectClass).forEach(b::selectors);
    LauncherDiscoveryRequest request = b.build();
    run0(envProvider, request);
  }

  public static void run0(Supplier<Env> envProvider, LauncherDiscoveryRequest request)
      throws Exception {
    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    MemoryTestExecutionListener memoryListener = new MemoryTestExecutionListener();
    try (LauncherSession session = LauncherFactory.openSession()) {
      Launcher launcher = session.getLauncher();
      launcher.registerTestExecutionListeners(listener, memoryListener);
      launcher.registerTestExecutionListeners(
          LoggingListener.forBiConsumer(new BiConsumer<Throwable, Supplier<String>>() {
            @Override
            public void accept(Throwable throwable, Supplier<String> stringSupplier) {
              if (throwable != null) {
                log.error(stringSupplier.get(), throwable);
              } else {
                log.info(stringSupplier.get());
              }
            }
          }));
      TestPlan testPlan = launcher.discover(request);
      if (!testPlan.containsTests()) {
        throw new IllegalStateException("IT not found");
      }
      Env<?> env = envProvider.get();
      try {
        env.start();
        DockerHolder.DCC = env.getCompose();
        launcher.execute(testPlan);
      } finally {
        DockerHolder.DCC = null;
        env.stop();
      }
    }
    TestExecutionSummary summary = listener.getSummary();
    // Don't close System.out !
    PrintWriter pw = new PrintWriter(System.out);
    summary.printTo(pw);
    summary.printFailuresTo(pw);

    printResult(memoryListener);

    if (summary.getTestsFailedCount() > 0) {
      throw new IllegalStateException("IT fail");
    }
  }

  private static void printResult(MemoryTestExecutionListener memoryListener) {
    log.info("");
    log.info("result begin");

    log.info("success tests:");
    for (val e : memoryListener.getDisplayRecords().entrySet()) {
      String id = e.getKey();
      MemoryTestExecutionListener.Record r = e.getValue();
      if (r.skipReason == null && r.result.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
        log.info("[{}], cost=[{}s]", id, r.costSeconds());
      }
    }
    log.info("");

    log.info("failure tests:");
    for (val e : memoryListener.getDisplayRecords().entrySet()) {
      String id = e.getKey();
      MemoryTestExecutionListener.Record r = e.getValue();
      if (r.skipReason == null && r.result.getStatus() != TestExecutionResult.Status.SUCCESSFUL) {
        log.info("[{}] [{}], cost=[{}s]", id, r.result.getStatus(), r.costSeconds());
      }
    }
    log.info("");

    log.info("skipped tests:");
    for (val e : memoryListener.getDisplayRecords().entrySet()) {
      String id = e.getKey();
      MemoryTestExecutionListener.Record r = e.getValue();
      if (r.skipReason != null) {
        log.info("[{}], reason=[{}]", id, r.skipReason);
      }
    }
    log.info("");

    log.info("result end");
    log.info("");
  }

  protected static void runBySceneAndGroup(LauncherDiscoveryRequest request) throws Exception {
    try (LauncherSession session = LauncherFactory.openSession()) {
      Launcher launcher = session.getLauncher();

      TestPlan testPlan = launcher.discover(request);

      Map<SceneAndGroup, Set<Class<?>>> groups = new HashMap<>();

      for (TestIdentifier ti : testPlan.getRoots()) {

        Set<TestIdentifier> children = testPlan.getChildren(ti);
        for (TestIdentifier clazzTI : children) {
          if (!clazzTI.getSource().isPresent()) {
            continue;
          }
          ClassSource classSource = (ClassSource) clazzTI.getSource().get();
          List<Tag> tags =
              AnnotationUtils.findRepeatableAnnotations(classSource.getJavaClass(), Tag.class);
          String foundScene = null;
          String foundGroup = null;
          for (Tag tag : tags) {
            String tagValue = tag.value();
            if (tagValue.startsWith("scene-")) {
              if (foundScene != null) {
                throw new IllegalStateException("duplicated scene");
              }
              foundScene = tagValue;
            } else if (tagValue.startsWith("group-")) {
              if (foundGroup != null) {
                throw new IllegalStateException("duplicated group");
              }
              foundGroup = tagValue;
            }
          }
          if (foundScene == null) {
            foundScene = "scene-default";
          }
          if (foundGroup == null) {
            foundGroup = "group-default";
          }
          groups.computeIfAbsent(new SceneAndGroup(foundScene, foundGroup), i -> new HashSet<>())
              .add(classSource.getJavaClass());
        }
      }

      for (Map.Entry<SceneAndGroup, Set<Class<?>>> e : groups.entrySet()) {
        String scene = e.getKey().getScene();
        Set<Class<?>> classes = e.getValue();
        log.info("run scene=[{}] tests=[{}]", scene, classes.size());

        List<DiscoverySelector> selectors = new ArrayList<>(classes.size());
        for (Class<?> clazz : classes) {
          selectors.add(selectClass(clazz));
        }

        LauncherDiscoveryRequest request2 =
            LauncherDiscoveryRequestBuilder.request().selectors(selectors).build();
        run0(() -> prepareScene(scene), request2);
      }
    }
  }

  public static Env<?> prepareScene(String name) {
    return new DockerComposeEnv(name);
  }

}
