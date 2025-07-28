package com.debugging.course;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic debugging exercise - contains several intentional bugs
 * Students will learn to use breakpoints and step through code
 */
public class BasicDebuggingExample {

    static void main() {
        System.out.println("Starting Basic Debugging Exercise");

        // Bug 1: Off-by-one error in loop
        List<Integer> numbers = generateNumbers(5);
        System.out.println("Generated numbers: " + numbers);

        // Bug 2: Null pointer exception
        String result = processNumbers(numbers);
        System.out.println("Result: " + result);

        // Bug 3: Array index out of bounds
        int[] array = {1, 2, 3, 4, 5};
        printArrayElements(array);
    }

    // Contains off-by-one error
    private static List<Integer> generateNumbers(int count) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= count + 1; i++) { // BUG: should be i <= count
            numbers.add(i * 2);
        }
        return numbers;
    }

    // Contains potential null pointer exception
    private static String processNumbers(List<Integer> numbers) {
        String prefix = null; // BUG: should be initialized
        StringBuilder result = new StringBuilder();

        for (Integer number : numbers) {
            result.append(prefix.toUpperCase()) // BUG: prefix is null
                  .append(number)
                  .append(" ");
        }

        return result.toString();
    }

    // Contains array index out of bounds error
    private static void printArrayElements(int[] array) {
        System.out.println("Array elements:");
        for (int i = 0; i <= array.length; i++) { // BUG: should be i < array.length
            System.out.println("Element " + i + ": " + array[i]);
        }
    }
}