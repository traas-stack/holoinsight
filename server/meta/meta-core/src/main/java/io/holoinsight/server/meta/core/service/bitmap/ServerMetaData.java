/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 服务端维度数据
 *
 * @author xiangwanpeng
 * @version : ServerMetaData.java, v 0.1 2019年12月06日 17:38 xiangwanpeng Exp $
 */
@Getter
@Slf4j
public class ServerMetaData extends AbstractMetaData {

  private static final int SERVER_DATA_EXPIRED_TIME = 60 * 60 * 1000;

  private static final long SERVER_SYNC_DELAY = 3000;

  private static final long SERVER_SYNC_FREQUENCY = 1000;

  public ServerMetaData(String tableName, long version, List<MetaDataRow> rows,
      Metasynchronizer synchronizer) {
    super(tableName, SERVER_DATA_EXPIRED_TIME, version, rows, synchronizer);
    this.buildTime = System.currentTimeMillis();
    log.info("ServerMetaData build finish, table={}, version={}, buildTime={}, ttl={}, rows={}.",
        this.getTableName(), this.getVersion(), this.getBuildTime(), this.getTtl(),
        CollectionUtils.size(rows));
    this.startSync();
  }

  @Override
  protected long syncDelay() {
    return 3000;
  }

}
