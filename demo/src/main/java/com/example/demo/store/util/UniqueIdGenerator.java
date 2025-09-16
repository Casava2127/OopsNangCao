package com.example.demo.store.util;

import java.util.UUID;

/**
 * Quân - Singleton
 */
public class UniqueIdGenerator {
    private static UniqueIdGenerator instance;

    private UniqueIdGenerator() { }

    public static synchronized UniqueIdGenerator getInstance() {
        if (instance == null) {
            instance = new UniqueIdGenerator();
        }
        return instance;
    }

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
