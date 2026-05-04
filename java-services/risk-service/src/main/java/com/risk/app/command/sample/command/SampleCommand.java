package com.risk.app.command.sample.command;

import com.risk.app.command.core.Command;
import com.risk.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
