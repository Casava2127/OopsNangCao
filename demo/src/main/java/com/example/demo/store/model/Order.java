package com.example.demo.store.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalId;
    private double total;
    private String status; // CREATED, PAID, FAILED

    // Nhiều Order thuộc về 1 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 1 Order có nhiều OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}
