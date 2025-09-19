package com.example.demo.store.controller;

import com.example.demo.store.dto.PaymentResult;
import com.example.demo.store.dto.PaymentType;
import com.example.demo.store.payment.PaymentFactory;
import com.example.demo.store.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    /**
     * POST /api/payments/pay?type=PAYPAL&amount=100.0
     * @param type loại thanh toán (PAYPAL, CREDIT_CARD, COD, LEGACY)
     * @param amount số tiền thanh toán
     */
    @PostMapping("/pay")
    public ResponseEntity<PaymentResult> pay(
            @RequestParam("type") PaymentType type,
            @RequestParam("amount") double amount) {

        // Factory tạo PaymentService tương ứng
        PaymentService service = PaymentFactory.create(type);

        // Gọi phương thức pay thực hiện thanh toán
        boolean ok = service.pay(amount);

        // Trả về kết quả
        PaymentResult result = ok
                ? new PaymentResult(true, "Payment processed successfully")
                : new PaymentResult(false, "Payment failed");

        return ResponseEntity.ok(result);
    }
}
