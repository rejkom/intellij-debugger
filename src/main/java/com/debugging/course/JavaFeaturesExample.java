package com.debugging.course;

import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;
import java.time.Duration;

/**
 * Java features debugging exercise
 * Demonstrates debugging virtual threads, pattern matching, and records
 */
public class JavaFeaturesExample {

    // Record for data modeling
    public record User(String name, int age, String email) {
        // Compact constructor with validation
        public User {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be null or blank");
            }
            if (age < 0) {
                throw new IllegalArgumentException("Age cannot be negative");
            }
        }
    }

    static void main() throws Exception {
        System.out.println("Java Features Debugging Exercise");

        // Virtual Threads example
        testVirtualThreads();

        // Pattern Matching example
        testPatternMatching();

        // Record example
        testRecords();

        // Structured Concurrency (Preview feature)
        testStructuredConcurrency();
    }

    private static void testVirtualThreads() {
        System.out.println("\n=== Virtual Threads Test ===");

        // Create virtual threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        System.out.println("Virtual thread " + taskId + " starting - " + Thread.currentThread());
                        Thread.sleep(Duration.ofMillis(100 * taskId)); // Set breakpoint here
                        System.out.println("Virtual thread " + taskId + " completed");
                        return taskId;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return -1;
                    }
                });
            }
        } // executor.close() is called automatically
    }

    private static void testPatternMatching() {
        System.out.println("\n=== Pattern Matching Test ===");

        Object[] objects = {
                "Hello World",
                42,
                3.14159,
                new User("Alice", 30, "alice@example.com"),
                null
        };

        for (Object obj : objects) {
            String result = analyzeObject(obj); // Set breakpoint here
            System.out.println("Object: " + obj + " -> " + result);
        }
    }

    // Pattern matching with switch expressions
    private static String analyzeObject(Object obj) {
        return switch (obj) {
            case null -> "Null object";
            case String s when s.length() > 10 -> "Long string: " + s.substring(0, 10) + "...";
            case String s -> "Short string: " + s;
            case Integer i when i > 100 -> "Large integer: " + i;
            case Integer i -> "Small integer: " + i;
            case Double d -> "Double value: " + String.format("%.2f", d);
            case User(var name, var age, var email) -> "User: " + name + " (age " + age + ")";
            default -> "Unknown type: " + obj.getClass().getSimpleName();
        };
    }

    private static void testRecords() {
        System.out.println("\n=== Records Test ===");

        try {
            User user1 = new User("Bob", 25, "bob@example.com");
            User user2 = new User("Charlie", -5, "charlie@example.com"); // Will throw exception

            System.out.println("User 1: " + user1);
            System.out.println("User 2: " + user2);
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating user: " + e.getMessage()); // Set breakpoint here
        }
    }

    // Structured Concurrency
    private static void testStructuredConcurrency() throws Exception {
        System.out.println("\n=== Structured Concurrency Test ===");

        try (var scope = StructuredTaskScope.open()) {
            var task1 = scope.fork(() -> {
                Thread.sleep(Duration.ofMillis(100));
                return "Task 1 result";
            });

            var task2 = scope.fork(() -> {
                Thread.sleep(Duration.ofMillis(200));
                return "Task 2 result";
            });

            var task3 = scope.fork(() -> {
                Thread.sleep(Duration.ofMillis(50));
                throw new RuntimeException("Task 3 failed");
            });

            scope.join(); // This will throw StructuredTaskScope.FailedException if any task fails

            // Only reached if all tasks succeed
            System.out.println("Task 1: " + task1.get());
        }
    }
}