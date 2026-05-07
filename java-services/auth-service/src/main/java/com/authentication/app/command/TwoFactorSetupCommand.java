package com.authentication.app.command;

import com.authentication.app.command.Command;
import com.authentication.app.bean.TwoFactorSetupResponse;

public record TwoFactorSetupCommand(String username) implements Command<TwoFactorSetupResponse> {
}
