package com.example.app.controller;

import com.example.app.command.core.CommandBus;
import com.example.app.command.sample.command.SampleCommand;
import com.example.app.command.sample.mapper.SampleCommandMapper;
import com.example.app.common.response.ApiResponse;
import com.example.app.dto.request.SampleRequest;
import com.example.app.dto.response.SampleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private final CommandBus commandBus;
    private final SampleCommandMapper sampleCommandMapper;

    public SampleController(CommandBus commandBus, SampleCommandMapper sampleCommandMapper) {
        this.commandBus = commandBus;
        this.sampleCommandMapper = sampleCommandMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SampleResponse>> createSample(@RequestBody SampleRequest request) {
        SampleCommand command = sampleCommandMapper.toCommand(request);
        SampleResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
