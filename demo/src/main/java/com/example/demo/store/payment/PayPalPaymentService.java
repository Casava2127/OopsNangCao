package com.example.demo.store.payment;

public class PayPalPaymentService implements PaymentService {
    @Override
    public boolean pay(double amount) {
        // Dummy PayPal behavior
        return amount > 0;
    }
}
