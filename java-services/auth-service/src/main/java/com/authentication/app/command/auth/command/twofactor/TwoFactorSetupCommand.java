package com.authentication.app.command.auth.command.twofactor;

import com.authentication.app.command.core.Command;
import com.authentication.app.dto.response.auth.twofactor.TwoFactorSetupResponse;

public record TwoFactorSetupCommand(String username) implements Command<TwoFactorSetupResponse> {
}
