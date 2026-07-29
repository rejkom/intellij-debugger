package com.debugging.course.exercise;

import java.util.ArrayList;
import java.util.List;

/**
 * Advanced breakpoint strategies in IntelliJ IDEA.
 *
 * This class is a playground for breakpoint features. The code itself
 * works correctly - the goal is to PRACTICE breakpoints, not to fix a bug.
 *
 * Things to demonstrate with this file:
 *   1. CONDITIONAL breakpoint:    stop only when order.id == 750
 *   2. HIT COUNT / pass count:     stop every 100th time
 *   3. LOGGING breakpoint:         print a value WITHOUT stopping
 *                                  (uncheck "Suspend", check "Evaluate and log")
 *   4. DEPENDENT breakpoint:       enable breakpoint B only after breakpoint A is hit
 *   5. EXCEPTION breakpoint:       stop when IllegalStateException is thrown
 *   6. FIELD watchpoint:           stop when 'processedCount' changes
 */
public class BreakpointStrategiesExercise {

    public record Order(int id, double amount, boolean flagged) {
    }

    private int processedCount = 0; // <-- add a field watchpoint here

    static void main() {
        System.out.println("Advanced Breakpoints Exercise");

        BreakpointStrategiesExercise app = new BreakpointStrategiesExercise();
        List<Order> orders = app.createOrders(1000);

        double total = app.processAll(orders);
        System.out.println("Processed " + app.processedCount + " orders");
        System.out.println("Total amount: " + total);
    }

    List<Order> createOrders(int count) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            boolean flagged = (i % 250 == 0); // a few "flagged" orders
            orders.add(new Order(i, 10.0 + i, flagged));
        }
        return orders;
    }

    double processAll(List<Order> orders) {
        double total = 0.0;
        for (Order order : orders) {
            total += processOne(order);   // <-- conditional breakpoint: order.id() == 750
        }
        return total;
    }

    double processOne(Order order) {
        processedCount++;                 // <-- field watchpoint target

        if (order.flagged()) {
            // Great place for an EXCEPTION breakpoint demo:
            // temporarily 'throw' here, or catch this exception type.
            handleFlaggedOrder(order);
        }
        return order.amount();            // <-- logging breakpoint: log "order.id() + amount"
    }

    void handleFlaggedOrder(Order order) {
        if (order.amount() < 0) {
            throw new IllegalStateException("Negative amount for order " + order.id());
        }
        // Normal handling for flagged orders goes here.
    }
}

