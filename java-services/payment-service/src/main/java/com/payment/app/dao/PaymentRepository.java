package com.payment.app.dao;

import com.payment.app.bean.payment.Payment;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    Optional<Payment> findByUserIdAndIdempotencyKey(String userId, String idempotencyKey);
}
