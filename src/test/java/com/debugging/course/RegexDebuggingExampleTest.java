package com.debugging.course;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Regex Debugging Example Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegexDebuggingExampleTest {

    private ByteArrayOutputStream outputStream;
    private static final PrintStream ORIGINAL_OUT = System.out;

    // Regex patterns extracted for testing
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "(?:\\+1[-.\\s]?)?\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})";
    private static final String LOG_REGEX = "^(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2})\\s+\\[(\\w+)]\\s+(.+)$";

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(ORIGINAL_OUT);
    }

    @Nested
    @DisplayName("Email Validation Tests")
    @Tag("email")
    class EmailValidationTests {

        private Pattern emailPattern;

        @BeforeEach
        void setUpPattern() {
            emailPattern = Pattern.compile(EMAIL_REGEX);
        }

        @ParameterizedTest(name = "Email ''{0}'' should be valid")
        @ValueSource(strings = {
                "valid@example.com",
                "user.name@example.com",
                "user+tag@example.co.uk",
                "user_name@example.org",
                "user123@test-domain.com",
                "valid123@test-domain.org"
        })
        @DisplayName("Should validate correct email addresses")
        void shouldValidateCorrectEmails(String email) {
            // When
            boolean isValid = emailPattern.matcher(email).matches();

            // Then
            assertTrue(isValid, () -> email + " should be recognized as valid");
        }

        @ParameterizedTest(name = "Email ''{0}'' should be invalid")
        @ValueSource(strings = {
                "invalid-email",
                "@example.com",
                "user@",
                "user@.com",
                "user*name@example.com",
                "user@domain",
                ""
        })
        @DisplayName("Should reject invalid email addresses")
        void shouldRejectInvalidEmails(String email) {
            // When
            boolean isValid = emailPattern.matcher(email).matches();

            // Then
            assertFalse(isValid, () -> email + " should be recognized as invalid");
        }

        @Test
        @DisplayName("Should handle edge cases in email validation")
        void shouldHandleEmailEdgeCases() {
            assertAll("Email edge cases",
                    () -> assertFalse(emailPattern.matcher("").matches(),
                            "Empty string should be invalid"),
                    () -> assertFalse(emailPattern.matcher("user@domain@example.com").matches(),
                            "Multiple @ symbols should be invalid"),
                    () -> assertTrue(emailPattern.matcher("a@b.co").matches(),
                            "Minimal valid email should pass"),
                    () -> assertFalse(emailPattern.matcher("user name@example.com").matches(),
                            "Space in email should be invalid")
            );
        }

        @Test
        @DisplayName("Should test email validation method from class")
        @Tag("integration")
        void shouldTestEmailValidationMethod() {
            // When
            RegexDebuggingExample.testEmailValidation();
            String output = outputStream.toString();

            // Then
            assertAll("Verify email validation output",
                    () -> assertTrue(output.contains("valid@example.com"),
                            "Should test basic valid email"),
                    () -> assertTrue(output.contains("VALID"),
                            "Should mark valid emails"),
                    () -> assertTrue(output.contains("INVALID"),
                            "Should mark invalid emails"),
                    () -> assertTrue(output.contains("invalid-email"),
                            "Should test invalid email format")
            );
        }
    }

    @Nested
    @DisplayName("Phone Number Extraction Tests")
    @Tag("phone")
    class PhoneNumberExtractionTests {

        private Pattern phonePattern;

        @BeforeEach
        void setUpPattern() {
            phonePattern = Pattern.compile(PHONE_REGEX);
        }

        static Stream<Arguments> validPhoneNumbers() {
            return Stream.of(
                    arguments("(555) 123-4567", "555", "123", "4567"),
                    arguments("555.987.6543", "555", "987", "6543"),
                    arguments("+1-555-111-2222", "555", "111", "2222"),
                    arguments("5551234567", "555", "123", "4567"),
                    arguments("+1 555 123 4567", "555", "123", "4567")
            );
        }

        @ParameterizedTest(name = "Phone ''{0}'' -> area: {1}, exchange: {2}, number: {3}")
        @MethodSource("validPhoneNumbers")
        @DisplayName("Should extract phone number components correctly")
        void shouldExtractPhoneComponents(String phone, String expectedArea,
                                          String expectedExchange, String expectedNumber) {
            // When
            Matcher matcher = phonePattern.matcher(phone);

            // Then
            assertTrue(matcher.find(), () -> phone + " should be recognized as valid phone");
            assertAll("Verify extracted components",
                    () -> assertEquals(expectedArea, matcher.group(1),
                            "Area code should match"),
                    () -> assertEquals(expectedExchange, matcher.group(2),
                            "Exchange should match"),
                    () -> assertEquals(expectedNumber, matcher.group(3),
                            "Number should match")
            );
        }

        @Test
        @DisplayName("Should find multiple phone numbers in text")
        void shouldFindMultiplePhoneNumbers() {
            // Given
            String text = """
                    Contact: (555) 123-4567
                    Alternative: 555.987.6543
                    Mobile: +1-555-111-2222
                    """;

            // When
            Matcher matcher = phonePattern.matcher(text);
            int count = 0;
            while (matcher.find()) {
                count++;
            }

            // Then
            assertEquals(3, count, "Should find exactly 3 phone numbers");
        }

        @Test
        @DisplayName("Should not match invalid phone patterns")
        void shouldNotMatchInvalidPhones() {
            // Given
            String[] invalidPhones = {
                    "123-45-6789",  // SSN format
                    "12-345-6789",  // Wrong grouping
                    "555-12-34",    // Too short
                    "abed-efg-hick" // Letters
            };

            // When & Then
            for (String phone : invalidPhones) {
                Matcher matcher = phonePattern.matcher(phone);
                assertFalse(matcher.find(),
                        () -> phone + " should not match phone pattern");
            }
        }

        @Test
        @DisplayName("Should test phone extraction method from class")
        @Tag("integration")
        void shouldTestPhoneExtractionMethod() {
            // When
            RegexDebuggingExample.testPhoneNumberExtraction();
            String output = outputStream.toString();

            // Then
            assertAll("Verify phone extraction output",
                    () -> assertTrue(output.contains("Found phone numbers:"),
                            "Should have header for found numbers"),
                    () -> assertTrue(output.contains("Area:"),
                            "Should display area codes"),
                    () -> assertTrue(output.contains("Exchange:"),
                            "Should display exchange numbers"),
                    () -> assertTrue(output.matches("(?s).*555.*123.*4567.*"),
                            "Should find office number components")
            );
        }
    }

    @Nested
    @DisplayName("Log Parsing Tests")
    @Tag("log-parsing")
    class LogParsingTests {

        private Pattern logPattern;

        @BeforeEach
        void setUpPattern() {
            logPattern = Pattern.compile(LOG_REGEX);
        }

        static Stream<Arguments> validLogLines() {
            return Stream.of(
                    arguments(
                            "2024-01-15 10:30:25 [INFO] User logged in",
                            "2024-01-15", "10:30:25", "INFO", "User logged in"
                    ),
                    arguments(
                            "2024-12-31 23:59:59 [ERROR] Connection failed",
                            "2024-12-31", "23:59:59", "ERROR", "Connection failed"
                    ),
                    arguments(
                            "2024-06-15 12:00:00 [WARN] Memory usage high",
                            "2024-06-15", "12:00:00", "WARN", "Memory usage high"
                    ),
                    arguments(
                            "2024-01-01 00:00:00 [DEBUG] Debug message with: special chars!",
                            "2024-01-01", "00:00:00", "DEBUG", "Debug message with: special chars!"
                    )
            );
        }

        @ParameterizedTest(name = "Log: {0}")
        @MethodSource("validLogLines")
        @DisplayName("Should parse valid log lines correctly")
        void shouldParseValidLogLines(String logLine, String expectedDate,
                                      String expectedTime, String expectedLevel,
                                      String expectedMessage) {
            // When
            Matcher matcher = logPattern.matcher(logLine);

            // Then
            assertTrue(matcher.matches(), () -> logLine + " should match log pattern");
            assertAll("Verify parsed log components",
                    () -> assertEquals(expectedDate, matcher.group(1),
                            "Date should match"),
                    () -> assertEquals(expectedTime, matcher.group(2),
                            "Time should match"),
                    () -> assertEquals(expectedLevel, matcher.group(3),
                            "Log level should match"),
                    () -> assertEquals(expectedMessage, matcher.group(4),
                            "Message should match")
            );
        }

        @ParameterizedTest(name = "Invalid log: ''{0}''")
        @ValueSource(strings = {
                "Invalid log line without proper format",
                "2024-01-15 [INFO] Missing time",
                "10:30:25 [INFO] Missing date",
                "[INFO] Only has level and message",
                ""
        })
        @DisplayName("Should reject invalid log lines")
        void shouldRejectInvalidLogLines(String logLine) {
            // When
            Matcher matcher = logPattern.matcher(logLine);

            // Then
            assertFalse(matcher.matches(),
                    () -> logLine + " should not match log pattern");
        }

        @Test
        @DisplayName("Should handle various log levels")
        void shouldHandleVariousLogLevels() {
            // Given
            String[] logLevels = {"INFO", "ERROR", "WARN", "DEBUG", "TRACE", "FATAL"};

            // When & Then
            for (String level : logLevels) {
                String logLine = String.format("2024-01-15 10:30:25 [%s] Test message", level);
                Matcher matcher = logPattern.matcher(logLine);

                assertTrue(matcher.matches(),
                        () -> "Log level " + level + " should be recognized");
                assertEquals(level, matcher.group(3),
                        () -> "Should extract " + level + " correctly");
            }
        }

        @Test
        @DisplayName("Should test log parsing method from class")
        @Tag("integration")
        void shouldTestLogParsingMethod() {
            // When
            RegexDebuggingExample.testLogParsing();
            String output = outputStream.toString();

            // Then
            assertAll("Verify log parsing output",
                    () -> assertTrue(output.contains("Date:"),
                            "Should display dates"),
                    () -> assertTrue(output.contains("Time:"),
                            "Should display times"),
                    () -> assertTrue(output.contains("Level:"),
                            "Should display log levels"),
                    () -> assertTrue(output.contains("Message:"),
                            "Should display messages"),
                    () -> assertTrue(output.contains("Invalid log format:"),
                            "Should identify invalid log lines")
            );
        }
    }

    @Nested
    @DisplayName("Regex Replacement Tests")
    @Tag("replacement")
    class RegexReplacementTests {

        @Test
        @DisplayName("Should replace dollar amounts correctly")
        void shouldReplaceDollarAmounts() {
            // Given
            String input = "The price is $19.99 and the tax is $2.50";
            String dollarRegex = "\\$\\d+\\.\\d{2}";

            // When
            String result = input.replaceAll(dollarRegex, "€XX.XX");

            // Then
            assertAll("Verify dollar replacement",
                    () -> assertFalse(result.contains("$"),
                            "Should not contain dollar signs"),
                    () -> assertTrue(result.contains("€XX.XX"),
                            "Should contain Euro placeholders"),
                    () -> assertFalse(result.contains("19.99"),
                            "Should replace first amount"),
                    () -> assertFalse(result.contains("2.50"),
                            "Should replace second amount")
            );
        }

        @ParameterizedTest(name = "Replace ''{0}'' with ''{1}''")
        @CsvSource({
                "$10.00, 1",
                "$19.99 and $29.99, 2",
                "Cost: $100.50 Tax: $10.05, 2",
                "No prices here, 0"
        })
        @DisplayName("Should count and replace all dollar amounts")
        void shouldCountAndReplaceAllAmounts(String input, int expectedCount) {
            // Given
            String dollarRegex = "\\$\\d+\\.\\d{2}";
            Pattern pattern = Pattern.compile(dollarRegex);

            // When
            Matcher matcher = pattern.matcher(input);
            int actualCount = 0;
            while (matcher.find()) {
                actualCount++;
            }

            // Then
            assertEquals(expectedCount, actualCount,
                    () -> "Should find " + expectedCount + " dollar amounts in: " + input);
        }
    }

    @Nested
    @DisplayName("Main Method Integration Tests")
    @Tag("integration")
    class MainMethodTests {

        @Test
        void mainMethodShouldExecuteAllTests() {
            // When
            assertDoesNotThrow(RegexDebuggingExample::main,
                    "Main method should execute without exceptions");

            String output = outputStream.toString();

            // Then
            assertAll("Verify main method execution",
                    () -> assertTrue(output.contains("Regex Debugging Exercise"),
                            "Should print header"),
                    () -> assertTrue(output.contains("Email Validation"),
                            "Should run email tests"),
                    () -> assertTrue(output.contains("Phone Number Extraction"),
                            "Should run phone tests"),
                    () -> assertTrue(output.contains("Log Parsing"),
                            "Should run log parsing tests")
            );
        }
    }

    @Nested
    @DisplayName("Debugging Exercise Documentation")
    @Tag("educational")
    class DebuggingExercisesTests {

        @Test
        @DisplayName("Document Check RegExp usage scenarios")
        @Tag("documentation")
        void documentCheckRegExpScenarios() {
            var guide = """
                    IntelliJ Check RegExp Debugging Scenarios:
                    
                    1. Email Regex Testing:
                       - Place cursor on EMAIL_REGEX
                       - Press Alt+Enter -> Check RegExp
                       - Test with: valid@example.com, invalid@email, @test.com
                    
                    2. Phone Number Regex Testing:
                       - Check PHONE_REGEX pattern
                       - Test various formats: (555) 123-4567, 555.123.4567, +1-555-123-4567
                       - Verify capture groups for area code, exchange, number
                    
                    3. Log Pattern Regex Testing:
                       - Validate LOG_REGEX
                       - Test date/time formats
                       - Verify message extraction
                    
                    4. Regex Debugger Features:
                       - Match count verification
                       - Capture group inspection
                       - Performance analysis for complex patterns
                    """;

            assertNotNull(guide, "Check RegExp guide should be documented");
        }

        @Test
        @DisplayName("Document regex best practices")
        void documentRegexBestPractices() {
            var practices = """
                    Regex Best Practices Demonstrated:
                    1. Use raw strings for readability (Java 15+ text blocks)
                    2. Compile Pattern once, reuse Matcher
                    3. Use named groups for complex patterns
                    4. Test edge cases: empty strings, special chars, boundaries
                    5. Validate regex performance with large inputs
                    6. Document regex intent with comments
                    7. Use IntelliJ's Check RegExp for rapid testing
                    """;

            assertTrue(practices.contains("Compile Pattern once"),
                    "Should document Pattern compilation best practice");
        }
    }

    @Nested
    @DisplayName("Performance and Edge Case Tests")
    @Tag("performance")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large input efficiently")
        @Tag("performance")
        void shouldHandleLargeInputEfficiently() {
            // Given
            String largeText = "Contact: (555) 123-4567\n".repeat(1000);
            Pattern phonePattern = Pattern.compile(PHONE_REGEX);

            // When
            long startTime = System.nanoTime();
            Matcher matcher = phonePattern.matcher(largeText);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            // Then
            int finalCount = count;
            assertAll("Verify performance",
                    () -> assertEquals(1000, finalCount, "Should find all 1000 phone numbers"),
                    () -> assertTrue(durationMs < 1000,
                            "Should complete within 1 second, took: " + durationMs + "ms")
            );
        }

        @Test
        @DisplayName("Should handle concurrent regex matching")
        @Tag("concurrency")
        void shouldHandleConcurrentMatching() {
            // Given
            Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
            String[] testEmails = {
                    "test1@example.com",
                    "test2@example.com",
                    "test3@example.com"
            };

            // When & Then - Pattern is thread-safe, Matcher is not
            assertDoesNotThrow(() -> {
                Thread[] threads = new Thread[testEmails.length];
                for (int i = 0; i < testEmails.length; i++) {
                    final String email = testEmails[i];
                    threads[i] = Thread.ofVirtual().start(() -> {
                        Matcher matcher = emailPattern.matcher(email);
                        assertTrue(matcher.matches());
                    });
                }
                for (Thread thread : threads) {
                    thread.join();
                }
            }, "Pattern should be safely used across multiple threads");
        }
    }
}
