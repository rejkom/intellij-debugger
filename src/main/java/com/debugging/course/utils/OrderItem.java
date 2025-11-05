package com.debugging.course.utils;

import java.math.BigDecimal;

public class OrderItem {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String category;

    public OrderItem(String productId, String productName, BigDecimal price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.category = "General";
    }

    public OrderItem(String productId, String productName, BigDecimal price, int quantity, String category) {
        this(productId, productName, price, quantity);
        this.category = category;
    }

    // Getters and setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return String.format("OrderItem{id='%s', name='%s', price=%s, qty=%d, subtotal=%s}",
                productId, productName, price, quantity, getSubtotal());
    }

}