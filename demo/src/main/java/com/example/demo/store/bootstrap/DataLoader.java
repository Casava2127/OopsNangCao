package com.example.demo.store.bootstrap;

import com.example.demo.store.model.Product;
import com.example.demo.store.model.User;
import com.example.demo.store.repository.ProductRepository;
import com.example.demo.store.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Insert sample users/products for demo
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DataLoader(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            Product p1 = new Product(); p1.setName("Keyboard"); p1.setPrice(25.0);
            Product p2 = new Product(); p2.setName("Mouse"); p2.setPrice(15.0);
            Product p3 = new Product(); p3.setName("Monitor"); p3.setPrice(150.0);
            productRepository.save(p1); productRepository.save(p2); productRepository.save(p3);
        }

        if (userRepository.count() == 0) {
            User u = new User(); u.setUsername("alice"); u.setEmail("alice@example.com");
            userRepository.save(u);
        }
    }
}
