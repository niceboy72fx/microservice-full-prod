package com.payment.app.service;

import com.payment.app.common.exception.BusinessException;
import com.payment.app.bean.payment.Payment;
import com.payment.app.bean.payment.PaymentResponse;
import com.payment.app.dao.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public GetPaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentResponse execute(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("PAYMENT_NOT_FOUND", "Payment not found: " + paymentId));
        return paymentMapper.toResponse(payment);
    }
}
