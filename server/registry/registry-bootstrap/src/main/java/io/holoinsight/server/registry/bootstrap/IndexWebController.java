/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.bootstrap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * created at 2022/11/21
 *
 * @author zzhb101
 */
@RestController
public class IndexWebController {
  @GetMapping("")
  public Object index() {
    return "Holoinsight Registry";
  }
}
