

# 🏬 Tổng quan dự án

## 🔹 Dự án này làm gì?

* Đây là **một ứng dụng Spring Boot (MVC + REST)** mô phỏng hệ thống **đặt hàng và thanh toán online**.
* Người dùng có thể:

    * Đăng ký / lưu thông tin tài khoản (`User`).
    * Xem danh sách sản phẩm (`Product`).
    * Tạo đơn hàng (`Order`) chứa nhiều sản phẩm (`OrderItem`).
    * Chọn phương thức thanh toán (`PaymentService`) như **PayPal, Credit Card, COD** hoặc **Legacy gateway**.
* Ứng dụng minh họa cách tích hợp **4 mẫu thiết kế (Design Patterns): Singleton, Factory, Adapter, Builder)** để tổ chức code rõ ràng, dễ mở rộng và bảo trì.

---

## 🔹 Các đối tượng chính

1. **User (Người dùng)**

    * Lưu thông tin khách hàng: `id, name, email`.
    * Quan hệ: **1 User → N Orders**.

2. **Product (Sản phẩm)**

    * Lưu thông tin sản phẩm: `id, name, price`.
    * Quan hệ: **1 Product → N OrderItems**.

3. **Order (Đơn hàng)**

    * Đại diện cho 1 giao dịch mua hàng: `id, orderNumber, user, items, totalAmount, status`.
    * Quan hệ:

        * **1 Order → 1 User**.
        * **1 Order → N OrderItems**.

4. **OrderItem (Chi tiết đơn hàng)**

    * Lưu thông tin sản phẩm trong đơn hàng: `id, product, quantity, price`.
    * Quan hệ:

        * **1 OrderItem → 1 Product**.
        * **1 Order → N OrderItems**.

5. **PaymentService (Thanh toán)**

    * Interface chung cho mọi phương thức thanh toán.
    * Các triển khai cụ thể:

        * `PayPalPaymentService`
        * `CreditCardPaymentService`
        * `CashOnDeliveryService`
        * `LegacyPaymentAdapter` (kết nối hệ thống thanh toán cũ).

6. **OrderBuilder (Builder Pattern)**

    * Giúp xây dựng `Order` phức tạp (nhiều items, discount, shippingAddress) một cách tuần tự, dễ mở rộng.

7. **PaymentFactory (Factory Pattern)**

    * Sinh ra `PaymentService` phù hợp với `PaymentType` (PAYPAL, CREDIT\_CARD, COD, LEGACY).

8. **UniqueIdGenerator (Singleton Pattern)**

    * Sinh ra mã đơn hàng duy nhất (`ORD-xxx`).

9. **LegacyPaymentAdapter (Adapter Pattern)**

    * Giúp hệ thống mới gọi được code thanh toán cũ (`LegacyPaymentGateway`) mà không cần thay đổi giao diện chung (`PaymentService`).

10. **DataLoader (Bootstrap)**

    * Tạo dữ liệu mẫu (`Users`, `Products`) khi ứng dụng khởi động để test nhanh.

---

## 🔹 Mối quan hệ giữa các đối tượng

* **User** tạo **Order**.
* **Order** chứa nhiều **OrderItem**.
* **OrderItem** tham chiếu tới **Product**.
* **OrderService**:

    1. Nhận dữ liệu từ **OrderController**.
    2. Dùng **OrderBuilder** để xây dựng Order.
    3. Gọi **UniqueIdGenerator** (Singleton) để sinh mã đơn hàng.
    4. Lưu Order vào DB qua **OrderRepository**.
    5. Gọi **PaymentFactory** để chọn PaymentService.
    6. Nếu PaymentType = LEGACY → dùng **LegacyPaymentAdapter** để kết nối hệ thống cũ.
    7. Cập nhật trạng thái Order dựa vào kết quả thanh toán.

---

## 🔹 Tóm tắt ngắn

* **Mục tiêu:** Xây dựng hệ thống đặt hàng + thanh toán có kiến trúc rõ ràng, áp dụng 4 mẫu thiết kế.
* **Đối tượng chính:** User, Product, Order, OrderItem, PaymentService.
* **Quan hệ:**

    * User → Order (1-N)
    * Order → OrderItem (1-N)
    * OrderItem → Product (N-1)
* **Design Patterns:**

    * Singleton → `UniqueIdGenerator`
    * Factory → `PaymentFactory`
    * Adapter → `LegacyPaymentAdapter`
    * Builder → `OrderBuilder`

---


## 1️⃣ `controller/` – REST API endpoints

| File                     | Chức năng                                                                                    | Kiểu thiết kế liên quan                        |
| ------------------------ | -------------------------------------------------------------------------------------------- | ---------------------------------------------- |
| `OrderController.java`   | Xử lý request tạo, lấy, xem chi tiết đơn hàng. Chuyển dữ liệu từ client sang `OrderService`. | Sử dụng `OrderBuilder` (Builder) khi tạo order |
| `PaymentController.java` | Xử lý request thanh toán, gọi `PaymentFactory` để lấy service tương ứng.                     | Factory (khi tạo `PaymentService`)             |

---

## 2️⃣ `dto/` – Data Transfer Object

| File                 | Chức năng                                                                  |
| -------------------- | -------------------------------------------------------------------------- |
| `OrderRequest.java`  | DTO chứa thông tin request tạo order: userId, danh sách item, paymentType. |
| `PaymentResult.java` | DTO trả kết quả thanh toán: thành công hay thất bại, message.              |
| `PaymentType.java`   | Enum liệt kê các loại thanh toán: PAYPAL, CREDIT\_CARD, COD, LEGACY.       |

---

## 3️⃣ `model/` – Entity ánh xạ database

| File             | Chức năng           | Quan hệ                                 |
| ---------------- | ------------------- | --------------------------------------- |
| `Order.java`     | Entity đơn hàng     | 1 Order → N OrderItem; 1 Order → 1 User |
| `OrderItem.java` | Entity mục đơn hàng | 1 OrderItem → 1 Product                 |
| `Product.java`   | Entity sản phẩm     | 1 Product → N OrderItem                 |
| `User.java`      | Entity người dùng   | 1 User → N Order                        |

**Ghi chú:** quan hệ giữa các entity được ánh xạ qua JPA.

---

## 4️⃣ `payment/` – Thanh toán

| File                            | Chức năng                                              | Kiểu thiết kế  |
| ------------------------------- | ------------------------------------------------------ | -------------- |
| `PaymentService.java`           | Interface chung cho tất cả service thanh toán          | —              |
| `PayPalPaymentService.java`     | Service thanh toán qua PayPal                          | —              |
| `CreditCardPaymentService.java` | Service thanh toán thẻ tín dụng                        | —              |
| `CashOnDeliveryService.java`    | Service thanh toán COD                                 | —              |
| `LegacyPaymentGateway.java`     | Legacy gateway cũ                                      | —              |
| `LegacyPaymentAdapter.java`     | Adapter: chuyển interface legacy sang `PaymentService` | Adapter (Minh) |
| `PaymentFactory.java`           | Tạo PaymentService dựa trên `PaymentType`              | Factory (Khoa) |

---

## 5️⃣ `repository/` – JPA repository

| File                     | Chức năng          |
| ------------------------ | ------------------ |
| `OrderRepository.java`   | CRUD cho `Order`   |
| `ProductRepository.java` | CRUD cho `Product` |
| `UserRepository.java`    | CRUD cho `User`    |

---

## 6️⃣ `service/` – Business logic

| File                | Chức năng                                                       | Kiểu thiết kế                                                 |
| ------------------- | --------------------------------------------------------------- | ------------------------------------------------------------- |
| `OrderService.java` | Logic tạo order: tính tổng tiền, gọi PaymentService, lưu vào DB | Builder (Trí) khi build order, Factory khi gọi PaymentService |

---

## 7️⃣ `builder/` – Builder pattern

| File                | Chức năng                             | Kiểu thiết kế |
| ------------------- | ------------------------------------- | ------------- |
| `OrderBuilder.java` | Tạo Order theo Fluent API, dễ mở rộng | Builder (Trí) |

---

## 8️⃣ `util/` – Utilities

| File                     | Chức năng        | Kiểu thiết kế    |
| ------------------------ | ---------------- | ---------------- |
| `UniqueIdGenerator.java` | Sinh ID duy nhất | Singleton (Quân) |

---

## 9️⃣ `bootstrap/` – Data loading

| File              | Chức năng                                                |
| ----------------- | -------------------------------------------------------- |
| `DataLoader.java` | Khởi tạo dữ liệu mẫu (users, products) khi ứng dụng chạy |

---

## 10️⃣ `DemoApplication.java`

* Lớp main khởi động Spring Boot application.

---

### 🔹 Tổng kết 4 kiểu thiết kế trong dự án

| Kiểu thiết kế    | File liên quan         | Chức năng                                              |
| ---------------- | ---------------------- | ------------------------------------------------------ |
| Singleton (Quân) | `UniqueIdGenerator`    | Sinh ID duy nhất cho order                             |
| Factory (Khoa)   | `PaymentFactory`       | Tạo PaymentService dựa trên PaymentType                |
| Adapter (Minh)   | `LegacyPaymentAdapter` | Cho phép sử dụng legacy gateway với interface hiện tại |
| Builder (Trí)    | `OrderBuilder`         | Xây dựng Order theo Fluent API, dễ thêm thông tin      |

---


