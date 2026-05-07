package com.authentication.app.controller;

import com.authentication.app.common.response.ApiResponse;
import com.authentication.app.bean.LoginRequest;
import com.authentication.app.bean.RefreshTokenRequest;
import com.authentication.app.bean.RegisterRequest;
import com.authentication.app.bean.TokenRequest;
import com.authentication.app.bean.TwoFactorSetupRequest;
import com.authentication.app.bean.TwoFactorToggleRequest;
import com.authentication.app.bean.LoginResponse;
import com.authentication.app.bean.UserIdResponse;
import com.authentication.app.bean.TwoFactorSetupResponse;
import com.authentication.app.service.AuthDispatchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthDispatchService authDispatchService;

    public AuthController(AuthDispatchService authDispatchService) {
        this.authDispatchService = authDispatchService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authDispatchService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(authDispatchService.login(request, extractClientIp(httpRequest))));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authDispatchService.refresh(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        authDispatchService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/user-id")
    public ResponseEntity<ApiResponse<UserIdResponse>> getUserId(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authDispatchService.getUserId(request)));
    }

    @PostMapping("/2fa/setup")
    public ResponseEntity<ApiResponse<TwoFactorSetupResponse>> setupTwoFactor(@RequestBody TwoFactorSetupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authDispatchService.setupTwoFactor(request)));
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<ApiResponse<Void>> enableTwoFactor(@RequestBody TwoFactorToggleRequest request) {
        authDispatchService.enableTwoFactor(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiResponse<Void>> disableTwoFactor(@RequestBody TwoFactorToggleRequest request) {
        authDispatchService.disableTwoFactor(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
