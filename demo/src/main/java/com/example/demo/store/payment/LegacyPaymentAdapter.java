package com.example.demo.store.payment;

/**
 *  Adapter Pattern
 * - Mục tiêu: chuyển đổi interface "LegacyPaymentGateway" cũ
 *   thành "PaymentService" mà hệ thống mới đang sử dụng.
 */
public class LegacyPaymentAdapter implements PaymentService {

    // Giữ tham chiếu đến hệ thống cũ (LegacyPaymentGateway)
    private final LegacyPaymentGateway legacyGateway;

    // Constructor: nhận vào một instance của hệ thống cũ
    // để Adapter có thể "bọc" (wrap) lại.
    public LegacyPaymentAdapter(LegacyPaymentGateway legacyGateway) {
        this.legacyGateway = legacyGateway;
    }

    // Triển khai phương thức "pay" của interface mới PaymentService.
    // Lưu ý: LegacyPaymentGateway dùng hàm makePayment(double) trả về int
    //        -> 1 = thành công, 0 = thất bại.
    // Trong khi đó, PaymentService định nghĩa phương thức pay(double) trả về boolean.
    @Override
    public boolean pay(double amount) {
        // Adapter sẽ gọi sang hàm cũ và chuyển đổi kết quả int -> boolean.
        // Nếu legacyGateway.makePayment(amount) == 1 thì trả về true,
        // ngược lại trả về false.
        return legacyGateway.makePayment(amount) == 1;
    }
}

/**
 * Giải thích:
 * - Trước đây: hệ thống thanh toán cũ có lớp LegacyPaymentGateway với hàm makePayment(double)
 *   trả về int (1 = thành công, 0 = thất bại).
 *
 * - Bây giờ: hệ thống mới chuẩn hóa tất cả thanh toán thông qua interface PaymentService,
 *   với hàm pay(double) trả về boolean (true/false).
 *
 * - Adapter (LegacyPaymentAdapter) đóng vai trò "cầu nối":
 *   + Bên ngoài OrderService chỉ làm việc với PaymentService (code sạch, thống nhất).
 *   + Bên trong Adapter sẽ chuyển đổi lời gọi từ pay() -> makePayment() của hệ thống cũ.
 *
 * Lợi ích: tích hợp hệ thống cũ mà không phải sửa đổi code của nó,
 *   đồng thời giữ cho hệ thống mới thống nhất theo interface PaymentService.
 */
