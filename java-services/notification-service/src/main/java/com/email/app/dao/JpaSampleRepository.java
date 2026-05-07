package com.email.app.dao;

import com.email.app.bean.SampleModel;
import com.email.app.bean.SampleEntity;
import com.email.app.dao.SamplePersistenceMapper;
import com.email.app.dao.SampleRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class JpaSampleRepository implements SampleRepository {

    private final SamplePersistenceMapper samplePersistenceMapper;
    private final Map<String, SampleEntity> storage = new ConcurrentHashMap<>();

    public JpaSampleRepository(SamplePersistenceMapper samplePersistenceMapper) {
        this.samplePersistenceMapper = samplePersistenceMapper;
    }

    @Override
    public SampleModel save(SampleModel sampleModel) {
        // TODO Replace in-memory persistence with a real data source when needed.
        SampleEntity entity = samplePersistenceMapper.toEntity(sampleModel);
        storage.put(entity.getId(), entity);
        return samplePersistenceMapper.toModel(entity);
    }
}
