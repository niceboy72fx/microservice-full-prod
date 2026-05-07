package com.authentication.app.service;

import com.authentication.app.command.GetUserIdCommand;
import com.authentication.app.command.ForgotPasswordCommand;
import com.authentication.app.command.LoginCommand;
import com.authentication.app.command.LogoutCommand;
import com.authentication.app.command.RefreshTokenCommand;
import com.authentication.app.command.RegisterCommand;
import com.authentication.app.command.TwoFactorDisableCommand;
import com.authentication.app.command.TwoFactorEnableCommand;
import com.authentication.app.command.TwoFactorSetupCommand;
import com.authentication.app.bean.LoginRequest;
import com.authentication.app.bean.ForgotPasswordRequest;
import com.authentication.app.bean.RefreshTokenRequest;
import com.authentication.app.bean.RegisterRequest;
import com.authentication.app.bean.TokenRequest;
import com.authentication.app.bean.TwoFactorSetupRequest;
import com.authentication.app.bean.TwoFactorToggleRequest;
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
