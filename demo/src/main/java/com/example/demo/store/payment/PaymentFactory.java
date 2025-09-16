package com.example.demo.store.payment;

import com.example.demo.store.dto.PaymentType;

/**
 * Khoa - Factory: tạo PaymentService theo PaymentType
 */
public class PaymentFactory {

    public static PaymentService create(PaymentType type) {
        switch (type) {
            case PAYPAL:
                return new PayPalPaymentService();
            case CREDIT_CARD:
                return new CreditCardPaymentService();
            case COD:
                return new CashOnDeliveryService();
            case LEGACY:
                return new LegacyPaymentAdapter(new LegacyPaymentGateway());
            default:
                throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }
}
