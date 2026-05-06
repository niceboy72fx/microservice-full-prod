package com.authentication.app;

import com.commonservice.common.observability.Logger;
import com.commonservice.common.observability.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.authentication.app", "com.commonservice.common"})
public class AppApplication {
  private static final Logger LOG = LoggerFactory.create("auth-service");

  public static void main(String[] args) {
    com.commonservice.common.dto.CommonResponse<String> res =
      new com.commonservice.common.dto.CommonResponse<>(
        true,
        "Hello from common",
        "data"
      );
    LOG.info("Bootstrap common response prepared", java.util.Map.of("success", res.isSuccess()));
    SpringApplication.run(AppApplication.class, args);
  }
}
