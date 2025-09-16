package com.example.demo.store.payment;

public class CashOnDeliveryService implements PaymentService {
    @Override
    public boolean pay(double amount) {
        // COD: payment not processed now; treat as success for order creation
        return true;
    }
}
