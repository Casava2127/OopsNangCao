package com.example.demo.store.util;

import java.util.UUID;

/**
 * Quân - Singleton Pattern
 * - Đảm bảo chỉ có duy nhất 1 instance của UniqueIdGenerator trong toàn bộ ứng dụng.
 * - Dùng để sinh mã ID duy nhất (UUID) cho Order.
 */
public class UniqueIdGenerator {
    // Biến static lưu trữ instance duy nhất
    private static UniqueIdGenerator instance; // duy nhất 1 instance

    // Constructor private -> ngăn không cho tạo đối tượng từ bên ngoài bằng "new"
    private UniqueIdGenerator() { }

    // Phương thức public static để truy cập instance duy nhất
    public static synchronized UniqueIdGenerator getInstance() {
        // Kiểm tra nếu instance chưa tồn tại -> tạo mới
        if (instance == null) {
            instance = new UniqueIdGenerator(); // chỉ tạo khi cần (lazy initialization)
        }
        // Trả về cùng 1 instance cho mọi lần gọi
        return instance;
    }

    // Sinh ra một UUID ngẫu nhiên -> dùng làm externalId cho Order
    public String generate() {
        return UUID.randomUUID().toString();
    }
}

/**
 *  Giải thích:
 * - Lý do cần Singleton:
 *   + Mỗi khi tạo order, hệ thống cần một externalId duy nhất.
 *   + Thay vì tạo nhiều generator khác nhau, ta dùng chung một "UniqueIdGenerator".
 *
 * - Cách triển khai:
 *   + instance: biến static, giữ duy nhất 1 đối tượng.
 *   + private constructor: ngăn code bên ngoài tạo thêm đối tượng.
 *   + getInstance(): đảm bảo chỉ trả về cùng một instance.
 *
 * - synchronized: đảm bảo an toàn trong môi trường đa luồng (multi-threaded).
 *
 *  Lợi ích trong dự án:
 * - Giảm tốn bộ nhớ (chỉ duy nhất 1 instance).
 * - Đảm bảo tính nhất quán khi sinh ID (không bị trùng lặp logic).
 */
