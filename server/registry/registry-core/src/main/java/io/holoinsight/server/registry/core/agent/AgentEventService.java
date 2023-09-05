/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.registry.core.meta.MetaSyncService;
import io.holoinsight.server.registry.grpc.agent.ReportEventRequest;

/**
 * Agent event service
 * <p>
 * created at 2023/4/6
 *
 * @author xzchaoo
 */
@Service
public class AgentEventService {
  private static final Logger AGENT_STAT = LoggerFactory.getLogger("AGENT_EVENT_STAT");
  private static final Logger AGENT_DIGEST = LoggerFactory.getLogger("AGENT_EVENT_DIGEST");
  private static final Logger AGENT_UP = LoggerFactory.getLogger("AGENT_EVENT_UP");

  /**
   * Record events to log files
   * 
   * @param ai
   * @param request
   */
  public void reportEvents(AuthInfo ai, ReportEventRequest request) {
    Context ctx = new Context();
    ctx.setWorkspace(request.getHeader().getWorkspace());
    for (ReportEventRequest.Event event : request.getEventsList()) {
      switch (event.getEventType()) {
        case "STAT":
          if ("log_monitor_up".equals(event.getPayloadType())) {
            print(AGENT_UP, ai, ctx, event);
          } else {
            print(AGENT_STAT, ai, ctx, event);
          }
          break;
        default:
          print(AGENT_DIGEST, ai, ctx, event);
          break;
      }
    }
  }

  /**
   * Print one event
   * 
   * @param logger
   * @param ai
   * @param ctx
   * @param event
   */
  private void print(Logger logger, AuthInfo ai, Context ctx, ReportEventRequest.Event event) {
    StringBuilder sb = ctx.sb;
    SimpleDateFormat sdf = ctx.sdf;
    sb.setLength(0);

    appendOne(sb, "tenant", ai.getTenant());
    appendOne(sb, "workspace", ctx.workspace);

    if (event.getBornTimestamp() > 0) {
      appendOne(sb, "born_time", sdf.format(new Date(event.getBornTimestamp())));
    }
    if (event.getEventTimestamp() > 0) {
      appendOne(sb, "event_time", sdf.format(new Date(event.getEventTimestamp())));
    }
    appendOne(sb, "ptype", event.getPayloadType());

    event.getTagsMap().keySet().stream().sorted()
        .forEach(k -> appendOne(sb, k, event.getTagsOrThrow(k)));

    if (!event.getJson().isEmpty()) {
      // Indicates this is a json event
      sb.append(event.getJson()).append(' ');
    } else {
      event.getNumbersMap().keySet().stream().sorted()
          .forEach(k -> appendOne(sb, k, event.getNumbersOrThrow(k)));
      event.getStringsMap().keySet().stream().sorted()
          .forEach(k -> appendOne(sb, k, event.getStringsOrThrow(k)));
      if (event.getLogsCount() > 0) {
        sb.append("logs=").append(JsonUtils.toJson(event.getLogsList())).append(' ');
      }
    }

    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    logger.info(sb.toString());
  }

  private static void appendOne(StringBuilder sb, String key, String value) {
    sb.append(key).append("=[").append(value).append("] ");
  }

  private static void appendOne(StringBuilder sb, String key, long value) {
    sb.append(key).append("=[").append(value).append("] ");
  }

  private static class Context {
    SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm:ss");
    StringBuilder sb = new StringBuilder();
    String workspace;

    public void setWorkspace(String workspace) {
      if (workspace == null) {
        workspace = MetaSyncService.DEFAULT_WORKSPACE;
      }
      this.workspace = workspace;
    }
  }
}
