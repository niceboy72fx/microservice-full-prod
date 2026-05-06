package com.authentication.app.command.auth.command;

import com.authentication.app.command.core.Command;
import com.authentication.app.dto.response.auth.UserIdResponse;

public record GetUserIdCommand(String token) implements Command<UserIdResponse> {
}
