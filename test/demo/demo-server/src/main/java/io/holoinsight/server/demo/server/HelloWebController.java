package io.holoinsight.server.demo.server;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>
 * created at 2023/3/30
 *
 * @author xzchaoo
 */
@RestController
@Slf4j
public class HelloWebController {
  @Autowired
  private RedisTemplate<String, String> rt;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping("")
  public Object hello() {
    return "demo server";
  }

  @lombok.SneakyThrows
  @GetMapping("/demo-server")
  public Object demoServer() {
    OkHttpClient client = new OkHttpClient.Builder().build();
    Request request = new Request.Builder() //
        .get() //
        .url("https://www.httpbin.org/get") //
        .build(); //
    try (Response resp = client.newCall(request).execute()) {
      log.info("resp.code=[{}]", resp.code());
    }
    long value = rt.opsForValue().increment("demo.server.calls").longValue();


    int agentCount =
        jdbcTemplate.queryForObject("select count(*) from gaea_agent", new RowMapper<Integer>() {
          @Override
          public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
          }
        });

    return "demo server: " + value + " agentCount:" + agentCount;
  }
}
