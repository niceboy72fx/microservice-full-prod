package com.reporting.app.dao;

import com.reporting.app.bean.SampleModel;
import com.reporting.app.bean.SampleEntity;
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
