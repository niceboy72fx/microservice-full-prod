package com.settlement.app.integration.external;

import io.grpc.ManagedChannel;
import org.springframework.stereotype.Component;

@Component
public class ExternalClient {

    private final ManagedChannel managedChannel;

    public ExternalClient(ManagedChannel managedChannel) {
        this.managedChannel = managedChannel;
    }

    public String healthCheck() {
        // TODO Replace this stub with a generated gRPC client invocation.
        return managedChannel.authority() == null ? "GRPC_UNAVAILABLE" : "GRPC_READY";
    }
}
