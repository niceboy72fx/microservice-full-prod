package com.authentication.app.domain.service;

import com.authentication.app.command.sample.command.SampleCommand;
import com.authentication.app.component.IdGeneratorComponent;
import com.authentication.app.domain.model.SampleModel;
import com.authentication.app.integration.external.ExternalClient;
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
