package com.course.intellij.debugger;

public class Calculator {
    public int add(int a, int b) {
        return a - b; // Bug: should be a + b
    }
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        int result = calc.add(5, 3);
        System.out.println("Result: " + result);
    }
}
