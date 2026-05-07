package com.authentication.app.authen;

import com.authentication.app.bean.ForgotPasswordResponse;
import com.authentication.app.kafka.event.PasswordResetRequestedEvent;
import com.authentication.app.kafka.producer.AuthEventProducer;
import com.authentication.app.dao.record.AuthAccountRecord;
import com.authentication.app.dao.AuthAccountSqlRepository;
import java.time.Duration;
import java.util.UUID;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordService {

    private final AuthAccountSqlRepository authAccountSqlRepository;
    private final StringRedisTemplate redisTemplate;
    private final AuthEventProducer authEventProducer;

    @Value("${app.auth.topic.auth-events:auth-events}")
    private String authEventsTopic;

    @Value("${app.auth.reset-token.ttl-minutes:15}")
    private long resetTokenTtlMinutes;

    public ForgotPasswordService(
            AuthAccountSqlRepository authAccountSqlRepository,
            StringRedisTemplate redisTemplate,
            AuthEventProducer authEventProducer
    ) {
        this.authAccountSqlRepository = authAccountSqlRepository;
        this.redisTemplate = redisTemplate;
        this.authEventProducer = authEventProducer;
    }

    public ForgotPasswordResponse requestReset(String email) {
        String normalizedEmail = normalizeEmail(email);
        String correlationId = UUID.randomUUID().toString();

        Optional<AuthAccountRecord> accountOptional = authAccountSqlRepository.findByEmail(normalizedEmail);
        if (accountOptional.isEmpty()) {
            return new ForgotPasswordResponse("If account exists, reset link will be sent", correlationId);
        }
        AuthAccountRecord account = accountOptional.get();

        String resetToken = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(
                buildRedisKey(normalizedEmail),
                resetToken,
                Duration.ofMinutes(resetTokenTtlMinutes)
        );

        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent(
                UUID.randomUUID().toString(),
                correlationId,
                "PASSWORD_RESET_REQUESTED",
                account.id(),
                normalizedEmail,
                resetToken
        );
        authEventProducer.publishPasswordResetRequested(authEventsTopic, event);

        return new ForgotPasswordResponse("If account exists, reset link will be sent", correlationId);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String buildRedisKey(String email) {
        return "auth:reset-token:" + email;
    }
}
