package com.commonservice.common.observability;

import com.commonservice.common.json.JsonUtils;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public final class KafkaLogLogger implements Logger {
    private final String serviceName;
    private final LogLevel threshold;
    private final String topic;
    private final Producer<String, String> producer;

    public KafkaLogLogger(String serviceName, LogLevel threshold, String bootstrapServers, String topic) {
        this.serviceName = serviceName;
        this.threshold = threshold;
        this.topic = topic;
        this.producer = createProducer(bootstrapServers);
    }

    @Override
    public void log(LogLevel level, String message) {
        log(level, message, Map.of());
    }

    @Override
    public void log(LogLevel level, String message, Map<String, Object> fields) {
        if (level.ordinal() < threshold.ordinal()) {
            return;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("service", serviceName);
        payload.put("level", level.name());
        payload.put("message", message);
        payload.putAll(fields);

        try {
            String json = JsonUtils.toJson(payload);
            producer.send(new ProducerRecord<>(topic, serviceName, json));
        } catch (Exception ignored) {
            // Intentionally swallow logging transport failures to avoid impacting request flow.
        }
    }

    private Producer<String, String> createProducer(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("acks", "1");
        properties.put("retries", "3");
        properties.put("linger.ms", "50");
        properties.put("batch.size", "16384");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }
}
