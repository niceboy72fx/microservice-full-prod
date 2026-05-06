package com.authentication.app.controller.auth.password;

import com.authentication.app.command.auth.command.ForgotPasswordCommand;
import com.authentication.app.command.auth.mapper.AuthCommandMapper;
import com.authentication.app.command.core.CommandBus;
import com.authentication.app.common.response.ApiResponse;
import com.authentication.app.dto.request.auth.ForgotPasswordRequest;
import com.authentication.app.dto.response.auth.ForgotPasswordResponse;
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

    private final CommandBus commandBus;
    private final AuthCommandMapper authCommandMapper;

    public ForgotPasswordController(CommandBus commandBus, AuthCommandMapper authCommandMapper) {
        this.commandBus = commandBus;
        this.authCommandMapper = authCommandMapper;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        ForgotPasswordCommand command = authCommandMapper.toForgotPasswordCommand(request);
        ForgotPasswordResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
