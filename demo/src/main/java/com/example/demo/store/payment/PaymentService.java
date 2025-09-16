package com.example.demo.store.payment;

public interface PaymentService {
    /**
     * Process a payment for the given amount.
     * @param amount amount in currency units
     * @return true if successful
     */
    boolean pay(double amount);
}
