package com.payment.app.command.sample.command;

import com.payment.app.command.core.Command;
import com.payment.app.dto.response.SampleResponse;

public class SampleCommand implements Command<SampleResponse> {

    private final String name;

    public SampleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
