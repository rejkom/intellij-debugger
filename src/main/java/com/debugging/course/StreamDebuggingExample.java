package com.debugging.course;

import java.util.List;

/**
 * Debugging Java Streams with "Trace Current Stream Chain".
 *
 * The stream below produces the WRONG result on purpose.
 * We expect the total to be the sum of the prices of EXPENSIVE products,
 * but the number that comes out is too small.
 *
 * How to debug in IntelliJ:
 *   1. Put a breakpoint on the line that starts the stream (the "products.stream()" line).
 *   2. Start debugging. When the program stops, open the debugger toolbar.
 *   3. Click the "Trace Current Stream Chain" button (the icon with the dots).
 *   4. Look at each step (filter, map, reduce) to see where items disappear.
 */
public class StreamDebuggingExample {

    public record Product(String name, double price, String category) {
    }

    static void main() {
        System.out.println("Stream Debugging Exercise");

        List<Product> products = List.of(
                new Product("Laptop", 1200.0, "Electronics"),
                new Product("Mouse", 25.0, "Electronics"),
                new Product("Desk", 300.0, "Furniture"),
                new Product("Monitor", 450.0, "Electronics"),
                new Product("Pen", 2.0, "Office"),
                new Product("Chair", 150.0, "Furniture")
        );

        double total = sumExpensiveElectronics(products);

        // We expect 1650.0 (Laptop 1200 + Monitor 450). The bug makes it different.
        System.out.println("Total price of expensive electronics: " + total);
    }

    /**
     * BUG: the filter for "expensive" uses the wrong comparison,
     * so the wrong products pass through the stream.
     * Use "Trace Current Stream Chain" to SEE which items survive each step.
     */
    static double sumExpensiveElectronics(List<Product> products) {
        return products.stream()                                  // <-- set breakpoint here
                .filter(p -> p.category().equals("Electronics"))
                .filter(p -> p.price() < 400.0)                   // BUG: should be > 400.0
                .mapToDouble(Product::price)
                .sum();
    }
}

