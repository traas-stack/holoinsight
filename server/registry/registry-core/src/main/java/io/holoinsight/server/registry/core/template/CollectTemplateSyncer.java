/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDO;
import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaCollectConfigDOMapper;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetMaintainer;
import io.holoinsight.server.registry.core.utils.BoundedSchedulers;
import io.holoinsight.server.common.MetricsUtils;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.Maps;
import com.xzchaoo.commons.basic.concurrent.DynamicScheduledExecutorService;
import com.xzchaoo.commons.basic.concurrent.OneThreadFactory;
import com.xzchaoo.commons.basic.dispose.Disposable;
import com.xzchaoo.commons.stat.Measurement;
import com.xzchaoo.commons.stat.StringsKey;

/**
 * 负责将 CollectConfig 同步到内存里
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@Component
public class CollectTemplateSyncer {
  private static final int STOP_TIMEOUT_SECONDS = 3;
  private static final Logger LOGGER = LoggerFactory.getLogger("TEMPLATE");
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private Date begin;
  private ScheduledThreadPoolExecutor scheduler;
  private ScheduledThreadPoolExecutor scheduler2;
  @Autowired
  private GaeaCollectConfigDOMapper mapper;
  @Autowired
  private TemplateConfig templateConfig;
  private Disposable deltaSyncTaskDisposable;
  private Disposable fullSyncTaskDisposable;
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private PlatformTransactionManager platformTransactionManager;
  private TransactionTemplate readCommittedTT;
  /**
   * 不要直接依赖该maintainer
   */
  @Autowired
  private CollectTargetMaintainer taskMaintainer;

  @PostConstruct
  public void init() {
    MetricsUtils.SM.func(StringsKey.of("template.count"), () -> {
      Map<StringsKey, int[]> count = new HashMap<>();
      for (CollectTemplate t : templateStorage.readonlyLoop()) {
        String type = t.getType();
        if (StringUtils.isEmpty(type)) {
          type = "";
        }
        StringsKey key = StringsKey.of(t.getTenant(), type);
        count.computeIfAbsent(key, i -> new int[] {0})[0]++;
      }
      List<Measurement> ms = new ArrayList<>();
      for (Map.Entry<StringsKey, int[]> e : count.entrySet()) {
        ms.add(new Measurement(e.getKey(), new long[] {e.getValue()[0]}));
      }
      return ms;
    });

    DefaultTransactionDefinition dtd = new DefaultTransactionDefinition();
    dtd.setTimeout(30);
    dtd.setReadOnly(true);
    dtd.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    dtd.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    readCommittedTT = new TransactionTemplate(platformTransactionManager, dtd);
    readCommittedTT.afterPropertiesSet();

    scheduler2 =
        new ScheduledThreadPoolExecutor(1, new OneThreadFactory("template-maintainer-trigger"));
  }

  public Mono<Void> initLoadTemplates() {
    return Mono.<Void>fromRunnable(this::initLoadTemplates0) //
        .subscribeOn(BoundedSchedulers.BOUNDED); //
  }

  public int maintainAll() {
    int concurrency = 2 * Runtime.getRuntime().availableProcessors();
    Collection<CollectTemplate> templates = templateStorage.internalGetAllTemplates();
    Flux.fromIterable(templates) //
        .flatMap(t -> { //
          return taskMaintainer.publish(t, () -> taskMaintainer.maintainTemplateNow(t));
        }, concurrency) //
        .ignoreElements() //
        .block(); //
    // 这个值不是很准不过无所谓
    return templates.size();
  }

  public Mono<Void> initApplyTemplates() {
    long begin = System.currentTimeMillis();
    int concurrency = 2 * Runtime.getRuntime().availableProcessors();
    Collection<CollectTemplate> templates = templateStorage.internalGetAllTemplates();
    return Flux.fromIterable(templates) //
        .flatMap(t -> { //
          return taskMaintainer.maintainTemplateDelta(CollectTemplateDelta.one(t))
              .doOnTerminate(() -> scheduleOnce(t));
        }, concurrency) //
        .ignoreElements() //
        .doOnSuccess(ignored -> { //
          long cost = System.currentTimeMillis() - begin;
          LOGGER.info("init apply templates size=[{}] cost=[{}]", templates.size(), cost);
        });
  }

  /**
   * 初始化加载配置
   */
  private void initLoadTemplates0() {
    // 1. 读本地 sqlite 缓存到内存里
    // 2. 读数据库里的最新id
    // 3. 增量更新, 遇到有删除的case, 要doublecheck, 确保没有误删即可

    Date now = new Date();

    // 简单实现 select all
    long begin = System.currentTimeMillis();

    List<GaeaCollectConfigDO> configDos = readCommittedTT.execute(status -> {
      GaeaCollectConfigDOExample example = GaeaCollectConfigDOExample.newAndCreateCriteria() //
          .andDeletedEqualTo(0) //
          .example(); //
      return mapper.selectByExampleWithBLOBs(example);
    });
    long end = System.currentTimeMillis();

    int success = 0;
    int error = 0;

    for (GaeaCollectConfigDO gcc : configDos) {
      CollectTemplate t = build(gcc);
      if (t != null) {
        put0(t);
        success++;
      } else {
        error++;
      }
    }

    LOGGER.info("init load success=[{}] error=[{}] cost=[{}]", success, error, end - begin);

    // ...
    // 初始化失败是致命的 必须抛异常
    this.begin = now;

    // TODO 初始化之后触发 dimservice 初始化更新
  }

  /**
   * 开始同步数据
   */
  public void startSync() {
    scheduler = new ScheduledThreadPoolExecutor(1, new OneThreadFactory("template-syncer"));
    scheduler = new ScheduledThreadPoolExecutor(1, new OneThreadFactory("template-syncer"));
    // 读一个future执行cancel, 如果task还没执行, 是否立即从队列里remove掉
    // 这个我理解应该默认是true比较合理的 ... 否则会出现停不下来的情况
    scheduler.setRemoveOnCancelPolicy(true);

    // 开始的时候多延迟一会
    deltaSyncTaskDisposable =
        DynamicScheduledExecutorService.wrap(scheduler).dynamic(this::deltaSync, //
            templateConfig.getSync().getInterval().plus(templateConfig.getSync().getInterval()), //
            () -> templateConfig.getSync().getInterval()); //

    // 全量同步
    fullSyncTaskDisposable = DynamicScheduledExecutorService.wrap(scheduler).dynamic(this::fullSync, //
        templateConfig.getSync().getFullInterval(), //
        () -> templateConfig.getSync().getFullInterval()); //

  }

  @PreDestroy
  public void destroy() {
    if (deltaSyncTaskDisposable != null) {
      deltaSyncTaskDisposable.dispose();
    }
    if (fullSyncTaskDisposable != null) {
      fullSyncTaskDisposable.dispose();
    }

    ScheduledThreadPoolExecutor scheduler = this.scheduler;
    this.scheduler = null;
    if (scheduler != null) {
      // 对于之前已经提交还未执行的任务就不会再执行了
      // 按理此处是shutdown或shutdownNow都可以, 因为我们在上面已经主动 dispose(cancel) 掉该任务了 (需要配合 removeOonCancel)
      scheduler.shutdownNow();

      try {
        if (!scheduler.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
          LOGGER.warn("template syncer scheduler stop timeout");
        }
        // else 正常结束
      } catch (InterruptedException e) {
        LOGGER.warn("thread interrupted when waiting scheduler to stop");
      }
    }
  }

  /**
   * 公开的api 用于手动触发fullSync
   *
   * @throws Exception
   */
  public void publicFullSync() throws Exception {
    scheduler.submit(this::fullSync).get();
  }

  /**
   * 全量同步
   */
  private synchronized void fullSync() {
    // 因为调度器线程大小只有1, 因此在没有用户手动调用的情况下 deltaSync/fullSync 肯定是串行执行的
    // 为了处理用户手动调用的情况, 之类加上了锁
    // 解释一下此处为什么要用可重复读的tt

    // TODO 使用 repeat read ?
    List<Long> ids = readCommittedTT.execute(status -> {
      // TODO 这个如何高效地踩到索引上? 可选性太少了
      GaeaCollectConfigDOExample example = GaeaCollectConfigDOExample.newAndCreateCriteria() //
          .andDeletedEqualTo(0) //
          .example(); //

      // 我只需要id即可
      List<GaeaCollectConfigDO> configDos =
          mapper.selectByExampleSelective(example, GaeaCollectConfigDO.Column.id);
      return configDos.stream().map(GaeaCollectConfigDO::getId).collect(Collectors.toList());
    });

    Map<Long, CollectTemplate> memory = templateStorage.getAsMap();

    List<Long> needAdd = new ArrayList<>();
    for (Long id : ids) {
      CollectTemplate t = memory.remove(id);
      if (t == null) {
        needAdd.add(id);
      }
    }

    // TODO 这个方法读了多次db, 需要考虑事务如何安排
    // TODO 限制一下传入的ids, 必要时分批

    List<Long> selectIds = new ArrayList<>(needAdd.size() + memory.size());
    selectIds.addAll(needAdd);
    selectIds.addAll(memory.keySet());

    Set<String> needRetry = new HashSet<>();
    Map<Long, GaeaCollectConfigDO> tempMap = Collections.emptyMap();
    // TODO 名字能不能好好起
    if (selectIds.size() > 0) {
      List<GaeaCollectConfigDO> tempDos = mapper.selectByExampleSelective(
          GaeaCollectConfigDOExample.newAndCreateCriteria() //
              .andDeletedEqualTo(0) //
              .andIdIn(selectIds) //
              .example(), //
          GaeaCollectConfigDO.Column.id, //
          GaeaCollectConfigDO.Column.tableName, //
          GaeaCollectConfigDO.Column.deleted);//
      tempMap = tempDos.stream().collect(Collectors.toMap(GaeaCollectConfigDO::getId, e -> e));

      tempDos.forEach(e -> needRetry.add(e.getTableName()));
      memory.values().forEach(e -> needRetry.add(e.getTableName()));
    }

    Map<String, CollectTemplateDelta> templateDeltas = new HashMap<>();
    for (CollectTemplate t : memory.values()) {
      CollectTemplateDelta d =
          templateDeltas.computeIfAbsent(t.getTableName(), CollectTemplateDelta::new);
      GaeaCollectConfigDO gcc = tempMap.get(t.getId());
      if (gcc == null || gcc.getDeleted() > 0) {
        // 无法根据id读到, 被物理删除了
        d.delete(t);
      } else {
        // 不可能走到这里
        // select id from ... where deleted = 0 里没有它, 但 select * from ... where id in ... 缺有它
        // deleted == 0
        LOGGER.error(" {}", t.getId());
      }
    }

    int buildError = 0;
    if (needRetry.size() > 0) {
      List<GaeaCollectConfigDO> tempDos =
          mapper.selectByExampleWithBLOBs(GaeaCollectConfigDOExample.newAndCreateCriteria() //
              .andTableNameIn(new ArrayList<>(needRetry)) //
              .andDeletedEqualTo(0) //
              .example());

      for (GaeaCollectConfigDO gcc : tempDos) {
        CollectTemplate t = build(gcc);
        if (t == null) {
          ++buildError;
          continue;
        }
        CollectTemplateDelta d =
            templateDeltas.computeIfAbsent(t.getTableName(), CollectTemplateDelta::new);
        d.add(t);
      }
    }

    int add = 0;
    int del = 0;
    for (CollectTemplateDelta d : templateDeltas.values()) {
      add += d.getAdd().size();
      del += d.getDelete().size();
    }

    applyDeltas(templateDeltas);
    LOGGER.info("full sync success ids=[{}] add=[{}] del=[{}] error=[{}]", ids.size(), add, del,
        buildError);

    // t的删除有3种:
    // 1. 能读到t的删除
    // 2. 读不到t的删除, 但能读到tableName的其他版本
    // 2. 读不到t的删除, 也读不到tableName的其他版本, 说明整个tableName的数据都被删了

    // 此时:
    // 1. needAdd 是需要新增的template
    // 1.1 该tableName不在内存里, 即是第一次添加 (但你不读到内存里, 你岂能知道它的tableName?)
    // 1.2 该tableName已经在内存里, 其实是一次update (但你不读到内存里, 你岂能知道它的tableName?)
    // 2. memory.keySet() 是需要删除的template

    // 方案1:
    // 这些ids代表当时db里所有online的配置
    // 由于我们的同步是加锁的, 因此此时对于当前registry实例来说(与其他实例比是不一定的)
    // 这份数据一定是权威的
    // 对比ids和内存里的ids, 可以得出, add和update, add和update 进一步整理成 Delta 然后 apply

    // 当然这个算法也有缺点, 就是它需要一口气select出所有的ids, 这个sql简直太简单 select id from gaea_collect_config where
    // deleted = 0;
    // 主站量大情况下性能不好, 我们接受慢一点, 但求对DB压力小一点

    // 方案2:
    // 另外一个思路是增量select出所有ids
    // select id from gaea_collect_config where deleted = 0 and id > ? limit 10000; 不停这样迭代
    // 这依赖一个 (delete, id) 的索引 dimservice 也是这么做的

    // 但这有一个问题 毕竟这是多次select, 不能保证它和一次select的数据完全相同
    // 比如在 read-committed 的隔离模式下, 假设2次select之间发生了变更, 显然它可能读到2个tableName相同 deleted==0 的配置 (其实其中一个被删了)

    // 一种解法可能是使用 repeatable_read 隔离级别, 但需要论证一下这样一定是对的?

    // 方案3: 方案2的改进
    // 但我大概还有另外一种思路 (但这种属于具体问题具体分析, 它不能适用所有场景):
    // 1. 使用 read-committed 隔离读出所有ids
    // 2. 对比得出 template 的 add, del
    // 3. 将 add, del 进一步整理得出 delta, 此时不用考虑storage里是否已经存在template
    // 4. 对于每个 delta:
    // 4.1 如果 add > 1: 显然是有问题的, 该table需要重新read
    // 4.2 如果 add == 1: 那么确保该template存在即可 (不存在就创建)
    // 4.3 如果 add == 0, 意味着delete>0: 该table需要重新read

    // 最终, 我们的流程可以保证:
    // 1. 对于每个tableName, 除非是真的删除, 否则其配置一定不会被误删到光
    // 2. 对于每个tableName, 当前registry内存里生效的template可能不是最新的, 但最终会被 增量/全量 同步更新成最新的
    // 3.
  }

  // private List<GaeaCollectConfigDOWithBLOBs> selectByIds(List<Long> ids) {
  // // TODO 限制一下传入的ids, 必要时分批
  // Flux.fromIterable(ids) //
  // .buffer(1024) //
  // .flatMap(g -> {
  // // TODO 这里有并发 因此是在不同的 线程/事务
  // return Mono.fromCallable(() -> {
  // return mapper.selectByExampleWithBLOBs(GaeaCollectConfigDOExample.newAndCreateCriteria() //
  // .andDeletedEqualTo(0) //
  // .andIdIn(g) //
  // .example()); //
  // // TODO 放到一个线程池
  // });
  // });
  // }

  private Map<String, CollectConfigDelta> retry(Collection<String> tableNames) {
    GaeaCollectConfigDOExample retryExample = GaeaCollectConfigDOExample.newAndCreateCriteria() //
        .andTableNameIn(
            (tableNames instanceof List) ? (List) tableNames : new ArrayList<>(tableNames)) //
        .example(); //

    List<GaeaCollectConfigDO> retryConfigDos = mapper.selectByExampleWithBLOBs(retryExample);

    // 对这些重试的进行一个基本的检查
    Map<String, CollectConfigDelta> groups = toGroups(retryConfigDos, false);
    for (Map.Entry<String, CollectConfigDelta> e : groups.entrySet()) {
      CollectConfigDelta g = e.getValue();
      if (g.getAdd().size() > 1) {
        // 这个是非法的, 其他都是合法的, 只是打印一下日志而已
        LOGGER.error("tableName=[{}] violate rule: add=[{}] del=[{}]", //
            g.getTableName(), //
            g.getAdd().size(), g.getDelete().size()); //
        // invalid++;
        g.setStatus(CollectConfigDelta.STATUS_INVALID);
        // 只能先忽略这个组的update
      } else if (g.getAdd().size() == 1) {
        LOGGER.info("tableName=[{}] new add add=[{}], del=[{}]", //
            g.getTableName(), //
            g.getAdd().size(), g.getDelete().size());
      } else {
        LOGGER.info("tableName=[{}] do del add=[{}], del=[{}]", //
            g.getTableName(), //
            g.getAdd().size(), g.getDelete().size());
      }
    }
    groups.entrySet().removeIf(e -> e.getValue().getStatus() == CollectConfigDelta.STATUS_INVALID);

    return groups;
  }

  private synchronized void deltaSync() {
    val sync = templateConfig.getSync();

    long now = System.currentTimeMillis();
    long endMs = now - sync.getDelay().toMillis();

    Date end = new Date(endMs);
    try {
      if (end.after(begin)) {
        // TODO 检查 [begin,end) 差距是否太大, 太大的话别用增量的, 直接用一次全量的
        Map<String, CollectTemplateDelta> templateDeltas = readCommittedTT.execute((status) -> {
          try {
            return syncDataOnce0(begin, end);
          } catch (Throwable e) {
            throw new RuntimeException(e);
          }
        });
        if (templateDeltas != null) {
          applyDeltas(templateDeltas);
        }
      }
      // 仅成功才更新
      begin = end;
    } catch (Throwable e) {
      LOGGER.error("sync error", e);
    }
  }

  /**
   * 执行配置变更
   *
   * @param templateDeltas
   */
  private void applyDeltas(Map<String, CollectTemplateDelta> templateDeltas) {
    if (templateDeltas.isEmpty()) {
      return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    for (val d : templateDeltas.values()) {
      for (val t : d.getAdd()) {
        LOGGER.info("delta add [{}/{}] dbTime={}", t.getId(), t.getTableName(),
            sdf.format(t.getGmtModified()));
      }
      for (val t : d.getDelete()) {
        LOGGER.info("delta del [{}/{}] dbTime={}", t.getId(), t.getTableName(),
            sdf.format(t.getGmtModified()));
      }
    }

    applyDeltas0(templateDeltas);
  }

  private void applyDeltas0(Map<String, CollectTemplateDelta> templateDeltas) {
    applyDeltas0Quick(templateDeltas);
  }

  private void applyDeltas0Quick(Map<String, CollectTemplateDelta> templateDeltas) {
    // 这是简化版, 它容易理解, 但它无法处理一个问题:
    // db里的templates快速变化(或者reg自身太慢了, 这是相对的), 导致某个templates被硬删除了, 导致reg没有读到这个变化.
    // 此时reg内存里会有2个tableName相同的配置, 但reg自己不知道

    Flux.fromIterable(templateDeltas.values()) //
        .flatMap(this::applyDeltas0OneQuick) //
        .ignoreElements() //
        .block(); //
  }

  private Mono<Void> applyDeltas0OneQuick(CollectTemplateDelta g) {
    // 这是简化版, 它容易理解, 但它无法处理一个问题:
    // db里的templates快速变化(或者reg自身太慢了, 这是相对的), 导致某个templates被硬删除了, 导致reg没有读到这个变化.
    // 此时reg内存里会有2个tableName相同的配置, 但reg自己不知道

    return taskMaintainer.publish(g.getTableName(), () -> {
      // 因此内存里的templates, 可能出现不一致

      // 先把新增的add进去
      List<CollectTemplate> newAdd = new ArrayList<>();
      for (CollectTemplate t : g.getAdd()) {
        if (templateStorage.get(t.getId()) == null) {
          templateStorage.put(t);
          newAdd.add(t);
        }
      }

      // 执行新旧任务构建
      taskMaintainer.maintainTemplateDelta0(g);

      // 该方法跑在对应的线程里

      // 添加调度任务
      newAdd.forEach(this::scheduleOnce);

      // 把已知的旧任务删掉
      for (CollectTemplate t : g.getDelete()) {
        delete0(t.getId());
      }
      Set<Long> ids = templateStorage.get(g.getTableName());
      CollectTemplate add0 = g.getAdd0();

      List<CollectTemplate> ensureDeleted = new ArrayList<>();
      for (long id : new ArrayList<>(ids)) {
        if (add0.getId() != id) {
          CollectTemplate residual = delete0(id);
          if (residual != null) {
            ensureDeleted.add(residual);
          }
        }
      }
      // return taskMaintainer.maintainTemplateDelete(g.getTableName(), ensureDeleted);
    });
  }

  /**
   * 这个方法只处理配置db->内存同步, 不会对配置进行解析, 目的是为了减少在事务里的时间
   *
   * @param begin
   * @param end
   * @return
   * @throws Throwable
   */
  private Map<String, CollectTemplateDelta> syncDataOnce0(Date begin, Date end) throws Throwable {
    GaeaCollectConfigDOExample example = GaeaCollectConfigDOExample.newAndCreateCriteria() //
        .andGmtModifiedGreaterThanOrEqualTo(begin) //
        .andGmtModifiedLessThan(end) //
        .example();

    long dbBegin = System.currentTimeMillis();
    List<GaeaCollectConfigDO> configDos = mapper.selectByExampleWithBLOBs(example);
    long dbEnd = System.currentTimeMillis();
    if (configDos.isEmpty()) {
      synchronized (SDF) {
        LOGGER.info("delta sync success range=[{}, {}) empty, dbCost=[{}]", SDF.format(begin),
            SDF.format(end), dbEnd - dbBegin);
      }
      return null;
    }

    Map<String, CollectConfigDelta> groups = toGroups(configDos, true);

    List<String> retryTableNames = new ArrayList<>();
    for (Map.Entry<String, CollectConfigDelta> e : groups.entrySet()) {
      CollectConfigDelta g = e.getValue();
      // 99% case: add=1 delete=1 表示为一次更新

      if (g.getAdd().size() != 1 || g.getDelete().size() != 1) {
        // 如果不是add==1 && delete==1, 简单的说就是: "我不相信" 你select出的数据(我可以举例一些case会有错误的结果)
        // 那怎么验证这个case到底是不是确实是合法的呢? 唯有重新select一遍
        retryTableNames.add(g.getTableName());
        g.setStatus(CollectConfigDelta.STATUS_RETRY);
      }

      // 以下是一些细节:
      // 特别关注:
      // 1. add>0 delete=0:
      // 1.1 如果add>1, 走重新select
      // 1.2 否则add==1, 如果此时内存里确实没有该tableName的template, 那么这确实是一次add {缺点是这里需要有一个byTableName的索引},
      // 建议都走重新select
      // 1.2.1 如果此时内存里存在tableName对应的template, 那么意味着这其实是一次update, 我们没有读到该add对应的delete, 走重新select

      // 2. add=0 delete>0:
      // 2.1 如果delete>1, 走重新select: 理论上有可能的, 短时间内快速变化两次
      // 2.2 否则delete==1, 如果该 template id 在内存里都没出现过, 那么忽略这个template, 相当于就忽略这个group了
      // 2.2.1 走重新select, 确保这确实是一个delete, 而不是update

      // 一旦进入重新select, 则按如下规则:
      // select * from gaea_collect_config where table_name = ? , 注意这里select出了该tableName的所有历史
      // 1. 我们认为该SQL的结果是权威的, 无条件相信它
      // 2. 如果add>1, 违反业务规则, 则报错, 忽略这次处理
      // 3. 如果add==1, 那么它就是最终要存活下来的template, 其余的template全删掉
      // 4. 如果add==0, 意味着这个template已经走完了一生, 可以删除了
      // 我们要想办法定期清理哪些 deleted全为0 的 tableName 的 template

      // 最终简化版:
      // 1. 只要结果不是 add==1 && delete==1 我就重新select一遍
      // 2. 我们认为这次重新select的结果是权威的, 一定不会有错
      // 3. 当然对这次select的结果我们还是要做一些基本的校验的, 如果违反了, 那么就打印证据
    }

    groups.entrySet().removeIf(e -> e.getValue().getStatus() == CollectConfigDelta.STATUS_RETRY);

    int invalid = 0;
    if (retryTableNames.size() > 0) {
      // TODO 理论上这个地方只需要select出 deleted==0 的, 再与原本的进行比较就可以知道那些是彻底deleted的
      GaeaCollectConfigDOExample retryExample = GaeaCollectConfigDOExample.newAndCreateCriteria() //
          .andTableNameIn(retryTableNames) //
          .example(); //

      List<GaeaCollectConfigDO> retryConfigDos = mapper.selectByExampleWithBLOBs(retryExample);

      // 对这些重试的进行一个基本的检查
      Map<String, CollectConfigDelta> groups2 = toGroups(retryConfigDos, false);
      for (Map.Entry<String, CollectConfigDelta> e : groups2.entrySet()) {
        CollectConfigDelta g = e.getValue();
        if (g.getAdd().size() > 1) {
          // 这个是非法的, 其他都是合法的, 只是打印一下日志而已
          LOGGER.error("tableName=[{}] violate rule: add=[{}] del=[{}]", //
              g.getTableName(), //
              g.getAdd().size(), g.getDelete().size()); //
          invalid++;
          g.setStatus(CollectConfigDelta.STATUS_INVALID);
          // 只能先忽略这个组的update
        } else if (g.getAdd().size() == 1) {
          LOGGER.info("tableName=[{}] new add add=[{}], del=[{}]", //
              g.getTableName(), //
              g.getAdd().size(), g.getDelete().size());
        } else {
          LOGGER.info("tableName=[{}] do del add=[{}], del=[{}]", //
              g.getTableName(), //
              g.getAdd().size(), g.getDelete().size());
        }
      }
      groups2.entrySet()
          .removeIf(e -> e.getValue().getStatus() == CollectConfigDelta.STATUS_INVALID);
      groups.putAll(groups2);
    }

    int update = 0;
    int add = 0;
    int delete = 0;

    for (CollectConfigDelta g : groups.values()) {
      if (g.getAdd().size() == 1 && g.getDelete().size() > 0) {
        update++;
      } else if (g.getAdd().size() == 1 && g.getDelete().size() == 0) {
        add++;
      } else {
        delete++;
      }
    }

    // TODO 此处有一个case要注意, 就是配置多次变化, 导致某个id已经在数据库里被删没了, 此时我们的增量可能会犯错
    // 一旦进入这个错误, 只能通过全量修复
    // 可以通过2种方法规避:
    // 1. 配置下发时候检查tableName不重复, 否则直接报错;
    // 2. 在storage层维护一个byTableName的索引, 配置同步时我们做这个检查

    synchronized (SDF) {
      LOGGER.info(
          "delta sync success range=[{}, {}) update=[{}] add=[{}] delete=[{}] retry=[{}] invalid=[{}]", //
          SDF.format(begin), SDF.format(end), //
          update, add, delete, retryTableNames.size(), invalid);
    }

    return buildCollectTemplateDelta(groups);
  }

  private Map<String, CollectTemplateDelta> buildCollectTemplateDelta(
      Map<String, CollectConfigDelta> groups) {
    // 同步完之后开始处理
    // return new CollectTemplateDelta(groups);
    Map<String, CollectTemplateDelta> templateDeltas =
        Maps.newHashMapWithExpectedSize(groups.size());
    for (Map.Entry<String, CollectConfigDelta> e : groups.entrySet()) {
      CollectConfigDelta ccd = e.getValue();
      CollectTemplateDelta ctd = new CollectTemplateDelta(ccd.getTableName());
      ctd.add = new ArrayList<>(ccd.add.size());
      ctd.delete = new ArrayList<>(ccd.delete.size());

      for (GaeaCollectConfigDO gcc : ccd.add) {
        CollectTemplate exist = templateStorage.get(gcc.getId());
        if (exist != null) {
          ctd.add.add(exist);
        } else {
          CollectTemplate t = build(gcc);
          if (t != null) {
            ctd.add.add(t);
          }
        }
      }
      for (GaeaCollectConfigDO gcc : ccd.delete) {
        CollectTemplate exist = templateStorage.get(gcc.getId());
        if (exist != null) {
          ctd.delete.add(exist);
        } else {
          // 此时没有必要add进去
          // ctd.delete.add(TemplateUtils.build(gcc));
        }
      }

      // 当配置本身是非法的时候, 就会出现size全为0的case
      if (ctd.getAdd().size() > 0 || ctd.getDelete().size() > 0) {
        templateDeltas.put(e.getKey(), ctd);
      }

    }
    return templateDeltas;
  }

  private Map<String, CollectConfigDelta> toGroups(List<GaeaCollectConfigDO> configDos,
      boolean warnNotExistDelete) {
    Map<String, CollectConfigDelta> groups = Maps.newHashMapWithExpectedSize(configDos.size() / 2);

    for (GaeaCollectConfigDO gcc : configDos) {
      if (gcc.getDeleted() > 0) {
        // 该配置要删除, 但它并不在我内存里; 可能是配置快速变化, 我还没来得及同步到该配置的add事件, 它就已经被删了, 那么我直接忽略它; 是没问题的
        // 这样的最好记录一个warn事件, 这说明我们与数据库的同步不够及时
        // 1. 数据变化太快?
        // 2. reg同步太慢?
        CollectTemplate deletedT = templateStorage.get(gcc.getId());
        if (deletedT == null) {
          if (warnNotExistDelete) {
            LOGGER.warn("ignore delete no-exist template {}", gcc.getId());
          }
          continue;
        }
        groups.computeIfAbsent(gcc.getTableName(), CollectConfigDelta::new).delete(gcc);
      } else {
        // 重复添加没事的, 后面会处理
        groups.computeIfAbsent(gcc.getTableName(), CollectConfigDelta::new).add(gcc);
      }
    }

    return groups;
  }

  private void put0(CollectTemplate t) {
    templateStorage.put(t);
  }

  private void scheduleOnce(CollectTemplate t) {
    long seconds = templateConfig.getBuild().getInterval().getSeconds();
    scheduler2.schedule(() -> triggerMaintainTemplate(t), seconds, TimeUnit.SECONDS);
  }

  private void triggerMaintainTemplate(CollectTemplate t) {
    taskMaintainer.publish(t, () -> {
      // 假设 sync 线程 投递了一个 delta, 包含一个add和delete(这个delete对应我们的t)
      // 然后我们才投递一个maintainTemplate, 此时可能会有问题, 因为按照我们的线程模型前者一定会先执行, 我们再执行,
      // 可能会让原本该倍deleted的targets又被创建
      CollectTemplate ct = templateStorage.get(t.getId());
      if (ct == null) {
        // 配置已经被删除没必要继续维护
        LOGGER.warn("发现配置删除 [{}/{}]", t.getId(), t.getTableName());
        return;
      }
      try {
        taskMaintainer.maintainTemplateNow(t);
      } catch (Throwable e) {
        LOGGER.error("maintainTemplateNow error", e);
      } finally {
        scheduleOnce(t);
      }
    }).subscribe();
  }

  private CollectTemplate delete0(long id) {
    return templateStorage.delete(id);
  }

  private static CollectTemplate build(GaeaCollectConfigDO gcc) {
    // 统一在这里build并记录日志
    try {
      return TemplateUtils.build(gcc);
    } catch (Throwable e) {
      LOGGER.error("build template error [{}/{}]", gcc.getId(), gcc.getTableName(), e);
      return null;
    }
  }
}
