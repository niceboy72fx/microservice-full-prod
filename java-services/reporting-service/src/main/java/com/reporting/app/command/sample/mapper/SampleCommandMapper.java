package com.reporting.app.command.sample.mapper;

import com.reporting.app.command.sample.command.SampleCommand;
import com.reporting.app.dto.request.SampleRequest;
import org.springframework.stereotype.Component;

@Component
public class SampleCommandMapper {

    public SampleCommand toCommand(SampleRequest request) {
        String name = request.getName() == null || request.getName().isBlank() ? "Sample" : request.getName().trim();
        return new SampleCommand(name);
    }
}
