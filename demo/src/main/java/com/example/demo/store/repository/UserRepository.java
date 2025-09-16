package com.example.demo.store.repository;

import com.example.demo.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}