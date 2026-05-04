package com.account.app.controller;

import com.account.app.command.core.CommandBus;
import com.account.app.command.sample.command.SampleCommand;
import com.account.app.command.sample.mapper.SampleCommandMapper;
import com.account.app.common.response.ApiResponse;
import com.account.app.dto.request.SampleRequest;
import com.account.app.dto.response.SampleResponse;
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
