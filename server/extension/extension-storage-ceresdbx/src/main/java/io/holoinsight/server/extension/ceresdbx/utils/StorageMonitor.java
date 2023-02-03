package io.holoinsight.server.extension.ceresdbx.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinyan.ljw
 * @Description Storage Monitor
 * @date 2023/1/16
 */
public class StorageMonitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(StorageMonitor.class);
  private static Map<String, Body[]> map = new ConcurrentHashMap<>();

  // 监控调度线程
  private static final ScheduledThreadPoolExecutor monitorScheduler =
      new ScheduledThreadPoolExecutor(2, (r) -> new Thread(r, "storage-monitor"));

  private static final int REFRESH_PERIOD = 5;

  static {
    monitorScheduler.scheduleAtFixedRate(() -> {
      try {
        map.forEach((key, bodies) -> {
          if (bodies == null || bodies.length == 0) {
            return;
          }
          for (Body body : bodies) {
            if (body == null || body.getSize().get() == 0.0) {
              continue;
            }
            int reqs = body.getReqs().getAndSet(0);
            String resultStr = body.isResult() ? "success" : "fail";
            String metricName = body.table;
            String storageType = body.storageType;
            LOGGER.info(
                "[STORAGE-MONITOR][{}]save data for table:[{}] to [{}], period:[{}], size:[{}], dps:[{}], "
                    + "avg-rpcTime:[{}],avg-delay:[{}], tenant:[{}], reqs:[{}].",
                resultStr, metricName, storageType, body.getPeriod(), body.getSize().getAndSet(0),
                body.getDps().getAndSet(0), body.getRpcTime().getAndSet(0) / reqs,
                body.getDelay().getAndSet(0) / reqs, body.getTenant(), reqs);
          }
        });
      } catch (Exception e) {
        LOGGER.error("[STORAGE-MONITOR] error", e);
      }
    }, REFRESH_PERIOD, REFRESH_PERIOD, TimeUnit.SECONDS);
  }

  @Data
  static class Body {
    String table;
    String storageType;
    AtomicInteger size;
    AtomicInteger dps;
    AtomicInteger reqs;
    AtomicDouble rpcTime;
    AtomicDouble delay;
    String tenant;
    long period;
    boolean result;

    public Body(String table, String storageType, int size, int dps, double rpcTime, double delay,
        String tenant, long period, boolean result) {
      this.table = table;
      this.storageType = storageType;
      this.size = new AtomicInteger(size);
      this.dps = new AtomicInteger(dps);
      this.rpcTime = new AtomicDouble(rpcTime);
      this.delay = new AtomicDouble(delay);
      this.tenant = tenant;
      this.reqs = new AtomicInteger(1);
      this.period = period;
      this.result = result;
    }
  }

  public static void monitor(String storageType, String table, int size, int dps, double rpcTime,
      double delay, String tenant, boolean result, long period) {
    String key = table + tenant + storageType;
    if (!map.containsKey(key)) {
      map.put(key, new Body[2]);
    }
    Body[] bodies = map.get(key);
    int idx = result ? 1 : 0;
    Body body = bodies[idx];
    if (body == null) {
      body = new Body(storageType, table, size, dps, rpcTime, delay, tenant, period, result);
      bodies[idx] = body;
    } else {
      body.getSize().addAndGet(size);
      body.getDps().addAndGet(dps);
      body.getRpcTime().addAndGet(rpcTime);
      body.getDelay().addAndGet(delay);
      body.getReqs().addAndGet(1);
      body.setPeriod(period);
    }
  }
}
