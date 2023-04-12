package io.holoinsight.server.demo.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * created at 2023/3/30
 *
 * @author xzchaoo
 */
@RestController
public class DemoClientWebController {
  @GetMapping("")
  public Object hello() {
    return "demo client";
  }

  @GetMapping("/demo-client")
  public Object demoClient() {
    return "demo client";
  }
}
