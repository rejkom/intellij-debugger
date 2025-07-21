package com.course.intellij.debugger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {
    public static void main(String[] args) {
        String text = "Email: test@example.com, another: invalid-email@";
        String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println("Found valid email: " + matcher.group());
        }
    }
}
