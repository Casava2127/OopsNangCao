package com.example.demo.store.payment;

import com.example.demo.store.dto.PaymentType;

/**
 *  Factory Pattern
 * - Mục tiêu: Tách logic khởi tạo các lớp PaymentService ra khỏi business logic.
 * - Thay vì gọi trực tiếp new PayPalPaymentService(), OrderService chỉ cần gọi PaymentFactory.create(type).
 * - Giúp code dễ mở rộng, dễ bảo trì.
 */
public class PaymentFactory {

    // Phương thức static create() sẽ nhận vào PaymentType
    // và trả về một instance của lớp PaymentService tương ứng.
    public static PaymentService create(PaymentType type) {
        switch (type) {
            case PAYPAL:
                // Nếu khách hàng chọn thanh toán PayPal -> trả về service PayPal
                return new PayPalPaymentService();
            case CREDIT_CARD:
                // Nếu chọn Credit Card -> trả về service CreditCard
                return new CreditCardPaymentService();
            case COD:
                // Nếu chọn Cash On Delivery -> trả về service COD
                return new CashOnDeliveryService();
            case LEGACY:
                // Nếu chọn hệ thống cũ -> dùng Adapter để chuyển đổi LegacyPaymentGateway sang PaymentService
                return new LegacyPaymentAdapter(new LegacyPaymentGateway());
            default:
                // Nếu không có kiểu nào phù hợp -> ném ra exception
                throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
    }
}
//
/// **
// *  Giải thích:
// * - Thay vì OrderService phải biết chi tiết cách tạo PayPalPaymentService, CreditCardPaymentService...
// *   → chỉ cần gọi PaymentFactory.create(paymentType).
// *
// * - Factory Pattern giúp:
// *   + Đóng gói (encapsulate) logic khởi tạo.
// *   + Giảm sự phụ thuộc giữa OrderService và các class con cụ thể.
// *   + Dễ mở rộng: nếu sau này thêm GooglePayPaymentService,
// *     chỉ cần bổ sung case vào Factory mà không phải sửa logic*
