package com.authentication.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

  public static void main(String[] args) {
    com.commonservice.common.dto.CommonResponse<String> res =
      new com.commonservice.common.dto.CommonResponse<>(
        true,
        "Hello from common",
        "data"
      );
    System.out.println("CommonResponse success: " + res.isSuccess());
    SpringApplication.run(AppApplication.class, args);
  }
}
