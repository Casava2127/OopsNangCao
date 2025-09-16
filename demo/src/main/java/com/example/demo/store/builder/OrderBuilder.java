package com.example.demo.store.builder;

import com.example.demo.store.model.Order;
import com.example.demo.store.model.OrderItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Trí - Builder: hỗ trợ tạo Order theo fluent API
 */
public class OrderBuilder {
    private Order order;

    public OrderBuilder() {
        order = new Order();
        order.setStatus("CREATED");
        order.setItems(new ArrayList<>());
    }

    public OrderBuilder withExternalId(String externalId) {
        order.setExternalId(externalId);
        return this;
    }

    public OrderBuilder withUserId(Long userId) {
        order.setUserId(userId);
        return this;
    }

    public OrderBuilder addItem(OrderItem item) {
        order.getItems().add(item);
        return this;
    }

    public OrderBuilder withTotal(double total) {
        order.setTotal(total);
        return this;
    }

    public Order build() {
        return order;
    }
}
