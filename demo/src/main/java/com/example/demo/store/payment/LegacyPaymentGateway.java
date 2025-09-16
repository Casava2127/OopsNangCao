package com.example.demo.store.payment;

/**
 * Legacy gateway returns 1 for success, 0 for fail.
 */
public class LegacyPaymentGateway {
    public int makePayment(double amount) {
        // Simulate legacy behavior
        return amount > 0 ? 1 : 0;
    }
}
