

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

