package com.example.demo.store.controller;
// Khai báo package chứa class này (theo cấu trúc thư mục của dự án)

import com.example.demo.store.dto.OrderRequest;      // Import DTO chứa dữ liệu request từ client
import com.example.demo.store.model.Order;          // Import entity Order
import com.example.demo.store.model.OrderItem;      // Import entity OrderItem
import com.example.demo.store.model.Product;        // Import entity Product
import com.example.demo.store.model.User;           // Import entity User
import com.example.demo.store.repository.ProductRepository; // Import repository để thao tác với Product trong DB
import com.example.demo.store.repository.UserRepository;    // Import repository để thao tác với User trong DB
import com.example.demo.store.service.OrderService;         // Import service xử lý logic liên quan đến Order
import org.springframework.http.ResponseEntity;             // ResponseEntity giúp trả response HTTP chuẩn
import org.springframework.web.bind.annotation.*;           // Import các annotation cho REST API

import java.util.ArrayList;  // Dùng để khởi tạo danh sách rỗng
import java.util.List;       // Interface List để quản lý danh sách items

// Đánh dấu đây là một REST controller (RESTful API)
@RestController
// Tất cả endpoint trong class này sẽ bắt đầu với "/api/orders"
@RequestMapping("/api/orders")
public class OrderController {

    // Khai báo các dependency (sẽ được Spring inject vào qua constructor)
    private final OrderService orderService;          // Xử lý logic order
    private final UserRepository userRepository;      // CRUD với User
    private final ProductRepository productRepository;// CRUD với Product

    // Constructor để Spring Boot inject các dependency vào controller
    public OrderController(OrderService orderService,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Endpoint POST: tạo order mới
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest request) {
        // Lấy thông tin user từ DB theo userId trong request
        // Nếu không tìm thấy thì ném lỗi RuntimeException
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        // Danh sách items của order
        List<OrderItem> items = new ArrayList<>();

        // Nếu request có gửi danh sách items
        if (request.getItems() != null) {
            // Duyệt từng item trong request
            for (OrderRequest.Item i : request.getItems()) {
                // Lấy product từ DB theo productId
                Product product = productRepository.findById(i.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + i.getProductId()));

                // Dùng Builder để tạo OrderItem
                OrderItem oi = OrderItem.builder()
                        .product(product)              // Gắn sản phẩm
                        .quantity(i.getQuantity())     // Gắn số lượng
                        .price(product.getPrice())     // Lưu giá sản phẩm tại thời điểm order
                        .build();
                // Thêm vào danh sách items
                items.add(oi);
            }
        }

        // Gọi OrderService để tạo Order hoàn chỉnh (bao gồm thanh toán)
        Order order = orderService.createOrder(user, items, request.getPaymentType());

        // Trả response 200 OK với order vừa tạo
        return ResponseEntity.ok(order);
    }

    // Endpoint GET: lấy tất cả order
    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        // Gọi service lấy danh sách order và trả về
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Endpoint GET: lấy order theo id
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        // Gọi service tìm order theo id và trả về
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
