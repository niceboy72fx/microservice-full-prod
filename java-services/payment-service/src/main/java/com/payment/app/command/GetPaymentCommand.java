package com.payment.app.command;

import com.payment.app.bean.payment.PaymentResponse;

public class GetPaymentCommand implements Command<PaymentResponse> {

    private final String paymentId;

    public GetPaymentCommand(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
