/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务端维度数据
 *
 * @author wanpeng.xwp
 * @version : ServerDimData.java, v 0.1 2019年12月06日 17:38 wanpeng.xwp Exp $
 */
@Getter
@Slf4j
public class ServerDimData extends AbstractDimData {

  private static final int SERVER_DATA_EXPIRED_TIME = 60 * 60 * 1000;

  private static final long SERVER_SYNC_DELAY = 3000;

  private static final long SERVER_SYNC_FREQUENCY = 1000;

  public ServerDimData(String tableName, long version, List<DimDataRow> rows,
      Metasynchronizer synchronizer) {
    super(tableName, SERVER_DATA_EXPIRED_TIME, version, rows, synchronizer);
    this.buildTime = System.currentTimeMillis();
    log.info("ServerDimData build finish, table={}, buildTime={}, ttl={}.", this.getTableName(),
        this.getBuildTime(), this.getTtl());
    this.startSync();
  }

  @Override
  protected long syncDelay() {
    return 3000;
  }

}
