package com.example.demo.store.dto;

import java.util.List;

public class OrderRequest {
    private Long userId;
    private List<Item> items;
    private PaymentType paymentType;

    public static class Item {
        private Long productId;
        private int quantity;
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
}
