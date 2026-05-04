package com.risk.app.persistence.mapper;

import com.risk.app.domain.model.SampleModel;
import com.risk.app.persistence.entity.SampleEntity;
import org.springframework.stereotype.Component;

@Component
public class SamplePersistenceMapper {

    public SampleEntity toEntity(SampleModel model) {
        return new SampleEntity(model.getId(), model.getName(), model.getStatus());
    }

    public SampleModel toModel(SampleEntity entity) {
        return new SampleModel(entity.getId(), entity.getName(), entity.getStatus());
    }
}
