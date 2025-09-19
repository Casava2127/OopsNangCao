package com.example.demo.store.builder;

import com.example.demo.store.model.Order;
import com.example.demo.store.model.OrderItem;
import com.example.demo.store.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 👉 Builder Pattern
 * - Hỗ trợ tạo Order theo "fluent API"
 * - Tách việc khởi tạo Order phức tạp thành nhiều bước nhỏ, rõ ràng
 */
public class OrderBuilder {
    private Order order; // đối tượng Order đang được xây dựng

    // Constructor
    public OrderBuilder() {
        order = new Order();                  // tạo 1 Order rỗng
        order.setStatus("CREATED");           // mặc định trạng thái ban đầu
        order.setItems(new ArrayList<>());    // khởi tạo danh sách rỗng cho items
    }

    // Gắn externalId cho Order
    public OrderBuilder withExternalId(String externalId) {
        order.setExternalId(externalId);
        return this; // trả về chính builder để chain nhiều method
    }

    // Gắn user cho Order
    public OrderBuilder withUser(User user) {
        order.setUser(user);
        return this;
    }

    // Thêm 1 item vào danh sách OrderItem
    public OrderBuilder addItem(OrderItem item) {
        order.getItems().add(item);
        return this;
    }

    // Gắn tổng tiền cho Order
    public OrderBuilder withTotal(double total) {
        order.setTotal(total);
        return this;
    }

    // Kết thúc: trả về đối tượng Order đã build xong
    public Order build() {
        return order;
    }
}
