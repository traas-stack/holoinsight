package io.holoinsight.server.storage.web.scheduler;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.StatisticData;
import io.holoinsight.server.storage.server.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@ConditionalOnFeature("trace")
public class StatisticScheduler {

    @Autowired
    private TraceService traceService;

    @Scheduled(initialDelay=1000, fixedRate=1000 * 60 * 5)
    private void statisticTrace() {
        try {
            long endTime = System.currentTimeMillis();
            long startTime = endTime - 1000 * 60 * 60 * 24; // one day ago
            List<StatisticData> statisticData = traceService.statisticTrace(startTime, endTime);
            for (StatisticData statisticDatum : statisticData) {
                log.info(String.format("[statisticTrace] Statistic trace data, appId: %s, envId: %s, serviceCount: %s, traceCount: %s",
                        statisticDatum.getAppId(),
                        statisticDatum.getEnvId(),
                        statisticDatum.getServiceCount(),
                        statisticDatum.getTraceCount()));
            }
        } catch (Exception e) {
            log.error("[statisticTrace] Statistic trace scheduler error: ", e.getMessage());
        }

    }
}
