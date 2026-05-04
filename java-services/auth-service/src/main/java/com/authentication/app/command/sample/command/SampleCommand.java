package com.authentication.app.command.sample.command;

import com.authentication.app.command.core.Command;
import com.authentication.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
