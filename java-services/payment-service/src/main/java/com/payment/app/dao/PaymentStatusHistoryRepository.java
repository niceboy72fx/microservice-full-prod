package com.payment.app.dao;

import com.payment.app.bean.payment.PaymentStatusHistory;

public interface PaymentStatusHistoryRepository {
    void save(PaymentStatusHistory history);
}
