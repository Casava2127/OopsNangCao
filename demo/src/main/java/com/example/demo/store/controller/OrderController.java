package com.example.demo.store.controller;

import com.example.demo.store.dto.OrderRequest;
import com.example.demo.store.model.Order;
import com.example.demo.store.model.OrderItem;
import com.example.demo.store.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Tạo order mới
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest request) {
        List<OrderItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            for (OrderRequest.Item i : request.getItems()) {
                OrderItem oi = new OrderItem();
                oi.setProductId(i.getProductId());
                oi.setQuantity(i.getQuantity());
                items.add(oi);
            }
        }
        Order order = orderService.createOrder(request.getUserId(), items, request.getPaymentType());
        return ResponseEntity.ok(order);
    }

    // Lấy tất cả order
    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Lấy order theo id
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
