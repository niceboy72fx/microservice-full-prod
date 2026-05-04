package com.settlement.app.command.sample.command;

import com.settlement.app.command.core.Command;
import com.settlement.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
