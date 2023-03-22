/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class From {
  /**
   * 数据来源类型 cpu/mem/disk/load/traffic/processperf/log
   */
  private String type;

  private ProcessPerf processPerf;
  private Log log;

  /**
   * 进程性能监控, 该类的参数完全是可选的
   */
  @ToString
  @Getter
  @Setter
  public static class ProcessPerf {
    /**
     * 如果非空则进程用户必须在这个名单内 'root' 'admin'
     */
    private Set<String> includeUsernames;
    /**
     * 如果非空则进程用户必须不在这个名单内 'root' 'admin'
     */
    private Set<String> excludeUsernames;
    /**
     * 如果非空则进程名必须在这个名单内 'ssh' 'java'
     */
    private Set<String> includeProcesses;
    /**
     * 如果非空则进程名必须不在这个名单内 'ssh' 'java'
     */
    private Set<String> excludeProcesses;
    /**
     * 如果非空则进程的cmdline必须包含该关键字 '/opt/taobao/java/bin/java -cp /home/admin/xxx/lib/* -server
     * -XX:+UnlockExperimentalVMOptions -Xms8g -Xmx8g ..'
     */
    private Set<String> includeKeywords;
    /**
     * 如果非空则进程的cmdline必须不包含该关键字 '/opt/taobao/java/bin/java -cp /home/admin/xxx/lib/* -server
     * -XX:+UnlockExperimentalVMOptions -Xms8g -Xmx8g ..'
     */
    private Set<String> excludeKeywords;
  }

  /**
   * 日志清洗
   */
  @ToString
  @Getter
  @Setter
  public static class Log {
    /**
     * 日志路径 允许多个
     */
    @Nonnull
    private List<LogPath> path;
    /**
     * 日志字符集, 如果不填的默认utf-8
     */
    @Nullable
    private String charset;
    /**
     * 定义日志如何切分
     */
    @Nonnull
    private Parse parse;
    /**
     * 定义时间如何解析
     */
    @Nonnull
    private TimeParse time;

    @Nullable
    private Multiline multiline;

    @ToString
    @Getter
    @Setter
    public static class LogPath {
      /**
       * path/glob/regexp/format. 推荐 path > format > glob > regexp. 注意regexp和glob可能会导致多个文件.
       */
      private String type;
      /**
       * 当type==path时填写该字段为绝对路径 当type==glob时填写该字段为glob表达式 /home/admin/logs/gateway/common-error*.log
       * 当type==format时填写该字段为表达式,
       * /home/admin/logs/gateway/{time:yyyy}/{time:MM}/{time:dd}/common-error.log
       * 当type==regexp填写该字段, 路径全称要匹配的正则表达式
       */
      private String pattern;
      /**
       * 当type==regexp填写该字段, 为一个基础父目录 比如 /home/admin/logs/gateway
       */
      private String dir;
    }

    @ToString
    @Getter
    @Setter
    public static class Parse {
      /**
       * 在parse前可以先进行一次where, 此时where里的elect只能使用 leftRight/line
       */
      @Nullable
      private Where where;
      /**
       * <ul>
       * <li>none: 不需要事先切分, 即antmonitor传统风格, 此时所有的elect只能使用 leftRight/line</li>
       * <li>separator: 按分隔符切分, 此时elect可以使用leftRight/line/refIndex</li>
       * <li>regexp: 按分隔符切分, 此时elect可以使用leftRight/line/refName</li>
       * <li>grok: 按分隔符切分, 此时elect可以使用leftRight/line/refName</li>
       * <li>json: 按分隔符切分, 此时elect可以使用leftRight/line/refName</li>
       * </ul>
       */
      @Nonnull
      private String type;
      private Separator separator;
      private Regexp regexp;
      private Grok grok;
      private Json json;
      private Multiline multiline;
    }

    @ToString
    @Getter
    @Setter
    public static class Multiline {
      @Nullable
      private Where match;

      @Nullable
      private boolean enabled;

      /**
       * 'previous' 'next'
       */
      @NonNull
      private String what;
    }

    @ToString
    @Getter
    @Setter
    public static class Separator {
      @Nonnull
      private String separator;
    }

    @ToString
    @Getter
    @Setter
    public static class Regexp {
      @Nonnull
      private String expression;
    }

    @ToString
    @Getter
    @Setter
    public static class Grok {
      @Nonnull
      private String expression;
    }

    @ToString
    @Getter
    @Setter
    public static class Json {
    }
  }
}
