package com.email.app.command.sample.command;

import com.email.app.command.core.Command;
import com.email.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
