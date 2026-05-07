package com.email.app.service;

import com.email.app.command.SampleCommand;
import com.email.app.utility.IdGeneratorComponent;
import com.email.app.bean.SampleModel;
import com.email.app.integration.external.ExternalClient;
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
