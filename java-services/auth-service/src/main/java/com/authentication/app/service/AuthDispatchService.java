package com.authentication.app.service;

import com.authentication.app.command.ForgotPasswordCommand;
import com.authentication.app.command.GetUserIdCommand;
import com.authentication.app.command.LoginCommand;
import com.authentication.app.command.LogoutCommand;
import com.authentication.app.command.RefreshTokenCommand;
import com.authentication.app.command.RegisterCommand;
import com.authentication.app.command.TwoFactorDisableCommand;
import com.authentication.app.command.TwoFactorEnableCommand;
import com.authentication.app.command.TwoFactorSetupCommand;
import com.authentication.app.service.AuthCommandMapper;
import com.authentication.app.command.CommandBus;
import com.authentication.app.bean.ForgotPasswordRequest;
import com.authentication.app.bean.LoginRequest;
import com.authentication.app.bean.RefreshTokenRequest;
import com.authentication.app.bean.RegisterRequest;
import com.authentication.app.bean.TokenRequest;
import com.authentication.app.bean.TwoFactorSetupRequest;
import com.authentication.app.bean.TwoFactorToggleRequest;
import com.authentication.app.bean.ForgotPasswordResponse;
import com.authentication.app.bean.LoginResponse;
import com.authentication.app.bean.UserIdResponse;
import com.authentication.app.bean.TwoFactorSetupResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthDispatchService {

    private final CommandBus commandBus;
    private final AuthCommandMapper mapper;

    public AuthDispatchService(CommandBus commandBus, AuthCommandMapper mapper) {
        this.commandBus = commandBus;
        this.mapper = mapper;
    }

    public LoginResponse register(RegisterRequest request) {
        RegisterCommand command = mapper.toRegisterCommand(request);
        return commandBus.execute(command);
    }

    public LoginResponse login(LoginRequest request, String ipAddress) {
        LoginCommand command = mapper.toLoginCommand(request, ipAddress);
        return commandBus.execute(command);
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshTokenCommand command = mapper.toRefreshTokenCommand(request);
        return commandBus.execute(command);
    }

    public void logout(RefreshTokenRequest request) {
        LogoutCommand command = mapper.toLogoutCommand(request);
        commandBus.execute(command);
    }

    public UserIdResponse getUserId(TokenRequest request) {
        GetUserIdCommand command = mapper.toGetUserIdCommand(request);
        return commandBus.execute(command);
    }

    public TwoFactorSetupResponse setupTwoFactor(TwoFactorSetupRequest request) {
        TwoFactorSetupCommand command = mapper.toTwoFactorSetupCommand(request);
        return commandBus.execute(command);
    }

    public void enableTwoFactor(TwoFactorToggleRequest request) {
        TwoFactorEnableCommand command = mapper.toTwoFactorEnableCommand(request);
        commandBus.execute(command);
    }

    public void disableTwoFactor(TwoFactorToggleRequest request) {
        TwoFactorDisableCommand command = mapper.toTwoFactorDisableCommand(request);
        commandBus.execute(command);
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        ForgotPasswordCommand command = mapper.toForgotPasswordCommand(request);
        return commandBus.execute(command);
    }
}
