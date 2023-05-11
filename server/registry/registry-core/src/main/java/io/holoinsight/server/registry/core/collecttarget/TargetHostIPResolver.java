package io.holoinsight.server.registry.core.collecttarget;

import io.holoinsight.server.registry.core.template.CollectTemplate;

/**
 * 
 * <p>
 * created at 2023/5/11
 *
 * @author xzchaoo
 */
public interface TargetHostIPResolver {
  /**
   * Try to resolve the host ip of target. Returns null if not found.
   * 
   * @param t
   * @param target
   * @return
   */
  String getHostIP(CollectTemplate t, Target target);
}
