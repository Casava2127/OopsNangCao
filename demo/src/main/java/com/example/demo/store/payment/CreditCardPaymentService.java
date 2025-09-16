package com.example.demo.store.payment;

public class CreditCardPaymentService implements PaymentService {
    @Override
    public boolean pay(double amount) {
        // Dummy implementation - in real life call payment gateway
        return amount > 0;
    }
}
