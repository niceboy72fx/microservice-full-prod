package com.authentication.app.command.auth.mapper;

import com.authentication.app.command.auth.command.GetUserIdCommand;
import com.authentication.app.command.auth.command.ForgotPasswordCommand;
import com.authentication.app.command.auth.command.LoginCommand;
import com.authentication.app.command.auth.command.LogoutCommand;
import com.authentication.app.command.auth.command.RefreshTokenCommand;
import com.authentication.app.command.auth.command.RegisterCommand;
import com.authentication.app.command.auth.command.twofactor.TwoFactorDisableCommand;
import com.authentication.app.command.auth.command.twofactor.TwoFactorEnableCommand;
import com.authentication.app.command.auth.command.twofactor.TwoFactorSetupCommand;
import com.authentication.app.dto.request.auth.LoginRequest;
import com.authentication.app.dto.request.auth.ForgotPasswordRequest;
import com.authentication.app.dto.request.auth.RefreshTokenRequest;
import com.authentication.app.dto.request.auth.RegisterRequest;
import com.authentication.app.dto.request.auth.TokenRequest;
import com.authentication.app.dto.request.auth.twofactor.TwoFactorSetupRequest;
import com.authentication.app.dto.request.auth.twofactor.TwoFactorToggleRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthCommandMapper {

    public RegisterCommand toRegisterCommand(RegisterRequest request) {
        return new RegisterCommand(request.getUsername(), request.getPassword());
    }

    public LoginCommand toLoginCommand(LoginRequest request, String ipAddress) {
        return new LoginCommand(request.getUsername(), request.getPassword(), request.getOtpCode(), ipAddress);
    }

    public ForgotPasswordCommand toForgotPasswordCommand(ForgotPasswordRequest request) {
        return new ForgotPasswordCommand(request.getEmail());
    }

    public RefreshTokenCommand toRefreshTokenCommand(RefreshTokenRequest request) {
        return new RefreshTokenCommand(request.getRefreshToken());
    }

    public LogoutCommand toLogoutCommand(RefreshTokenRequest request) {
        return new LogoutCommand(request.getRefreshToken());
    }

    public GetUserIdCommand toGetUserIdCommand(TokenRequest request) {
        return new GetUserIdCommand(request.getToken());
    }

    public TwoFactorSetupCommand toTwoFactorSetupCommand(TwoFactorSetupRequest request) {
        return new TwoFactorSetupCommand(request.getUsername());
    }

    public TwoFactorEnableCommand toTwoFactorEnableCommand(TwoFactorToggleRequest request) {
        return new TwoFactorEnableCommand(request.getUsername(), request.getCode());
    }

    public TwoFactorDisableCommand toTwoFactorDisableCommand(TwoFactorToggleRequest request) {
        return new TwoFactorDisableCommand(request.getUsername(), request.getCode());
    }
}
