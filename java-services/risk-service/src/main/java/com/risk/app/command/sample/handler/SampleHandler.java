package com.risk.app.command.sample.handler;

import com.risk.app.command.core.CommandHandler;
import com.risk.app.command.core.CommandRegistry;
import com.risk.app.command.sample.command.SampleCommand;
import com.risk.app.domain.model.SampleModel;
import com.risk.app.domain.service.SampleDomainService;
import com.risk.app.dto.response.SampleResponse;
import com.risk.app.repository.SampleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SampleHandler implements CommandHandler<SampleCommand, SampleResponse> {

    private final CommandRegistry commandRegistry;
    private final SampleDomainService sampleDomainService;
    private final SampleRepository sampleRepository;

    public SampleHandler(
            CommandRegistry commandRegistry,
            SampleDomainService sampleDomainService,
            SampleRepository sampleRepository
    ) {
        this.commandRegistry = commandRegistry;
        this.sampleDomainService = sampleDomainService;
        this.sampleRepository = sampleRepository;
    }

    @PostConstruct
    public void register() {
        commandRegistry.register(this);
    }

    @Override
    public Class<SampleCommand> commandType() {
        return SampleCommand.class;
    }

    @Override
    public SampleResponse handle(SampleCommand command) {
        SampleModel model = sampleDomainService.createModel(command);
        SampleModel savedModel = sampleRepository.save(model);
        return new SampleResponse(savedModel.getId(), savedModel.getName(), savedModel.getStatus());
    }
}
