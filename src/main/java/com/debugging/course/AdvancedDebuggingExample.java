package com.debugging.course;

import com.debugging.course.utils.Customer;
import com.debugging.course.utils.Order;
import com.debugging.course.utils.OrderItem;
import com.debugging.course.utils.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates advanced debugging techniques in IntelliJ IDEA using the order processing system.
 * This class can be used for features such as Evaluate Expression, Throw Exception, and Force Return to explore code
 * behavior, test hypotheses, and simulate various runtime scenarios without modifying source code.
 */
public class AdvancedDebuggingExample {

    static void main() {
        AdvancedDebuggingExample challenge = new AdvancedDebuggingExample();
        List<Order> orders = challenge.createTestOrders();
        challenge.processOrders(orders);
    }

    public void processOrders(List<Order> orders) {
        for (Order order : orders) {
            if (order.getCustomer().isPremium()) {
                double discount = calculateDiscount(order);
                order.applyDiscount(discount);
            }
            processOrder(order);
        }
    }

    double calculateDiscount(Order order) {
        double baseDiscount = order.getTotal().doubleValue() * 0.1;
        int loyaltyYears = order.getCustomer().getLoyaltyYears();

        if (loyaltyYears > 5) {
            baseDiscount *= 1.5; // 50% bonus for loyal customers
        }
        return Math.max(0.0, baseDiscount); // Only positive discounts
    }

    private void processOrder(Order order) {
        System.out.printf("Order: %-10s | Customer: %-15s | Total: %8.2f | Discount: %8.2f | Final: %8.2f%n",
                order.getOrderId(),
                order.getCustomer().getName(),
                order.getTotal().doubleValue(),
                order.getDiscount().doubleValue(),
                order.getFinalTotal().doubleValue());
        order.setStatus(OrderStatus.CONFIRMED);
    }

    List<Order> createTestOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(createSampleOrder("ORD-001", new Customer("C001", "John Smith", "john@example.com", false, 1), 150.00));
        orders.add(createSampleOrder("ORD-002", new Customer("C002", "Jane Doe", "jane@example.com", true, 3), 250.00));
        orders.add(createSampleOrder("ORD-003", new Customer("C003", "Bob Wilson", "bob@example.com", true, 10), 100.00));
        orders.add(createSampleOrder("ORD-004", new Customer("C004", "Alice Johnson", "alice@example.com", true, 15), 300.00));
        orders.add(createSampleOrder("ORD-005", new Customer("C005", "Charlie Brown", "charlie@example.com", true, 8), 25.00));
        return orders;
    }

    private Order createSampleOrder(String orderId, Customer customer, double baseAmount) {
        Order order = new Order(orderId, customer);
        order.addItem(new OrderItem("P001", "Product A", BigDecimal.valueOf(baseAmount * 0.6), 1, "Electronics"));
        order.addItem(new OrderItem("P002", "Product B", BigDecimal.valueOf(baseAmount * 0.4), 1, "Books"));
        return order;
    }

}
