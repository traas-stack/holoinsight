/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.streambiz;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import io.holoinsight.server.registry.core.grpc.BiStreamService;
import io.holoinsight.server.registry.core.grpc.stream.ServerStream;
import io.holoinsight.server.registry.core.grpc.stream.ServerStreamManager;
import io.holoinsight.server.registry.core.utils.ApiResp;
import io.holoinsight.server.registry.grpc.prod.HttpProxyRequest;
import io.holoinsight.server.registry.grpc.prod.HttpProxyResponse;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesRequest;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.MatchFilesRequest;
import io.holoinsight.server.registry.grpc.prod.MatchFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileRequest;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.SplitLogRequest;
import io.holoinsight.server.registry.grpc.prod.SplitLogResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * BiStream debug api
 * <p>
 * created at 2022/3/10
 *
 * @author zzhb101
 * @author xzchaoo
 */
@RestController
@RequestMapping("/internal/api/registry/bistream")
public class BiStreamWebController {
  @Autowired
  private ServerStreamManager m;
  @Autowired
  private BiStreamService biStreamService;

  @GetMapping({"", "/ids"})
  public ApiResp get() {
    return ApiResp.success(m.getIds());
  }

  /**
   * Ping agent with a payload message.
   * 
   * @param agentId
   * @param msg
   * @return
   */
  @GetMapping("/ping")
  public Object ping(@RequestParam("agentId") String agentId, @RequestParam("msg") String msg) {
    return biStreamService.proxySimple(agentId, BizTypes.ECHO, ByteString.copyFromUtf8(msg)) //
        .map(ByteString::toStringUtf8); //
  }


  @GetMapping("/perf")
  public Object perf(@RequestParam("agentId") String agentId, //
      @RequestParam(value = "count", defaultValue = "count") int count, //
      @RequestParam(value = "concurrency", defaultValue = "16") int concurrency) { //
    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    long begin = System.currentTimeMillis();
    return Flux.range(0, count) //
        .flatMap(ignored -> {
          return s.rpc(BizTypes.ECHO, ByteString.copyFromUtf8("msg")) //
              .timeout(Duration.ofSeconds(3)); //
        }, concurrency) //
        .ignoreElements().then(Mono.fromCallable(() -> {
          long cost = System.currentTimeMillis() - begin;
          return String.format("count=%d concurrency=%d cost=%d", count, concurrency, cost);
        }));
  }

  @GetMapping("/listFiles")
  public Object listFiles(@RequestParam("agentId") String agentId, //
      @RequestParam(value = "name") String name, //
      @RequestParam(value = "depth", defaultValue = "2") int depth,
      @RequestParam(value = "exts", defaultValue = "") List<String> exts,
      @RequestParam(value = "includeParents", defaultValue = "false") boolean includeParents) { //
    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    ListFilesRequest request = ListFilesRequest.newBuilder() //
        .setName(name) //
        .setMaxDepth(depth) //
        .addAllIncludeExts(exts) //
        .setIncludeParents(includeParents) //
        .build(); //
    return s.rpc(BizTypes.LIST_FILES, request.toByteString()) //
        .timeout(Duration.ofSeconds(3)) //
        .map(respCmd -> { //
          ListFilesResponse resp;
          try {
            resp = ListFilesResponse.parseFrom(respCmd.getData());
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
          }
          try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(JsonFormat.printer().print(resp));
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @GetMapping("/previewFile")
  public Object previewFile(@RequestParam("agentId") String agentId, //
      @RequestParam(value = "path") String path, //
      @RequestParam(value = "maxBytes", defaultValue = "4096") int maxBytes, //
      @RequestParam(value = "maxLines", defaultValue = "10") int maxLines, //
      @RequestParam(value = "charset", defaultValue = "") String charset) {
    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    PreviewFileRequest request = PreviewFileRequest.newBuilder() //
        .setPath(path) //
        .setMaxBytes(maxBytes) //
        .setMaxLines(maxLines) //
        .setCharset(charset) //
        .build(); //
    return s.rpc(BizTypes.PREVIEW_FILE, request.toByteString()) //
        .timeout(Duration.ofSeconds(3)) //
        .map(respCmd -> { //
          if (respCmd.getBizType() == BizTypes.BIZ_ERROR) {
            return respCmd.getData().toStringUtf8();
          }
          PreviewFileResponse resp;
          try {
            resp = PreviewFileResponse.parseFrom(respCmd.getData());
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
          }
          try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(JsonFormat.printer().print(resp));
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @PostMapping("/splitLog")
  public Object splitLog(@RequestParam("agentId") String agentId, //
      @RequestParam(value = "content") String content, //
      @RequestParam(value = "regexp") String regexp) {
    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    SplitLogRequest request = SplitLogRequest.newBuilder() //
        .setContent(content) //
        .setRegexp(regexp) //
        .build(); //
    return s.rpc(BizTypes.SPLIT_LOG, request.toByteString()) //
        .timeout(Duration.ofSeconds(3)) //
        .map(respCmd -> { //
          SplitLogResponse resp;
          try {
            resp = SplitLogResponse.parseFrom(respCmd.getData());
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
          }
          try {
            return JsonFormat.printer().print(resp);
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @GetMapping("/inspect")
  public Object inspect(@RequestParam("agentId") String agentId) {
    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    return s.rpc(BizTypes.INSPECT, ByteString.EMPTY) //
        .timeout(Duration.ofSeconds(3)) //
        .map(respCmd -> { //
          try {
            return InspectResponse.parseFrom(respCmd.getData()).getResult();
          } catch (Throwable e) {
            throw new RuntimeException(e);
          }
        });
  }

  @GetMapping("/matchFiles")
  public Object matchFiles(@RequestParam("agentId") String agentId, //
      @RequestParam("type") String type, //
      @RequestParam("pattern") String pattern, //
      @RequestParam(value = "dir", defaultValue = "") String dir, //
      @RequestParam(value = "maxVisited", defaultValue = "1000") int maxVisited, //
      @RequestParam(value = "maxMatched", defaultValue = "100") int maxMatched) { //

    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    MatchFilesRequest req = MatchFilesRequest.newBuilder() //
        .setAgentId(agentId) //
        .setType(type) //
        .setPattern(pattern) //
        .setDir(dir) //
        .setMaxVisited(maxVisited) //
        .setMaxMatched(maxMatched) //
        .build(); //

    return s.rpc(BizTypes.MATCH_FILES, req.toByteString()) //
        .timeout(Duration.ofSeconds(3)) //
        .map(respCmd -> { //
          try {
            MatchFilesResponse resp = MatchFilesResponse.parseFrom(respCmd.getData());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(JsonFormat.printer().print(resp));
          } catch (Throwable e) {
            throw new RuntimeException(e);
          }
        });
  }

  @GetMapping("/httpProxy")
  public Object httpProxy(@RequestParam("agentId") String agentId) { //
    ServerStream s = m.get(agentId);
    if (s == null) {
      return ApiResp.error("not found " + agentId);
    }
    HttpProxyRequest req = HttpProxyRequest.newBuilder() //
        .setAgentId(agentId) //
        .setUrl("http://localhost:9117") //
        .build(); //
    return s.rpc(BizTypes.HTTP_PROXY, req.toByteString()) //
        .timeout(Duration.ofSeconds(3)) //
        .map(respCmd -> { //
          try {
            HttpProxyResponse resp = HttpProxyResponse.parseFrom(respCmd.getData());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(JsonFormat.printer().print(resp));
          } catch (Throwable e) {
            throw new RuntimeException(e);
          }
        });
  }
}
