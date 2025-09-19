

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



# 🔑 Singleton trong dự án

## 1. Triển khai ở đâu?

Trong cấu trúc dự án, Singleton được áp dụng ở 2 nơi:

1. **`util/UniqueIdGenerator.java`**

    * Tạo **mã đơn hàng duy nhất**.
    * Không phụ thuộc Spring container, mà được quản lý thủ công bằng **Singleton Pattern**.
    * Triển khai theo **thread-safe Singleton (double-checked locking)** để nhiều luồng vẫn gọi an toàn.

   ```java
   public class UniqueIdGenerator {
       private static volatile UniqueIdGenerator instance;
       private AtomicLong counter = new AtomicLong(0);

       private UniqueIdGenerator() {}

       public static UniqueIdGenerator getInstance() {
           if (instance == null) {
               synchronized (UniqueIdGenerator.class) {
                   if (instance == null) {
                       instance = new UniqueIdGenerator();
                   }
               }
           }
           return instance;
       }

       public String nextId(String prefix) {
           return prefix + "-" + counter.incrementAndGet();
       }
   }
   ```

   📌 **Ứng dụng trong dự án:**

    * Khi tạo đơn hàng trong `OrderService`, gọi:

      ```java
      order.setOrderNumber(UniqueIdGenerator.getInstance().nextId("ORD"));
      ```
    * Đảm bảo mỗi đơn hàng có **orderNumber duy nhất** (ORD-1, ORD-2…).

---

2. **`payment/CashOnDeliveryService.java`**

    * Cách triển khai **Singleton Service** cho phương thức thanh toán COD.
    * Vì COD rất đơn giản (chỉ cần xác nhận “Thanh toán khi nhận hàng”) → không cần nhiều instance.

   ```java
   public class CashOnDeliveryService implements PaymentService {
       private static final CashOnDeliveryService instance = new CashOnDeliveryService();

       private CashOnDeliveryService() {}

       public static CashOnDeliveryService getInstance() {
           return instance;
       }

       @Override
       public boolean processPayment(Order order) {
           System.out.println("COD Payment accepted for order: " + order.getOrderNumber());
           return true;
       }
   }
   ```

   📌 **Ứng dụng trong dự án:**

    * Khi user chọn `paymentMethod = "COD"`,
      `PaymentFactory` sẽ trả về `CashOnDeliveryService.getInstance()`.
    * Nhờ Singleton, toàn hệ thống chỉ dùng **1 instance COD** → tiết kiệm bộ nhớ.

---

## 2. Ý nghĩa khi dùng Singleton ở đây

* **Đảm bảo duy nhất**:

    * `UniqueIdGenerator` → không bị trùng ID đơn hàng.
    * `CashOnDeliveryService` → chỉ cần một instance.

* **Tiết kiệm tài nguyên**:

    * COD không cần tạo mới nhiều lần, vì logic đơn giản.

* **Thread-safe**:

    * Với `UniqueIdGenerator`, dùng `volatile` + `synchronized` → an toàn trong môi trường nhiều request cùng tạo đơn hàng.

* **Tách biệt Spring Bean**:

    * Mặc dù Spring bean mặc định là Singleton, nhưng ví dụ này cho thấy **cách triển khai thủ công** Singleton cho những tiện ích/logic không phụ thuộc Spring.

---

👉 Tóm lại:

* **UniqueIdGenerator** (Singleton thủ công) → tạo order number duy nhất.
* **CashOnDeliveryService** (Singleton service) → tiết kiệm tài nguyên khi thanh toán COD.
* Singleton giúp dự án **kiểm soát tài nguyên + đảm bảo tính nhất quán**.


---

# 🏭 Factory Pattern trong dự án

## 1️⃣ Vị trí của Factory

* File:

  ```
  src/main/java/com/example/demo/store/payment/PaymentFactory.java
  ```
* Thuộc **package `payment/`**, nơi tập trung xử lý logic liên quan đến các phương thức thanh toán.

---

## 2️⃣ Vai trò của `PaymentFactory`

* `PaymentFactory` chính là nơi **tạo ra đối tượng PaymentService phù hợp** dựa trên loại thanh toán mà người dùng chọn (`PaymentType`).
* Nó **ẩn đi chi tiết khởi tạo** (`new PayPalPaymentService()`, `new CreditCardPaymentService()`, v.v.) khỏi các lớp khác như `OrderService` hoặc `PaymentController`.

---

## 3️⃣ Cách hoạt động trong luồng xử lý

1. **Người dùng** gọi API đặt hàng và chọn phương thức thanh toán (`PAYPAL`, `CREDIT_CARD`, `COD`, `LEGACY`).
2. **Controller** nhận request → gọi `OrderService`.
3. **OrderService** gọi `PaymentFactory.create(paymentType)` để lấy ra đúng `PaymentService`.
4. `PaymentService` được trả về (PayPal, Credit Card, COD, Legacy Adapter) và thực hiện `processPayment()`.

→ Nhờ Factory, `OrderService` không cần biết cụ thể đối tượng nào được tạo, chỉ cần làm việc với interface `PaymentService`.

---

## 4️⃣ Lợi ích khi dùng Factory trong dự án

* **Tách biệt logic khởi tạo**: Các class khác chỉ gọi Factory, không phải `new` trực tiếp.
* **Dễ mở rộng**: Thêm một phương thức thanh toán mới (VD: MomoPaymentService) → chỉ cần sửa trong Factory.
* **Kết hợp với các pattern khác**:

    * `Singleton` (cho `CashOnDeliveryService`)
    * `Adapter` (cho `LegacyPaymentAdapter`)
    * `Builder` (cho PayPal / CreditCard khi cần nhiều tham số khởi tạo)

---

## 5️⃣ Tóm gọn

* **Factory nằm ở**: `payment/PaymentFactory.java`.
* **Vai trò**: Trung tâm tạo các đối tượng `PaymentService` phù hợp với từng `PaymentType`.
* **Ý nghĩa trong dự án**: Làm cho hệ thống **linh hoạt, dễ mở rộng, giảm phụ thuộc** giữa các lớp.

---


---

# 🔌 Adapter Pattern trong dự án

## 1️⃣ Vị trí của Adapter

* File:

  ```
  src/main/java/com/example/demo/store/payment/LegacyPaymentAdapter.java
  src/main/java/com/example/demo/store/legacy/LegacyPaymentGateway.java
  ```
* **`LegacyPaymentGateway`**: Hệ thống thanh toán cũ (code từ hệ thống trước, không tương thích trực tiếp).
* **`LegacyPaymentAdapter`**: Lớp trung gian giúp hệ thống mới (Spring Boot app) có thể sử dụng được hệ thống cũ.

---

## 2️⃣ Vấn đề cần giải quyết

* Trong dự án có một hệ thống thanh toán cũ (`LegacyPaymentGateway`) với **interface, cách gọi hàm và dữ liệu khác hoàn toàn** so với `PaymentService`.
* Nếu dùng trực tiếp → code sẽ rối, không thống nhất.

👉 Giải pháp: Dùng **Adapter Pattern** để **chuyển đổi giao diện** từ hệ thống cũ sang chuẩn mới (`PaymentService`).

---

## 3️⃣ Cách hoạt động của Adapter

1. **OrderService** chỉ biết làm việc với `PaymentService`.
2. Khi người dùng chọn **Legacy Payment** (`PaymentType.LEGACY`), `PaymentFactory` sẽ trả về một đối tượng `LegacyPaymentAdapter`.
3. `LegacyPaymentAdapter` sẽ **dịch lệnh**:

    * Từ `processPayment(amount)` trong `PaymentService`
    * Sang phương thức mà `LegacyPaymentGateway` hiểu (VD: `makeLegacyPayment(double amt)`).
4. Kết quả từ hệ thống cũ được trả về lại theo định dạng mới.

---

## 4️⃣ Lợi ích khi dùng Adapter

* **Tích hợp hệ thống cũ** mà không cần chỉnh sửa nó.
* **Đồng bộ giao diện**: Toàn bộ hệ thống chỉ làm việc với `PaymentService`.
* **Giảm phụ thuộc**: Nếu sau này bỏ Legacy, chỉ cần xóa Adapter mà không ảnh hưởng phần còn lại.

---

## 5️⃣ Tóm gọn

* **Adapter nằm ở**: `payment/LegacyPaymentAdapter.java`.
* **Vai trò**: Biến `LegacyPaymentGateway` thành một `PaymentService` hợp lệ.
* **Ý nghĩa trong dự án**: Cho phép tận dụng hệ thống cũ mà không phá vỡ cấu trúc, đảm bảo tính tương thích.

---

---

# 🧱 Builder Pattern trong dự án

## 1️⃣ Vị trí của Builder

* File:

  ```
  src/main/java/com/example/demo/store/builder/OrderBuilder.java
  ```
* Thuộc package `builder/`, chuyên để xây dựng các đối tượng phức tạp (ở đây là `Order`).

---

## 2️⃣ Vấn đề cần giải quyết

* Đối tượng **Order** trong hệ thống **không đơn giản**:

    * Nó có **User** (người đặt hàng).
    * Danh sách nhiều **OrderItem** (mỗi item lại tham chiếu đến Product, số lượng, giá).
    * Có thể cần gắn **PaymentService** để thực hiện thanh toán.
* Nếu khởi tạo Order bằng constructor → sẽ có quá nhiều tham số, code khó đọc, dễ sai.

👉 Giải pháp: Dùng **Builder Pattern** để khởi tạo `Order` theo cách **từng bước, rõ ràng, dễ mở rộng**.

---

## 3️⃣ Cách hoạt động của OrderBuilder

Ví dụ:

```java
Order order = new OrderBuilder()
        .withUser(user)
        .addItem(product1, 2)
        .addItem(product2, 1)
        .withPayment(PaymentFactory.create(PaymentType.PAYPAL))
        .build();
```

* **`withUser(user)`** → gắn thông tin khách hàng.
* **`addItem(product, quantity)`** → thêm sản phẩm vào giỏ hàng.
* **`withPayment(...)`** → gắn phương thức thanh toán.
* **`build()`** → tạo ra đối tượng `Order` hoàn chỉnh.

---

## 4️⃣ Lợi ích khi dùng Builder

* **Code rõ ràng, dễ đọc** (thay vì constructor 5–6 tham số).
* **Dễ mở rộng**: thêm thuộc tính mới (VD: discount, deliveryDate) mà không cần sửa constructor.
* **Giảm lỗi** khi tạo đối tượng phức tạp.
* **Kết hợp linh hoạt với Factory/Adapter**: có thể gọi `PaymentFactory` hoặc `LegacyPaymentAdapter` ngay trong builder.

---

## 5️⃣ Tóm gọn

* **Builder nằm ở**: `builder/OrderBuilder.java`.
* **Vai trò**: Hỗ trợ tạo đối tượng `Order` phức tạp theo cách rõ ràng, tuần tự.
* **Ý nghĩa trong dự án**:

    * Giúp `OrderService` dễ dàng tạo `Order` từ dữ liệu request.
    * Hạn chế lỗi khi hệ thống có nhiều thuộc tính mới cần bổ sung vào Order.

---

