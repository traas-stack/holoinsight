/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.lock;

import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import io.holoinsight.server.common.dao.entity.GaeaLockDO;
import io.holoinsight.server.common.dao.entity.GaeaLockDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaLockDOMapper;
import lombok.SneakyThrows;

/**
 * 分布式锁
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
@Service
public class LockService {
  public static final int STATUS_FREE = 0;
  public static final int STATUS_LOCKED = 1;
  private static final Logger LOGGER = LoggerFactory.getLogger(LockService.class);
  @Autowired
  private GaeaLockDOMapper mapper;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Transactional
  public GaeaLockDO getLockDO(String tenant, String name) {
    // need an unique index on (tenant, name)
    GaeaLockDOExample example = GaeaLockDOExample.newAndCreateCriteria() //
        .andTenantEqualTo(tenant) //
        .andNameEqualTo(name) //
        .example(); //
    return mapper.selectOneByExample(example);
  }

  @Transactional
  public Lock tryLock(String tenant, String name, String json, long expireMs) {
    GaeaLockDO lockDO = getLockDO(tenant, name);

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
        return new Lock(lockDO, expireMs);
      } catch (DuplicateKeyException e) {
        return null;
      }
    }

    if (lockDO.getStatus() == STATUS_LOCKED) {
      long expiredAt = lockDO.getGmtCreate().getTime();
      if (expiredAt >= System.currentTimeMillis()) {
        return null;
      } // else this lock is locked by others but is already expired
    } // else this lock is not locked by others

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

    // cas success
    if (count == 1) {
      return new Lock(lockDO, expireMs);
    }

    // cas error
    return null;
  }

  private boolean unlock(Lock lock) {
    return transactionTemplate.execute(new TransactionCallback<Boolean>() {
      @Override
      public Boolean doInTransaction(TransactionStatus status) {
        GaeaLockDO lockDO = copy(lock.lockDO);

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

        if (count == 1) {
          lock.lockDO = lockDO;
        }
        return count == 1;
      }
    });
  }

  private boolean touch(Lock lock) {
    return transactionTemplate.execute(new TransactionCallback<Boolean>() {
      @Override
      public Boolean doInTransaction(TransactionStatus status) {
        GaeaLockDO lockDO = copy(lock.lockDO);

        GaeaLockDOExample cas = GaeaLockDOExample.newAndCreateCriteria() //
            .andIdEqualTo(lockDO.getId()) //
            .andVersionEqualTo(lockDO.getVersion()) //
            .example();

        lockDO.setGmtCreate(new Date(System.currentTimeMillis() + lock.expireMs));
        lockDO.setGmtModified(new Date());
        lockDO.setVersion(lockDO.getVersion() + 1);

        int count = mapper.updateByExampleSelective(lockDO, cas, //
            GaeaLockDO.Column.status, //
            GaeaLockDO.Column.json, //
            GaeaLockDO.Column.version); //

        if (count == 1) {
          lock.lockDO = lockDO;
        }

        return count == 1;
      }
    });
  }

  @SneakyThrows
  private static GaeaLockDO copy(GaeaLockDO lockDO) {
    return (GaeaLockDO) BeanUtils.cloneBean(lockDO);
  }

  public class Lock {
    private final long expireMs;
    private GaeaLockDO lockDO;

    private Lock(GaeaLockDO lockDO, long expireMs) {
      this.lockDO = lockDO;
      this.expireMs = expireMs;
    }

    /**
     * Unlock
     * 
     * @return true if unlock successfully. Returns false if the status of lock is changed before
     *         unlock.
     */
    public boolean unlock() {
      return LockService.this.unlock(this);
    }

    public boolean touch() {
      return LockService.this.touch(this);
    }
  }
}
