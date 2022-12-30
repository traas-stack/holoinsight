/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import io.holoinsight.server.registry.core.utils.Dict;

/**
 * <p>created at 2022/3/1
 *
 * @author zzhb101
 */
@Getter
@Setter
@ToString
public class ExecutorSelector {
    public static final String SIDECAR   = "sidecar";
    public static final String DAEMONSET = "daemonset";
    public static final String FIXED     = "fixed";
    public static final String DIM       = "dim";
    public static final String CENTRAL   = "central";

    private String  type;
    /**
     * 当type为sidecar时, 该字段非null
     */
    @Deprecated
    private Sidecar sidecar;
    /**
     * 当type为fixed时, 该字段非null
     */
    private Fixed   fixed;
    private Dim     dim;
    private Central central;

    public void reuseStrings() {
        type = Dict.get(type);
        if (central != null) {
            central.name = Dict.get(central.name);
        }
    }

    @Deprecated
    @Data
    public static class Sidecar {
        public Sidecar() {
        }

        // TODO 临时兼容
        public Sidecar(String temp) {
        }
    }

    @Data
    public static class Fixed {
        // 固定到一台上
        // 如何引用一台?
        // 可以用dim表达式, 用户必须自己保证只有一台
    }

    @Data
    public static class Dim {
        // 不用包含 sofa#sofa_server
        private String[] expression;
    }

    @Data
    public static class Central {
        private String name;
    }
}
