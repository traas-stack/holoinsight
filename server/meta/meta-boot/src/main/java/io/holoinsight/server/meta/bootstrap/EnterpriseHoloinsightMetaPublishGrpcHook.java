/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.bootstrap;

import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.holoinsight.server.common.GrpcUtils;
import io.holoinsight.server.common.hook.PublishGrpcHook;

import org.springframework.stereotype.Component;

/**
 * <p>created at 2022/12/20
 *
 * @author jsy1001de
 */
@Component
public class EnterpriseHoloinsightMetaPublishGrpcHook implements PublishGrpcHook {
    @Override
    public void onPublish(ServerBuilder<?> b, ServerServiceDefinition ssd) {
        // Compatible with old agent version. After all agents move to new version, remove this code.
        if (ssd.getServiceDescriptor().getName().startsWith("io.holoinsight.server.meta")) {
            b.addService(GrpcUtils.rebind(ssd, "io.holoinsight.server.meta", "com.alipay.cloudmonitor.meta"));
        }
    }
}
