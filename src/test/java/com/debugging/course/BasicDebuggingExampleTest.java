package com.debugging.course;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Basic Debugging Example Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BasicDebuggingExampleTest {

    private static final PrintStream ORIGINAL_OUT = System.out;
    private static final PrintStream ORIGINAL_ERR = System.err;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(ORIGINAL_OUT);
        System.setErr(ORIGINAL_ERR);
    }

    @Nested
    @DisplayName("Generate Numbers Tests")
    class GenerateNumbersTests {

        @Test
        @DisplayName("Should generate exact number of elements requested")
        @Tag("bug-detection")
        void shouldGenerateCorrectNumberOfElements() {
            // Given
            int expectedCount = 5;

            // When
            List<Integer> numbers = BasicDebuggingExample.generateNumbers(expectedCount);

            // Then - This will FAIL due to off-by-one error (intentional bug)
            assertNotEquals(expectedCount, numbers.size(),
                    "Bug detected: generates " + numbers.size() + " elements instead of " + expectedCount);
            assertEquals(expectedCount + 1, numbers.size(),
                    "Expected off-by-one error to produce " + (expectedCount + 1) + " elements");
        }

        @ParameterizedTest(name = "Generate {0} numbers should produce list with correct size")
        @ValueSource(ints = {1, 3, 5, 10, 100})
        @DisplayName("Should demonstrate consistent off-by-one error across different inputs")
        @Tag("parameterized")
        void shouldShowConsistentOffByOneError(int count) {
            // When
            List<Integer> numbers = BasicDebuggingExample.generateNumbers(count);

            // Then
            assertEquals(count + 1, numbers.size(),
                    "Off-by-one error should consistently add 1 extra element");
        }

        @Test
        @DisplayName("Should verify doubled values in generated numbers")
        @Tag("logic-verification")
        void shouldContainDoubledValues() {
            // Given
            int count = 3;

            // When
            List<Integer> numbers = BasicDebuggingExample.generateNumbers(count);

            // Then
            assertAll("Verify all numbers are doubled correctly",
                    () -> assertTrue(numbers.contains(2), "Should contain 2 (1*2)"),
                    () -> assertTrue(numbers.contains(4), "Should contain 4 (2*2)"),
                    () -> assertTrue(numbers.contains(6), "Should contain 6 (3*2)"),
                    () -> assertTrue(numbers.contains(8), "Should contain 8 (4*2) - bug produces extra element"));
        }

        @Test
        @DisplayName("Should handle edge case with zero count")
        @Tag("edge-cases")
        void shouldHandleZeroCount() {
            // When
            List<Integer> numbers = BasicDebuggingExample.generateNumbers(0);

            // Then
            assertEquals(1, numbers.size(),
                    "Even with count=0, off-by-one error produces 1 element");
        }
    }

    @Nested
    @DisplayName("Process Numbers Tests")
    class ProcessNumbersTests {

        @Test
        @DisplayName("Should process numbers correctly with valid prefix")
        @Tag("functionality")
        void shouldProcessNumbersCorrectly() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3);

            // When
            String result = BasicDebuggingExample.processNumbers(numbers);

            // Then
            assertNotNull(result, "Result should not be null");
            assertTrue(result.contains("NUMBER:"), "Result should contain prefix");
            assertTrue(result.contains("1"), "Result should contain first number");
            assertTrue(result.contains("2"), "Result should contain second number");
            assertTrue(result.contains("3"), "Result should contain third number");
        }

        @Test
        @DisplayName("Should handle empty list")
        @Tag("edge-cases")
        void shouldHandleEmptyList() {
            // Given
            List<Integer> numbers = List.of();

            // When
            String result = BasicDebuggingExample.processNumbers(numbers);

            // Then
            assertNotNull(result, "Result should not be null");
            assertEquals("", result, "Result should be empty for empty list");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle errors in processWithErrorHandling method")
        @Tag("error-handling")
        void shouldHandleErrorsInProcessWithErrorHandling() {
            // When
            assertDoesNotThrow(BasicDebuggingExample::processWithErrorHandling,
                    "processWithErrorHandling should catch all exceptions");

            // Then
            String output = outputStream.toString();
            assertFalse(output.isEmpty(), "Should produce some output");
        }
    }

    @Nested
    @DisplayName("Debugging Learning Objectives")
    @Tag("educational")
    class DebuggingLearningTests {

        @Test
        @DisplayName("Document expected debugging breakpoint locations")
        @Tag("documentation")
        void documentBreakpointLocations() {
            // This test serves as documentation for instructors
            var breakpointGuide = """
                    Recommended breakpoints for debugging exercises:
                    1. Line in generateNumbers() - loop condition (i <= count + 1)
                    2. Line in processNumbers() - StringBuilder append operations
                    3. Line in printArrayElements() - array[i] access in loop
                    
                    Students should:
                    - Use Step Over to observe loop iterations
                    - Inspect variable values in debugger
                    - Use Evaluate Expression to test fixes
                    """;

            assertNotNull(breakpointGuide, "Breakpoint guide should be available");
            assertTrue(breakpointGuide.contains("generateNumbers"),
                    "Guide should reference all buggy methods");
        }
    }
}