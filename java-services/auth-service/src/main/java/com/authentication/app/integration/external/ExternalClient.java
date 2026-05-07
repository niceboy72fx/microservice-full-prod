package com.authentication.app.integration;

import io.grpc.ManagedChannel;
import org.springframework.stereotype.Component;

@Component
public class ExternalClient {

  private final ManagedChannel managedChannel;

  public ExternalClient(ManagedChannel managedChannel) {
    this.managedChannel = managedChannel;
  }

  public String healthCheck() {
    return managedChannel.authority() == null
      ? "GRPC_UNAVAILABLE"
      : "GRPC_READY";
  }
}
