package com.ledger.app.command.sample.command;

import com.ledger.app.command.core.Command;
import com.ledger.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
