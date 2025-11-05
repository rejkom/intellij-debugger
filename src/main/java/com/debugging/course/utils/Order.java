package com.debugging.course.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private BigDecimal total;
    private BigDecimal discount;
    private final LocalDateTime orderDate;
    private OrderStatus status;

    public Order(String orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // getters and setters omitted for brevity
    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        calculateTotal();
    }

    private void calculateTotal() {
        this.total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void applyDiscount(double discountAmount) {
        this.discount = BigDecimal.valueOf(Math.max(0, discountAmount)); // Never negative
    }

    public BigDecimal getFinalTotal() {
        return total.subtract(discount);
    }
}

