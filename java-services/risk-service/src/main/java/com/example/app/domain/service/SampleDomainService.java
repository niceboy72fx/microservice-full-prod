package com.example.app.domain.service;

import com.example.app.command.sample.command.SampleCommand;
import com.example.app.component.IdGeneratorComponent;
import com.example.app.domain.model.SampleModel;
import com.example.app.integration.external.ExternalClient;
import org.springframework.stereotype.Service;

@Service
public class SampleDomainService {

    private final IdGeneratorComponent idGeneratorComponent;
    private final ExternalClient externalClient;

    public SampleDomainService(IdGeneratorComponent idGeneratorComponent, ExternalClient externalClient) {
        this.idGeneratorComponent = idGeneratorComponent;
        this.externalClient = externalClient;
    }

    public SampleModel createModel(SampleCommand command) {
        String generatedId = idGeneratorComponent.generate();
        String externalStatus = externalClient.healthCheck();

        // TODO Implement generic business rules and orchestration here.
        return new SampleModel(generatedId, command.getName(), "CREATED:" + externalStatus);
    }
}
