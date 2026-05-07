package com.authentication.app.controller;

import com.authentication.app.common.response.ApiResponse;
import com.authentication.app.bean.ForgotPasswordRequest;
import com.authentication.app.bean.ForgotPasswordResponse;
import com.authentication.app.service.AuthDispatchService;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
@Scope("prototype")
@RequestMapping("/api/v1/auth")
public class ForgotPasswordController {

    private final AuthDispatchService authDispatchService;

    public ForgotPasswordController(AuthDispatchService authDispatchService) {
        this.authDispatchService = authDispatchService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authDispatchService.forgotPassword(request)));
    }
}
