package io.holoinsight.server.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * created at 2023/3/30
 *
 * @author xzchaoo
 */
@SpringBootApplication
@EnableScheduling
public class DemoClientApp {
  public static void main(String[] args) {
    SpringApplication.run(DemoClientApp.class);
  }
}
