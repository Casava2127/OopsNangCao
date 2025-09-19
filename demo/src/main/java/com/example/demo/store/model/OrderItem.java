package com.example.demo.store.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private double price; // giá tại thời điểm order

    // Nhiều OrderItem thuộc về 1 Order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Mỗi OrderItem đại diện cho 1 Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
