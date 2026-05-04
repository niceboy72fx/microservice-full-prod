package com.settlement.app.controller;

import com.settlement.app.command.core.CommandBus;
import com.settlement.app.command.sample.command.SampleCommand;
import com.settlement.app.command.sample.mapper.SampleCommandMapper;
import com.settlement.app.common.response.ApiResponse;
import com.settlement.app.dto.request.SampleRequest;
import com.settlement.app.dto.response.SampleResponse;
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
