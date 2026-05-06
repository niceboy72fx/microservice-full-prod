package com.commonservice.common.circuitbreaker;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "common.circuit-breaker")
public class CircuitBreakerConfigProperties {

    private final Rule defaults = new Rule();
    private Map<String, Rule> instances = new HashMap<>();

    public Rule getDefaults() {
        return defaults;
    }

    public Map<String, Rule> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, Rule> instances) {
        this.instances = instances;
    }

    public static class Rule {
        private int failureThreshold = 5;
        private int minimumCalls = 10;
        private int halfOpenMaxCalls = 3;
        private Duration openDuration = Duration.ofSeconds(30);

        public int getFailureThreshold() {
            return failureThreshold;
        }

        public void setFailureThreshold(int failureThreshold) {
            this.failureThreshold = failureThreshold;
        }

        public int getMinimumCalls() {
            return minimumCalls;
        }

        public void setMinimumCalls(int minimumCalls) {
            this.minimumCalls = minimumCalls;
        }

        public int getHalfOpenMaxCalls() {
            return halfOpenMaxCalls;
        }

        public void setHalfOpenMaxCalls(int halfOpenMaxCalls) {
            this.halfOpenMaxCalls = halfOpenMaxCalls;
        }

        public Duration getOpenDuration() {
            return openDuration;
        }

        public void setOpenDuration(Duration openDuration) {
            this.openDuration = openDuration;
        }
    }
}
