package com.authentication.app.controller.auth;

import com.authentication.app.command.auth.command.GetUserIdCommand;
import com.authentication.app.command.auth.command.LoginCommand;
import com.authentication.app.command.auth.command.LogoutCommand;
import com.authentication.app.command.auth.command.RefreshTokenCommand;
import com.authentication.app.command.auth.command.RegisterCommand;
import com.authentication.app.command.auth.command.twofactor.TwoFactorDisableCommand;
import com.authentication.app.command.auth.command.twofactor.TwoFactorEnableCommand;
import com.authentication.app.command.auth.command.twofactor.TwoFactorSetupCommand;
import com.authentication.app.command.auth.mapper.AuthCommandMapper;
import com.authentication.app.command.core.CommandBus;
import com.authentication.app.common.response.ApiResponse;
import com.authentication.app.dto.request.auth.LoginRequest;
import com.authentication.app.dto.request.auth.RefreshTokenRequest;
import com.authentication.app.dto.request.auth.RegisterRequest;
import com.authentication.app.dto.request.auth.TokenRequest;
import com.authentication.app.dto.request.auth.twofactor.TwoFactorSetupRequest;
import com.authentication.app.dto.request.auth.twofactor.TwoFactorToggleRequest;
import com.authentication.app.dto.response.auth.LoginResponse;
import com.authentication.app.dto.response.auth.UserIdResponse;
import com.authentication.app.dto.response.auth.twofactor.TwoFactorSetupResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CommandBus commandBus;
    private final AuthCommandMapper authCommandMapper;

    public AuthController(CommandBus commandBus, AuthCommandMapper authCommandMapper) {
        this.commandBus = commandBus;
        this.authCommandMapper = authCommandMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody RegisterRequest request) {
        RegisterCommand command = authCommandMapper.toRegisterCommand(request);
        LoginResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginCommand command = authCommandMapper.toLoginCommand(request, extractClientIp(httpRequest));
        LoginResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = authCommandMapper.toRefreshTokenCommand(request);
        LoginResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        LogoutCommand command = authCommandMapper.toLogoutCommand(request);
        commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/user-id")
    public ResponseEntity<ApiResponse<UserIdResponse>> getUserId(@RequestBody TokenRequest request) {
        GetUserIdCommand command = authCommandMapper.toGetUserIdCommand(request);
        UserIdResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/2fa/setup")
    public ResponseEntity<ApiResponse<TwoFactorSetupResponse>> setupTwoFactor(@RequestBody TwoFactorSetupRequest request) {
        TwoFactorSetupCommand command = authCommandMapper.toTwoFactorSetupCommand(request);
        TwoFactorSetupResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<ApiResponse<Void>> enableTwoFactor(@RequestBody TwoFactorToggleRequest request) {
        TwoFactorEnableCommand command = authCommandMapper.toTwoFactorEnableCommand(request);
        commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiResponse<Void>> disableTwoFactor(@RequestBody TwoFactorToggleRequest request) {
        TwoFactorDisableCommand command = authCommandMapper.toTwoFactorDisableCommand(request);
        commandBus.execute(command);
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
