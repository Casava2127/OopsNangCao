package com.example.demo.store.service;

import com.example.demo.store.builder.OrderBuilder;
import com.example.demo.store.dto.PaymentType;
import com.example.demo.store.model.Order;
import com.example.demo.store.model.OrderItem;
import com.example.demo.store.model.Product;
import com.example.demo.store.model.User;
import com.example.demo.store.payment.PaymentFactory;
import com.example.demo.store.payment.PaymentService;
import com.example.demo.store.repository.OrderRepository;
import com.example.demo.store.repository.ProductRepository;
import com.example.demo.store.util.UniqueIdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // Đánh dấu class này là một Spring Service (chứa business logic)
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // Constructor injection: Spring sẽ tự động inject repository vào
    public OrderService(ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Tạo order mới
    public Order createOrder(User user, List<OrderItem> items, PaymentType paymentType) {
        double total = 0; // tổng tiền đơn hàng
        List<OrderItem> processedItems = new ArrayList<>(); // danh sách item đã xử lý

        // ✅ B1: Xử lý từng OrderItem từ client gửi lên
        for (OrderItem it : items) {
            // Lấy product từ DB để đảm bảo thông tin chính xác
            Product product = productRepository.findById(it.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + it.getProduct().getId()));

            // Dùng Builder Pattern để tạo bản copy của OrderItem
            OrderItem copy = OrderItem.builder()
                    .product(product)              // gắn sản phẩm thực từ DB
                    .quantity(it.getQuantity())    // gắn số lượng
                    .price(product.getPrice())     // giá sản phẩm tại thời điểm order
                    .build();

            processedItems.add(copy); // thêm vào danh sách
            total += product.getPrice() * it.getQuantity(); // cộng vào tổng tiền
        }

        // ✅ B2: Sinh externalId duy nhất cho order
        // Ứng dụng Singleton Pattern qua UniqueIdGenerator
        String externalId = UniqueIdGenerator.getInstance().generate();

        // ✅ B3: Dùng Builder Pattern để build Order
        OrderBuilder builder = new OrderBuilder()
                .withExternalId(externalId)
                .withUser(user)   // gắn user trực tiếp
                .withTotal(total);

        // Thêm các item vào order
        for (OrderItem it : processedItems) {
            builder.addItem(it);
        }

        // Build ra đối tượng Order
        Order order = builder.build();
        order.setUser(user);          // gắn user
        order.setStatus("CREATED");   // trạng thái ban đầu

        // ✅ B4: Lưu Order lần 1 vào DB (trạng thái CREATED)
        order = orderRepository.save(order);

        // ✅ B5: Xử lý thanh toán
        // Dùng Factory Pattern để lấy service thanh toán phù hợp
        PaymentService paymentService = PaymentFactory.create(paymentType);
        boolean paid = paymentService.pay(total);

        // Cập nhật trạng thái order theo kết quả thanh toán
        order.setStatus(paid ? "PAID" : "FAILED");

        // ✅ B6: Save lại order vào DB với trạng thái final
        return orderRepository.save(order);
    }

    // Lấy tất cả order trong DB
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy 1 order theo id
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
