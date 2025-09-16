package com.example.demo.store.repository;

import com.example.demo.store.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy tất cả order của 1 user
    List<Order> findByUserId(Long userId);

    // Lấy order theo trạng thái (CREATED, PAID, FAILED)
    List<Order> findByStatus(String status);
}
