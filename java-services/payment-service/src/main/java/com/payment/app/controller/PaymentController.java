package com.payment.app.controller;

import com.payment.app.common.response.ApiResponse;
import com.payment.app.command.CommandBus;
import com.payment.app.command.CreatePaymentCommand;
import com.payment.app.command.GetPaymentCommand;
import com.payment.app.command.PaymentCommandMapper;
import com.payment.app.bean.payment.CreatePaymentRequest;
import com.payment.app.bean.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final CommandBus commandBus;
    private final PaymentCommandMapper paymentCommandMapper;

    public PaymentController(CommandBus commandBus, PaymentCommandMapper paymentCommandMapper) {
        this.commandBus = commandBus;
        this.paymentCommandMapper = paymentCommandMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestBody CreatePaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKeyHeader,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationIdHeader
    ) {
        CreatePaymentCommand command = paymentCommandMapper.toCreateCommand(request, idempotencyKeyHeader, correlationIdHeader);
        PaymentResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String paymentId) {
        GetPaymentCommand command = paymentCommandMapper.toGetCommand(paymentId);
        PaymentResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
