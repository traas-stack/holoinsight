/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package extensions.org.awaitility.core.ConditionFactory;

import org.awaitility.core.ConditionFactory;
import org.awaitility.core.ThrowingRunnable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

/**
 * <p>
 * created at 2023/4/20
 *
 * @author xzchaoo
 */
@Extension
public class ConditionFactoryExtension {
  /**
   * <p>
   * This method treats all exceptions as retryable errors.
   * <p>
   * When use {@link ConditionFactory#untilAsserted(ThrowingRunnable)}, only {@link AssertionError}
   * will be treated as expected errors and then retry tests. Other exceptions will lead to test
   * failure.
   * <p>
   * But it is also very common to encounter the following exceptions when using `rest-assured`.
   * These exceptions should be treated as expected errors too.
   * <ul>
   * <li>{@link NullPointerException}</li>
   * <li>{@link org.apache.http.NoHttpResponseException}</li>
   * </ul>
   * 
   * @param self
   * @param run
   */
  public static void untilNoException(@This ConditionFactory self, ThrowingRunnable run) {
    self.untilAsserted(() -> {
      try {
        run.run();
      } catch (Exception e) {
        throw new AssertionError("wraps non AssertionError exception", e);
      }
    });
  }
}
