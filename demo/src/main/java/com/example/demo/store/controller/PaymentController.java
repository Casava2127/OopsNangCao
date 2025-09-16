package com.example.demo.store.controller;

import com.example.demo.store.dto.PaymentType;
import com.example.demo.store.dto.PaymentResult;
import com.example.demo.store.payment.PaymentFactory;
import com.example.demo.store.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // POST /api/payments/pay
    @PostMapping("/pay")
    public ResponseEntity<PaymentResult> pay(@RequestParam("type") PaymentType type,
                                             @RequestParam("amount") double amount) {
        PaymentService service = PaymentFactory.create(type);
        boolean ok = service.pay(amount);

        if (ok) {
            return ResponseEntity.ok(new PaymentResult(true, "Payment processed successfully"));
        } else {
            return ResponseEntity.ok(new PaymentResult(false, "Payment failed"));
        }
    }
}
