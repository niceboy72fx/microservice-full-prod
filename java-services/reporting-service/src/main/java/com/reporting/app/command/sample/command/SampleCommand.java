package com.reporting.app.command.sample.command;

import com.reporting.app.command.core.Command;
import com.reporting.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
