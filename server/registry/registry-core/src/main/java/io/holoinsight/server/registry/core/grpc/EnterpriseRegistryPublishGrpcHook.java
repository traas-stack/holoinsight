/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.holoinsight.server.common.GrpcUtils;
import io.holoinsight.server.common.hook.PublishGrpcHook;

import org.springframework.stereotype.Component;

/**
 * <p>created at 2022/2/28
 *
 * @author zzhb101
 */
@Component
public class EnterpriseRegistryPublishGrpcHook implements PublishGrpcHook {
    @Override
    public void onPublish(ServerBuilder<?> b, ServerServiceDefinition ssd) {
        // Compatible with old agent version. After all agents move to new version, remove this code.
        if (ssd.getServiceDescriptor().getName().startsWith("io.holoinsight.server.registry.grpc.agent")) {
            b.addService(GrpcUtils.rebind(ssd, "io.holoinsight.server.registry.grpc.agent", "com.alipay.cloudmonitor.registry.grpc.agent"));
        }

        // Compatible with old cloudmonitor-home.
        if (ssd.getServiceDescriptor().getName().startsWith("io.holoinsight.server.registry.grpc.prod")) {
            b.addService(GrpcUtils.rebind(ssd, "io.holoinsight.server.registry.grpc.prod", "com.alipay.cloudmonitor.registry.grpc.prod"));
        }
    }
}

