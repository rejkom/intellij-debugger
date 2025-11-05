package com.debugging.course;

import java.util.ArrayList;
import java.util.List;

public class BasicDebuggingExample {

    static void main() {
        System.out.println("Starting Basic Debugging Exercise");

        List<Integer> numbers = generateNumbers(5);
        System.out.println("Generated numbers: " + numbers);

        String result = processNumbers(numbers);
        System.out.println("Result: " + result);

        int[] array = {1, 2, 3, 4, 5};
        printArrayElements(array);
        processWithErrorHandling();
    }

    private static List<Integer> generateNumbers(int count) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= count + 1; i++) {
            numbers.add(i * 2);
        }
        return numbers;
    }

    private static String processNumbers(List<Integer> numbers) {
        String prefix = "NUMBER: ";
        StringBuilder result = new StringBuilder();

        for (Integer number : numbers) {
            result.append(prefix.toUpperCase())
                    .append(number)
                    .append(" ");
        }

        return result.toString();
    }

    private static void printArrayElements(int[] array) {
        System.out.println("Array elements:");
        for (int i = 0; i < array.length; i++) {
            System.out.println("Element " + i + ": " + array[i]);
        }
    }

    public static void processWithErrorHandling() {
        try {
            List<Integer> numbers = generateNumbers(7);
            String result = processNumbers(numbers);
            System.out.println("Result: " + result);
        } catch (NullPointerException e) {
            System.err.println("Caught NPE: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Caught runtime exception: " + e.getMessage());
        }
    }

}