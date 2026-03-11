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
                    () -> assertTrue(numbers.contains(8), "Should contain 8 (4*2) - bug produces extra element")
            );
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
        @DisplayName("Should throw NullPointerException due to null prefix")
        @Tag("bug-detection")
        void shouldThrowNullPointerException() {
            // Given
            List<Integer> numbers = List.of(1, 2, 3);

            // When & Then
            NullPointerException exception = assertThrows(NullPointerException.class,
                    () -> BasicDebuggingExample.processNumbers(numbers),
                    "Expected NullPointerException due to null prefix");

            assertNotNull(exception, "Exception should be thrown");
        }

        @Test
        @DisplayName("Should fail immediately on first iteration")
        @Tag("debugging-practice")
        void shouldFailOnFirstIteration() {
            // Given
            List<Integer> singleNumber = List.of(42);

            // When & Then
            NullPointerException exception = assertThrows(NullPointerException.class,
                    () -> BasicDebuggingExample.processNumbers(singleNumber));

            // Verify the exception occurs at the expected location
            assertTrue(exception.getStackTrace().length > 0,
                    "Stack trace should be available for debugging");
        }
    }

    @Nested
    @DisplayName("Print Array Elements Tests")
    class PrintArrayElementsTests {

        @Test
        @DisplayName("Should throw ArrayIndexOutOfBoundsException")
        @Tag("bug-detection")
        void shouldThrowArrayIndexOutOfBoundsException() {
            // Given
            int[] array = {1, 2, 3, 4, 5};

            // When & Then
            ArrayIndexOutOfBoundsException exception = assertThrows(
                    ArrayIndexOutOfBoundsException.class,
                    () -> BasicDebuggingExample.printArrayElements(array),
                    "Expected ArrayIndexOutOfBoundsException due to <= instead of <");

            assertNotNull(exception, "Exception should be thrown");
        }

        @Test
        @DisplayName("Should fail with single element array")
        @Tag("edge-cases")
        void shouldFailWithSingleElement() {
            // Given
            int[] array = {42};

            // When & Then
            assertThrows(ArrayIndexOutOfBoundsException.class,
                    () -> BasicDebuggingExample.printArrayElements(array),
                    "Even single element array triggers the bug");
        }

        @ParameterizedTest(name = "Array of size {0} should throw exception")
        @ValueSource(ints = {1, 2, 5, 10, 100})
        @DisplayName("Should consistently fail regardless of array size")
        @Tag("parameterized")
        void shouldFailConsistently(int size) {
            // Given
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = i + 1;
            }

            // When & Then
            assertThrows(ArrayIndexOutOfBoundsException.class,
                    () -> BasicDebuggingExample.printArrayElements(array),
                    "Bug should occur with array of size " + size);
        }

        @Test
        @DisplayName("Should print correct number of elements before exception")
        @Tag("debugging-practice")
        void shouldPrintElementsBeforeException() {
            // Given
            int[] array = {10, 20, 30};

            // When & Then
            assertThrows(ArrayIndexOutOfBoundsException.class,
                    () -> BasicDebuggingExample.printArrayElements(array));

            String output = outputStream.toString();

            // Verify that valid elements were printed before exception
            assertAll("Verify partial output before exception",
                    () -> assertTrue(output.contains("Element 0: 10"),
                            "First element should be printed"),
                    () -> assertTrue(output.contains("Element 1: 20"),
                            "Second element should be printed"),
                    () -> assertTrue(output.contains("Element 2: 30"),
                            "Third element should be printed")
            );
        }
    }

    @Nested
    @DisplayName("Main Method Integration Tests")
    class MainMethodTests {

        @Test
        @DisplayName("Main method should fail during execution")
        @Tag("integration")
        void mainMethodShouldFail() {
            // When & Then
            assertThrows(NullPointerException.class,
                    BasicDebuggingExample::main,
                    "Main method should fail with NullPointerException in processNumbers");
        }

        @Test
        @DisplayName("Should execute first steps before failing")
        @Tag("integration")
        void shouldExecuteInitialStepsBeforeFailing() {
            // When
            try {
                BasicDebuggingExample.main();
                fail("Should have thrown NullPointerException");
            } catch (NullPointerException e) {
                // Expected behavior
            }

            // Then
            String output = outputStream.toString();
            assertTrue(output.contains("Starting Basic Debugging Exercise"),
                    "Should print initial message before failing");
            assertTrue(output.contains("Generated numbers:"),
                    "Should generate and print numbers before failing");
        }
    }

    @Nested
    @DisplayName("Debugging Learning Objectives")
    @Tag("educational")
    class DebuggingLearningTests {

        @Test
        @DisplayName("Verify all three distinct bug types are present")
        void shouldContainThreeDistinctBugTypes() {
            assertAll("Verify bug diversity for learning",
                    () -> {
                        List<Integer> numbers = BasicDebuggingExample.generateNumbers(5);
                        assertEquals(6, numbers.size(), "Bug 1: Off-by-one error");
                    },
                    () -> {
                        List<Integer> testList = List.of(1, 2, 3);
                        assertThrows(NullPointerException.class,
                                () -> BasicDebuggingExample.processNumbers(testList),
                                "Bug 2: NullPointerException");
                    },
                    () -> {
                        int[] testArray = {1, 2, 3};
                        assertThrows(ArrayIndexOutOfBoundsException.class,
                                () -> BasicDebuggingExample.printArrayElements(testArray),
                                "Bug 3: ArrayIndexOutOfBoundsException");
                    }
            );
        }

        @Test
        @DisplayName("Document expected debugging breakpoint locations")
        @Tag("documentation")
        void documentBreakpointLocations() {
            // This test serves as documentation for instructors
            var breakpointGuide = """
                    Recommended breakpoints for debugging exercises:
                    1. Line in generateNumbers() - loop condition (i <= count + 1)
                    2. Line in processNumbers() - prefix.toUpperCase() call
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
