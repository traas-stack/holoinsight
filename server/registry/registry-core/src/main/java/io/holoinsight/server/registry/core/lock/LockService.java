/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.lock;

import io.holoinsight.server.common.dao.entity.GaeaLockDO;
import io.holoinsight.server.common.dao.entity.GaeaLockDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaLockDOMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.function.Consumer;

/**
 * 分布式锁
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
@Service
public class LockService {
  private static final Logger LOGGER = LoggerFactory.getLogger(LockService.class);

  private static final int STATUS_FREE = 0;
  private static final int STATUS_LOCKED = 1;

  @Autowired
  private GaeaLockDOMapper mapper;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Transactional
  public Lock tryLock(String tenant, String name, String json, long expireMs) {
    // gmt_create 被用作锁的过期时间

    GaeaLockDO lockDO;

    {
      GaeaLockDOExample example = GaeaLockDOExample.newAndCreateCriteria() //
          .andTenantEqualTo(tenant) //
          .andNameEqualTo(name) //
          .example(); //
      lockDO = mapper.selectOneByExample(example);
    }

    if (lockDO == null) {
      lockDO = new GaeaLockDO();
      Date now = new Date();
      lockDO.setGmtCreate(new Date(now.getTime() + expireMs));
      lockDO.setGmtModified(now);
      lockDO.setTenant(tenant);
      lockDO.setName(name);
      lockDO.setVersion(0);
      lockDO.setJson(json);
      lockDO.setStatus(1);
      try {
        mapper.insert(lockDO);
        return new Lock(lockDO);
      } catch (DuplicateKeyException e) {
        // 锁被人抢了 此时肯定失败
        return null;
      }
    }

    if (lockDO.getStatus() == STATUS_LOCKED) {
      long expiredAt = lockDO.getGmtCreate().getTime();
      if (expiredAt >= System.currentTimeMillis()) {
        return null;
      }
    }

    int version = lockDO.getVersion();
    Date now = new Date();
    lockDO.setGmtCreate(new Date(now.getTime() + expireMs));
    lockDO.setGmtModified(now);
    lockDO.setStatus(STATUS_LOCKED);
    lockDO.setJson(json);
    lockDO.setVersion(version + 1);

    GaeaLockDOExample cas = GaeaLockDOExample.newAndCreateCriteria() //
        .andIdEqualTo(lockDO.getId()) //
        .andVersionEqualTo(version) //
        .example();

    int count = mapper.updateByExampleSelective(lockDO, cas, //
        GaeaLockDO.Column.gmtCreate, //
        GaeaLockDO.Column.gmtModified, //
        GaeaLockDO.Column.status, //
        GaeaLockDO.Column.json, //
        GaeaLockDO.Column.version); //
    if (count == 1) {
      return new Lock(lockDO);
    }
    return null;
  }

  private void unlock(Lock lock) {
    transactionTemplate.executeWithoutResult(new Consumer<TransactionStatus>() {
      @Override
      public void accept(TransactionStatus transactionStatus) {
        GaeaLockDO lockDO = lock.lockDO;
        GaeaLockDOExample cas = GaeaLockDOExample.newAndCreateCriteria() //
            .andIdEqualTo(lockDO.getId()) //
            .andVersionEqualTo(lockDO.getVersion()) //
            .example();

        lockDO.setVersion(lockDO.getVersion() + 1);
        lockDO.setStatus(STATUS_FREE);
        lockDO.setJson("{}");

        int count = mapper.updateByExampleSelective(lockDO, cas, //
            GaeaLockDO.Column.status, //
            GaeaLockDO.Column.json, //
            GaeaLockDO.Column.version); //

        if (count == 0) {
          LOGGER.error("分布式锁被意外释放 {}", lockDO);
          // 被其他线程意外释放了
        }
      }
    });
  }

  public class Lock {
    private final GaeaLockDO lockDO;

    public Lock(GaeaLockDO lockDO) {
      this.lockDO = lockDO;
    }

    public void unlock() {
      LockService.this.unlock(this);
    }
  }
}
