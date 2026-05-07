package com.payment.app.service;

import com.payment.app.common.exception.BusinessException;
import com.payment.app.constant.PaymentEventConstants;
import com.payment.app.utility.IdGeneratorComponent;
import com.payment.app.bean.payment.Payment;
import com.payment.app.bean.payment.CreatePaymentRequest;
import com.payment.app.bean.payment.PaymentProviderType;
import com.payment.app.bean.payment.PaymentResponse;
import com.payment.app.bean.payment.PaymentStatus;
import com.payment.app.bean.payment.PaymentStatusHistory;
import com.payment.app.dao.PaymentRepository;
import com.payment.app.dao.PaymentStatusHistoryRepository;
import com.payment.app.service.PaymentProvider;
import com.payment.app.service.PaymentProviderRequest;
import com.payment.app.service.PaymentProviderResult;
import com.payment.app.service.PaymentProviderRouter;
import com.payment.app.validator.CreatePaymentValidator;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;
    private final PaymentProviderRouter providerRouter;
    private final PaymentMapper paymentMapper;
    private final IdGeneratorComponent idGeneratorComponent;
    private final Clock clock;
    private final CreatePaymentValidator createPaymentValidator;

    public CreatePaymentService(
            PaymentRepository paymentRepository,
            PaymentStatusHistoryRepository historyRepository,
            PaymentProviderRouter providerRouter,
            PaymentMapper paymentMapper,
            IdGeneratorComponent idGeneratorComponent,
            Clock clock,
            CreatePaymentValidator createPaymentValidator
    ) {
        this.paymentRepository = paymentRepository;
        this.historyRepository = historyRepository;
        this.providerRouter = providerRouter;
        this.paymentMapper = paymentMapper;
        this.idGeneratorComponent = idGeneratorComponent;
        this.clock = clock;
        this.createPaymentValidator = createPaymentValidator;
    }

    @Transactional
    public PaymentResponse execute(CreatePaymentRequest request, String idempotencyKeyHeader, String correlationIdHeader) {
        createPaymentValidator.validate(request);

        String normalizedUserId = request.getUserId().trim();
        String idempotencyKey = resolveIdempotencyKey(request, idempotencyKeyHeader);
        String correlationId = resolveCorrelationId(correlationIdHeader);

        Optional<Payment> existing = paymentRepository.findByUserIdAndIdempotencyKey(normalizedUserId, idempotencyKey);
        if (existing.isPresent()) {
            return paymentMapper.toResponse(existing.get());
        }

        PaymentProviderType providerType = providerRouter.determineProviderType(request.getMethod());
        LocalDateTime now = LocalDateTime.now(clock);
        Payment payment = Payment.newCreated(
                idGeneratorComponent.generate(),
                normalizedUserId,
                request.getType(),
                request.getAmount(),
                normalizeCurrency(request.getCurrency()),
                request.getMethod(),
                providerType,
                idempotencyKey,
                correlationId,
                now
        );

        try {
            paymentRepository.save(payment);
        } catch (DuplicateKeyException exception) {
            Optional<Payment> raceWinner = paymentRepository.findByUserIdAndIdempotencyKey(normalizedUserId, idempotencyKey);
            if (raceWinner.isPresent()) {
                return paymentMapper.toResponse(raceWinner.get());
            }
            throw exception;
        }
        saveHistory(payment.getId(), null, PaymentStatus.CREATED, PaymentEventConstants.PAYMENT_CREATED, now);
        // TODO Outbox placeholder: publish PAYMENT_CREATED event.

        PaymentStatus from = payment.getStatus();
        payment.transitionToProcessing(LocalDateTime.now(clock));
        paymentRepository.save(payment);
        saveHistory(payment.getId(), from, PaymentStatus.PROCESSING, PaymentEventConstants.PAYMENT_PROCESSING, LocalDateTime.now(clock));
        // TODO Outbox placeholder: publish PAYMENT_PROCESSING event.

        PaymentProvider provider = providerRouter.resolve(payment.getMethod(), payment.getProvider());
        PaymentProviderResult providerResult = provider.process(new PaymentProviderRequest(
                payment.getId(),
                payment.getUserId(),
                payment.getType(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMethod(),
                payment.getCorrelationId()
        ));

        applyProviderResult(payment, providerResult);
        paymentRepository.save(payment);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            // TODO Outbox placeholder: publish PAYMENT_SUCCESS event.
        } else if (payment.getStatus() == PaymentStatus.FAILED) {
            // TODO Outbox placeholder: publish PAYMENT_FAILED event.
        } else if (payment.getStatus() == PaymentStatus.UNKNOWN) {
            // TODO Outbox placeholder: publish PAYMENT_UNKNOWN event.
        }

        return paymentMapper.toResponse(payment);
    }

    private void applyProviderResult(Payment payment, PaymentProviderResult providerResult) {
        PaymentStatus from = payment.getStatus();
        LocalDateTime now = LocalDateTime.now(clock);

        if (providerResult.status() == PaymentStatus.SUCCESS) {
            payment.transitionToSuccess(providerResult.providerTransactionId(), now);
            saveHistory(payment.getId(), from, PaymentStatus.SUCCESS, PaymentEventConstants.PAYMENT_SUCCESS, now);
            return;
        }

        if (providerResult.status() == PaymentStatus.FAILED) {
            payment.transitionToFailed(providerResult.failureReason(), providerResult.providerTransactionId(), now);
            saveHistory(payment.getId(), from, PaymentStatus.FAILED, providerResult.failureReason(), now);
            return;
        }

        if (providerResult.status() == PaymentStatus.UNKNOWN) {
            payment.transitionToUnknown(providerResult.failureReason(), providerResult.providerTransactionId(), now);
            saveHistory(payment.getId(), from, PaymentStatus.UNKNOWN, providerResult.failureReason(), now);
            return;
        }

        throw new BusinessException("UNSUPPORTED_PROVIDER_STATUS", "Provider status not supported: " + providerResult.status());
    }

    private void saveHistory(String paymentId, PaymentStatus from, PaymentStatus to, String reason, LocalDateTime at) {
        historyRepository.save(new PaymentStatusHistory(
                idGeneratorComponent.generate(),
                paymentId,
                from,
                to,
                reason,
                at
        ));
    }

    private String resolveIdempotencyKey(CreatePaymentRequest request, String idempotencyKeyHeader) {
        String bodyKey = request.getIdempotencyKey();
        String key = (bodyKey != null && !bodyKey.trim().isEmpty()) ? bodyKey.trim() :
                (idempotencyKeyHeader == null ? "" : idempotencyKeyHeader.trim());
        if (key.isEmpty()) {
            throw new BusinessException("MISSING_IDEMPOTENCY_KEY", "idempotencyKey is required in body or header");
        }
        return key;
    }

    private String resolveCorrelationId(String header) {
        if (header != null && !header.trim().isEmpty()) {
            return header.trim();
        }
        return UUID.randomUUID().toString();
    }

    private String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase(Locale.ROOT);
    }
}
