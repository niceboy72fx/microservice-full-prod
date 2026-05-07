package com.authentication.app.command;

import com.authentication.app.command.Command;
import com.authentication.app.bean.UserIdResponse;

public record GetUserIdCommand(String token) implements Command<UserIdResponse> {
}
