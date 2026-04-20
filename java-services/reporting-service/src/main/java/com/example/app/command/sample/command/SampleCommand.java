package com.example.app.command.sample.command;

import com.example.app.command.core.Command;
import com.example.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
