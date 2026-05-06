package com.commonservice.common.circuitbreaker;

import com.commonservice.common.config.CircuitBreakerSharedConfigLoader;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CircuitBreakerConfigProperties.class)
public class CircuitBreakerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerRegistry.class)
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfigProperties properties) {
        CircuitBreakerConfigProperties merged = mergeWithShared(properties);
        return new InMemoryCircuitBreakerRegistry(merged);
    }

    private CircuitBreakerConfigProperties mergeWithShared(CircuitBreakerConfigProperties baseProperties) {
        Optional<CircuitBreakerConfigProperties> sharedOptional = CircuitBreakerSharedConfigLoader.loadOptional();
        if (sharedOptional.isEmpty()) {
            return baseProperties;
        }

        CircuitBreakerConfigProperties shared = sharedOptional.get();
        CircuitBreakerConfigProperties merged = new CircuitBreakerConfigProperties();

        copyRule(shared.getDefaults(), merged.getDefaults());
        copyRule(baseProperties.getDefaults(), merged.getDefaults());

        LinkedHashMap<String, CircuitBreakerConfigProperties.Rule> instances = new LinkedHashMap<>();
        instances.putAll(shared.getInstances());
        instances.putAll(baseProperties.getInstances());
        merged.setInstances(instances);
        return merged;
    }

    private void copyRule(CircuitBreakerConfigProperties.Rule source, CircuitBreakerConfigProperties.Rule target) {
        target.setFailureThreshold(source.getFailureThreshold());
        target.setMinimumCalls(source.getMinimumCalls());
        target.setHalfOpenMaxCalls(source.getHalfOpenMaxCalls());
        target.setOpenDuration(source.getOpenDuration());
    }
}
