package com.example.demo.store.service;

import com.example.demo.store.builder.OrderBuilder;
import com.example.demo.store.dto.PaymentType;
import com.example.demo.store.model.Order;
import com.example.demo.store.model.OrderItem;
import com.example.demo.store.model.Product;
import com.example.demo.store.payment.PaymentFactory;
import com.example.demo.store.payment.PaymentService;
import com.example.demo.store.repository.OrderRepository;
import com.example.demo.store.repository.ProductRepository;
import com.example.demo.store.util.UniqueIdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Long userId, List<OrderItem> items, PaymentType paymentType) {
        double total = 0;
        List<OrderItem> processedItems = new ArrayList<>();

        // Xử lý từng item, lấy giá từ DB (nếu có)
        for (OrderItem it : items) {
            Optional<Product> pOpt = productRepository.findById(it.getProductId());
            double price = pOpt.map(Product::getPrice).orElse(it.getPrice());

            OrderItem copy = new OrderItem();
            copy.setProductId(it.getProductId());
            copy.setQuantity(it.getQuantity());
            copy.setPrice(price);

            processedItems.add(copy);
            total += price * it.getQuantity();
        }

        // Tạo externalId cho order
        String externalId = UniqueIdGenerator.getInstance().generate();

        // Dùng builder pattern để build Order
        OrderBuilder builder = new OrderBuilder()
                .withExternalId(externalId)
                .withUserId(userId)
                .withTotal(total);

        for (OrderItem it : processedItems) {
            builder.addItem(it);
        }

        Order order = builder.build();
        order.setStatus("CREATED");

        // Save order lần 1 để có ID
        order = orderRepository.save(order);

        // Gọi service payment
        PaymentService paymentService = PaymentFactory.create(paymentType);
        boolean paid = paymentService.pay(total);

        order.setStatus(paid ? "PAID" : "FAILED");

        // Save lại sau khi thanh toán
        order = orderRepository.save(order);

        return order;
    }

    // Lấy tất cả order
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy order theo id
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
