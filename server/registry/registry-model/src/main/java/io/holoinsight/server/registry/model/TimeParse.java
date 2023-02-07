/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/23
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class TimeParse {
  /**
   * <ul>
   * <li>auto: antmonitor风格的自动猜测, 此时format/timezone无效</li>
   * <li>processTime: 以拉到日志的时间作为数据的时间, 此时format/timezone无效</li>
   * <li>elect: 通过某个切出来的字段作为时间并进行时间parse</li>
   * </ul>
   */
  @Nonnull
  private String type;
  /**
   * 如果type为elect, 那么会通过elect选出一个字段进行时间解析
   */
  private Elect elect;
  /**
   * <ul>
   * <li>unix秒时间戳, 此时timezone字段无效</li>
   * <li>unix毫秒, 此时timezone字段无效</li>
   * <li>TODO</li>
   * </ul>
   */
  private String format;
  /**
   * 可选, 不填则为agent所在的本地时间
   */
  private String timezone;
  /**
   * golang风格的layout, 此时layout字段生效
   */
  private String layout;
}
