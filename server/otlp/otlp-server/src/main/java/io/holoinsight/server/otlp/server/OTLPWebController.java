/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.util.JsonFormat;

import io.holoinsight.server.otlp.core.OTLPLogsHandler;
import io.holoinsight.server.otlp.core.OTLPMetricsHandler;
import io.holoinsight.server.otlp.core.OTLPTraceHandler;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 * @see <a href="https://opentelemetry.io/docs/specs/otlp/#otlphttp">otlp</a>
 */
@Slf4j
@RequestMapping("/otlpapi/v1")
public class OTLPWebController {
  private static final JsonFormat.Parser JSON_FORMAT_PARSER = JsonFormat.parser();
  private static final String JSON = "application/json";
  private static final String PROTOBUF = "application/x-protobuf";

  @Autowired(required = false)
  private OTLPMetricsHandler otlpMetricsHandler;
  @Autowired(required = false)
  private OTLPTraceHandler otlpTraceHandler;
  @Autowired(required = false)
  private OTLPLogsHandler otlpLogsHandler;

  @PostMapping(value = "/metrics", consumes = {JSON, PROTOBUF})
  public ResponseEntity<?> metrics(HttpServletRequest httpRequest)
      throws InvalidProtocolBufferException {
    if (otlpMetricsHandler == null) {
      return ResponseEntity.internalServerError().body("metrics unsupported");
    }

    return handle("metrics", //
        httpRequest, //
        ExportMetricsServiceRequest.parser(), //
        ExportMetricsServiceRequest::newBuilder, //
        otlpMetricsHandler::export);
  }

  @PostMapping(value = "/traces", consumes = {JSON, PROTOBUF})
  public ResponseEntity<?> traces(HttpServletRequest httpRequest)
      throws InvalidProtocolBufferException {
    if (otlpTraceHandler == null) {
      return ResponseEntity.internalServerError().body("traces unsupported");
    }

    return handle("traces", //
        httpRequest, //
        ExportTraceServiceRequest.parser(), //
        ExportTraceServiceRequest::newBuilder, //
        otlpTraceHandler::export);
  }

  @PostMapping(value = "/logs", consumes = {JSON, PROTOBUF})
  public ResponseEntity<?> logs(HttpServletRequest httpRequest)
      throws InvalidProtocolBufferException {
    if (otlpLogsHandler == null) {
      return ResponseEntity.internalServerError().body("logs unsupported");
    }

    return handle("logs", //
        httpRequest, //
        ExportLogsServiceRequest.parser(), //
        ExportLogsServiceRequest::newBuilder, //
        otlpLogsHandler::export);
  }

  private InputStream getInputStream(HttpServletRequest r) throws IOException {
    if ("gzip".equals(r.getHeader("Content-Encoding"))) {
      return new GZIPInputStream(r.getInputStream());
    } else {
      return r.getInputStream();
    }
  }

  private <REQ, RESP extends Message> ResponseEntity<?> handle(String type,
      HttpServletRequest httpRequest, Parser<REQ> parser, Supplier<Message.Builder> mbs,
      Function<REQ, RESP> handler) throws InvalidProtocolBufferException {

    REQ req;

    boolean isProtobuf = PROTOBUF.equals(httpRequest.getContentType());
    try (InputStream is = getInputStream(httpRequest)) {
      if (isProtobuf) {
        req = parser.parseFrom(is);
      } else {
        Message.Builder b = mbs.get();
        JSON_FORMAT_PARSER.merge(new InputStreamReader(is, StandardCharsets.UTF_8), b);
        req = (REQ) b.build();
      }
    } catch (IOException e) {
      log.error("read {} error", type, e);
      return ResponseEntity.badRequest().build();
    }

    RESP resp;
    try {
      resp = handler.apply(req);
    } catch (Exception e) {
      log.error("handle {} error", type, e);
      return ResponseEntity.internalServerError().body(type + " internal error");
    }

    if (isProtobuf) {
      return ResponseEntity.status(HttpStatus.OK) //
          .contentType(MediaType.parseMediaType(PROTOBUF)) //
          .body(resp.toByteArray()); //
    } else {
      String str = JsonFormat.printer().print(resp);
      return ResponseEntity.status(HttpStatus.OK) //
          .contentType(MediaType.APPLICATION_JSON) //
          .body(str); //
    }
  }

}
