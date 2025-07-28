package com.debugging.course;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Regular Expression debugging exercise
 * Demonstrates using IntelliJ's Check RegExp intention
 */
public class RegexDebuggingExample {

    static void main() {
        System.out.println("Regex Debugging Exercise");

        // Email validation examples
        testEmailValidation();

        // Phone number extraction
        testPhoneNumberExtraction();

        // Log parsing
        testLogParsing();
    }

    private static void testEmailValidation() {
        System.out.println("\n=== Email Validation ===");

        // Regex pattern for email validation (simplified)
        // Place cursor on this regex and use Alt+Enter -> Check RegExp
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        String[] testEmails = {
            "valid@example.com",
            "user.name+tag@example.co.uk",
            "invalid-email",
            "@example.com",
            "user@",
            "user@.com",
            "valid123@test-domain.org"
        };

        for (String email : testEmails) {
            boolean isValid = emailPattern.matcher(email).matches();
            System.out.println(email + " -> " + (isValid ? "VALID" : "INVALID"));
        }
    }

    private static void testPhoneNumberExtraction() {
        System.out.println("\n=== Phone Number Extraction ===");

        String text = """
            Contact us at:
            Office: (555) 123-4567
            Mobile: 555.987.6543
            International: +1-555-111-2222
            Alternative: 5551234567
            Invalid: 123-45-6789 (SSN, not phone)
            """;

        // Regex for US phone numbers - test this with Check RegExp
        String phoneRegex = "(?:\\+1[-.\\s]?)?\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher matcher = phonePattern.matcher(text);

        System.out.println("Found phone numbers:");
        while (matcher.find()) {
            String fullMatch = matcher.group();
            String areaCode = matcher.group(1);
            String exchange = matcher.group(2);
            String number = matcher.group(3);

            System.out.printf("Full: %s, Area: %s, Exchange: %s, Number: %s%n", 
                             fullMatch, areaCode, exchange, number);
        }
    }

    private static void testLogParsing() {
        System.out.println("\n=== Log Parsing ===");

        String[] logLines = {
            "2024-01-15 10:30:25 [INFO] User john.doe logged in successfully",
            "2024-01-15 10:31:02 [ERROR] Database connection failed: timeout after 30s",
            "2024-01-15 10:31:15 [WARN] Retrying database connection (attempt 2/3)",
            "2024-01-15 10:31:18 [INFO] Database connection restored",
            "Invalid log line without proper format"
        };

        // Regex for parsing log entries - use Check RegExp to test
        String logRegex = "^(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2})\\s+\\[(\\w+)\\]\\s+(.+)$";
        Pattern logPattern = Pattern.compile(logRegex);

        for (String line : logLines) {
            Matcher matcher = logPattern.matcher(line);
            if (matcher.matches()) {
                String date = matcher.group(1);
                String time = matcher.group(2);
                String level = matcher.group(3);
                String message = matcher.group(4);

                System.out.printf("Date: %s, Time: %s, Level: %s, Message: %s%n", 
                                 date, time, level, message);
            } else {
                System.out.println("Invalid log format: " + line);
            }
        }
    }

    // Method to demonstrate regex replacement
    private static void demonstrateRegexReplacement() {
        String input = "The price is $19.99 and the tax is $2.50";

        // Replace all dollar amounts with Euro symbol
        // Test this regex with Check RegExp: \$\d+\.\d{2}
        String result = input.replaceAll("\\$\\d+\\.\\d{2}", "€XX.XX");

        System.out.println("Original: " + input);
        System.out.println("Replaced: " + result);
    }
}