/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import static io.holoinsight.server.registry.core.lock.TargetProtector.executeInWriteLock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xzchaoo.commons.hashedeventloop.DisruptorEventLoopManager;
import com.xzchaoo.commons.hashedeventloop.EventLoopManagerConfig;
import com.xzchaoo.commons.stat.StringsKey;

import io.holoinsight.server.common.MetricsUtils;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.CollectTemplateDelta;
import io.holoinsight.server.registry.core.template.TemplateConfig;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Component
public class CollectTargetMaintainer {
  private static final Logger LOGGER = LoggerFactory.getLogger("TARGET");
  private static final int UPDATE_NO_CHANGE = 0;
  private static final int UPDATE_INDEX = 1;
  private static final int UPDATE_NO_INDEX = 2;
  private static final int UPDATE_ERROR = 3;
  @Autowired
  private CollectTargetStorage collectTargetStorage;
  @Autowired
  private CollectTargetService collectTargetService;
  private DisruptorEventLoopManager m;
  @Autowired
  private CommonThreadPools commonThreadPools;
  @Autowired
  private TemplateConfig templateConfig;

  @PostConstruct
  public void postConstruct() {
    MetricsUtils.SM.gauge(StringsKey.of("target.count"), collectTargetStorage,
        CollectTargetStorage::size);

    EventLoopManagerConfig config = new EventLoopManagerConfig();
    config.setName("CollectTargetMaintainer");
    config.setSize(Runtime.getRuntime().availableProcessors());
    // TODO buffer该多大
    config.setBufferSize(65536);
    config.setBlockOnInsufficientCapacity(true);
    m = new DisruptorEventLoopManager(config);
    m.start();
  }

  @PreDestroy
  public void preDestroy() {
    m.stop();
  }

  /**
   * 在当前线程上维护template
   *
   * @param t
   */
  public void maintainTemplateNow(CollectTemplate t) {
    long seconds = templateConfig.getBuild().getTimeout().getSeconds();
    ScheduledFuture<?> future = commonThreadPools.getScheduler().schedule(() -> {
      LOGGER.error("maintainTemplateNow timeout [{}/{}]", t.getId(), t.getTableName());
    }, seconds, TimeUnit.SECONDS);

    MaintainStat stat = new MaintainStat();
    try {
      maintainTemplate1(t, stat);
    } catch (Throwable e) {
      LOGGER.error("maintain template error {}", t.getId(), e);
    } finally {
      future.cancel(true);
    }
    LOGGER.info("mt [{}/{}] stat={}", t.getId(), t.getTableName(), stat);
  }

  public Mono<Void> publish(String tableName, Runnable r) {
    Sinks.One<Void> sink = Sinks.one();
    m.publish(tableName.hashCode(), () -> {
      try {
        r.run();
      } catch (Throwable e) {
        sink.tryEmitError(e);
      } finally {
        sink.tryEmitEmpty();
      }
    });
    return sink.asMono();
  }

  public Mono<Void> publish(CollectTemplate t, Runnable r) {
    return publish(t.getTableName(), r);
  }

  /**
   * 该方法不允许失败
   *
   * @param d
   * @return
   */
  public Mono<Void> maintainTemplateDelta(CollectTemplateDelta d) {
    return publish(d.getTableName(), () -> {
      maintainTemplateDelta0(d);
    });
  }

  public void maintainTemplateDelta0(CollectTemplateDelta d) {
    long seconds = templateConfig.getBuild().getTimeout().getSeconds();
    ScheduledFuture<?> future = commonThreadPools.getScheduler().schedule(() -> {
      LOGGER.error("maintainTemplateDelta timeout [{}]", d.getTableName());
    }, seconds, TimeUnit.SECONDS);

    List<Runnable> pending = new ArrayList<>();
    try {
      maintainTemplateDelta1(d, pending);
    } catch (Throwable e) {
      LOGGER.error("fail to maintain template {}", d.getTableName(), e);
    } finally {
      future.cancel(true);
      executePending(pending, true);
    }
  }

  private void maintainTemplateDelta1(CollectTemplateDelta d, List<Runnable> pending) {
    // TODO 减少持有锁的时间
    if (d.getDelete().size() > 0) {
      pending.add(() -> {
        for (CollectTemplate t : d.getDelete()) {
          collectTargetStorage.deleteByTemplateId(t.getId());
        }
      });
    }

    CollectTemplate add0 = d.getAdd0();

    // TODO 由于这里我不想引用 templateStorage
    // 因此有一种情况无法正确处理: 就是内存里存在一个db里已经没有的tableName了
    // CollectTemplate oldT = templateStorage.get(d.getTableName());

    // if (oldT != null) {
    // if (add0 != null && oldT.getId() == add0.getId()) {
    // // 同一个, 没必要处理
    // return;
    // }
    // pending.add(() -> taskStorage.deleteByTemplateId(oldT.getId()));
    // }

    if (add0 == null) {
      return;
    }

    // 该template已经被维护过了, 没必要再维护了
    if (collectTargetStorage.hasTemplateId(add0.getId())) {
      return;
    }

    List<Target> targets = collectTargetService.getTargets(add0);
    if (targets.isEmpty()) {
      return;
    }

    // TODO 其实可以并发构建 使用 forkjoin
    List<CollectTarget> needAdd = new ArrayList<>(targets.size());
    for (Target target : targets) {
      try {
        CollectTarget ct = build(add0, target);
        needAdd.add(ct);
      } catch (Throwable e) {
        LOGGER.error("build collect collecttarget error [{}/{}/{}]", add0.getId(),
            add0.getTableName(), target.getId(), e);
      }
    }

    executeInWriteLock(() -> {
      // 已经在锁里了, 没必要再加锁了
      executePending(pending, false);
      collectTargetStorage.batchPut(add0.getId(), needAdd);
      LOGGER.info("mtd [{}] add=[{}]", d.getTableName(), needAdd.size());
    });
  }

  /**
   * 必须保证runnable不抛异常 否则行为无法定义
   *
   * @param pending
   * @param lock
   */
  private void executePending(List<Runnable> pending, boolean lock) {
    if (pending.isEmpty()) {
      return;
    }

    if (lock) {
      executeInWriteLock(() -> {
        for (Runnable r : pending) {
          r.run();
        }
      });
    } else {
      for (Runnable r : pending) {
        r.run();
      }
    }
  }

  private void maintainTemplate1(CollectTemplate t, MaintainStat stat) {
    stat.setBegin(System.currentTimeMillis());

    // 查出匹配的dimrows
    List<Target> targets = collectTargetService.getTargets(t);
    stat.setListDimEnd(System.currentTimeMillis());
    stat.setDims(targets.size());

    // template是不变的, 但dim的内容可能会变化(id保持不变), 此时会潜在导致发生变化 refAgent, 采集目标元数据发生变化
    // 因此我们不得不做一下 CollectTask 的内容的对比, 而不能仅仅只是比较key(这样就太简单了)

    // 这里之所以要把old get
    Map<CollectTargetKey, CollectTarget> old = collectTargetStorage.getMapByTemplateId(t.getId());
    stat.oldSize = old.size();

    CollectTarget reuse = new CollectTarget();

    List<CollectTarget> needAdd = new ArrayList<>();
    // 涉及index的改动
    List<CollectTarget> needUpdateIndex = new ArrayList<>();
    // 没有涉及索引的update直接原地修改对象即可
    // List<CollectTask> needUpdateNoIndex = new ArrayList<>();

    for (Target target : targets) {
      CollectTargetKey key = CollectTargetKey.of(t.getId(), target);
      // TODO 这里 99% 都是 old.get + old.remove 能否省略成一次 old.remove
      CollectTarget oldT = old.remove(key);
      if (oldT == null) {
        try {
          CollectTarget ct = build(t, target);
          needAdd.add(ct);
          LOGGER.info("mt a [{}/{}] dim={}", ct.getTemplateId(), ct.getDimId(), target.getInner());
        } catch (Throwable e) {
          // TODO 静默 统计就好, 采样几个
          LOGGER.error("build collecttarget error [{}/{}]", t.getId(), target.getId(), e);
        }
      } else {
        // 是个update
        // TODO 结果有3种:
        // 1. 发生了影响索引的变化
        // 2. 发生了不影响索引, 但是影响内容的变化
        // 3. 没有发生变化或者变化无关痛痒

        int result = maybeUpdate(t, target, oldT, reuse);
        switch (result) {
          case UPDATE_INDEX:
            // reuse 已经被用了 此处需要new一个
            needUpdateIndex.add(reuse);
            reuse = new CollectTarget();
            LOGGER.info("mt u [{}/{}] dim={}", oldT.getTemplateId(), key.getDimId(),
                target.getInner());
            break;
          case UPDATE_NO_INDEX:
            // TODO 要修改reuse吗? 其实不用吧
            break;
          case UPDATE_NO_CHANGE:
            break;
          case UPDATE_ERROR:
            // TODO error 说明什么? 旧comb存在, 但无法构建出新comb, 基本都是因为refAgent
            // 那相当于旧的要删掉了
            // 此处无需调用
            // old.remove(key);
            break;
        }

        // TODO 解释
        if (result == UPDATE_ERROR) {
          old.put(key, oldT);
        }
      }
    }
    stat.setBuildEnd(System.currentTimeMillis());

    // 此时:
    // needAdd: 是需要新增的
    // needUpdateIndex: 是发生变化的
    // old.keySet() 是需要删除的

    // 99% 配置没改变 减少没必要的加锁
    boolean hasModify = needAdd.size() > 0 || old.size() > 0 || needUpdateIndex.size() > 0;
    if (hasModify) {
      for (Map.Entry<CollectTargetKey, CollectTarget> e : old.entrySet()) {
        LOGGER.info("mt d [{}/{}]", e.getKey().getTemplateId(), e.getKey().getDimId());
      }
      // 加锁保护, 防止中间状态被读到
      executeInWriteLock(() -> {
        stat.setLockEnd(System.currentTimeMillis());
        // apply add
        collectTargetStorage.batchPut(t.getId(), needAdd);
        stat.add += needAdd.size();

        // apply delete
        if (old.size() > 0) {
          stat.delete += old.size();
          collectTargetStorage.batchDelete(t.getId(), old.keySet());
        }

        stat.update += needUpdateIndex.size();
        // apply update
        collectTargetStorage.batchPut(t.getId(), needUpdateIndex);
      });
    }

    stat.setNewSize(collectTargetStorage.countByTemplate(t.getId()));

    stat.setEnd(System.currentTimeMillis());
  }

  /**
   * @param t
   * @param target
   */
  private CollectTarget build(CollectTemplate t, Target target) {
    String refAgent = collectTargetService.buildRefAgent(t, target);
    CollectTarget ct = new CollectTarget();
    ct.setKey(CollectTargetKey.of(t.getId(), target));
    ct.setRefAgent(refAgent);
    // 或者应该使用version更好?
    ct.setDimModifiedTime(target.getGmtModified());
    ct.setDimVersion(target.getVersion());
    return ct;
  }

  /**
   * @param t
   * @param target
   * @param old
   * @param to
   * @return 如果发生了实质性变化请返回true, 发生了一些无关同样的变化或者没有变化则返回false
   */
  private int maybeUpdate(CollectTemplate t, Target target, CollectTarget old, CollectTarget to) {
    // 这些无所谓 直接更新
    old.setDimModifiedTime(target.getGmtModified());
    old.setDimVersion(target.getVersion());

    String refAgent;
    try {
      refAgent = collectTargetService.buildRefAgent(t, target);
    } catch (Throwable e) {
      LOGGER.error("build ref agent error", e);
      return UPDATE_ERROR;
    }
    if (refAgent.equals(old.getRefAgent())) {
      return UPDATE_NO_CHANGE;
    }

    to.setKey(old.getKey());
    to.setRefAgent(refAgent);
    to.setDimModifiedTime(target.getGmtModified());
    to.setDimVersion(target.getVersion());

    return UPDATE_INDEX;
  }

  public Mono<Void> maintainTemplateDelete(String tableName, List<CollectTemplate> t) {
    LOGGER.error("maintainTemplateDelete");
    return Mono.empty();
  }
}
