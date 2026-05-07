package com.payment.app.validator;

import com.payment.app.bean.payment.CreatePaymentRequest;
import com.payment.app.common.exception.BusinessException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentValidator {

    public void validate(CreatePaymentRequest request) {
        if (request == null) {
            throw new BusinessException("INVALID_REQUEST", "Request is required");
        }
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new BusinessException("INVALID_USER_ID", "userId is required");
        }
        if (request.getType() == null) {
            throw new BusinessException("INVALID_TYPE", "type is required");
        }
        if (request.getMethod() == null) {
            throw new BusinessException("INVALID_METHOD", "method is required");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "amount must be greater than zero");
        }
        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            throw new BusinessException("INVALID_CURRENCY", "currency is required");
        }
    }
}
