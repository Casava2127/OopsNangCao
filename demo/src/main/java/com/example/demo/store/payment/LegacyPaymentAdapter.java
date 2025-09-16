package com.example.demo.store.payment;

/**
 * Minh - Adapter: chuyển interface LegacyPaymentGateway -> PaymentService
 */
public class LegacyPaymentAdapter implements PaymentService {

    private final LegacyPaymentGateway legacyGateway;

    public LegacyPaymentAdapter(LegacyPaymentGateway legacyGateway) {
        this.legacyGateway = legacyGateway;
    }

    @Override
    public boolean pay(double amount) {
        return legacyGateway.makePayment(amount) == 1;
    }
}
