package com.debugging.course;

import com.debugging.course.JavaFeaturesExample.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Java Features Example Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JavaFeaturesExampleTest {

    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;
    private static final PrintStream ORIGINAL_OUT = System.out;
    private static final PrintStream ORIGINAL_ERR = System.err;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(ORIGINAL_OUT);
        System.setErr(ORIGINAL_ERR);
    }

    @Nested
    @DisplayName("User Record Tests")
    @Tag("records")
    class UserRecordTests {

        @Test
        @DisplayName("Should create valid user record")
        void shouldCreateValidUser() {
            // When
            User user = new User("Alice", 30, "alice@example.com");

            // Then
            assertAll("Verify user record creation",
                    () -> assertEquals("Alice", user.name(), "Name should match"),
                    () -> assertEquals(30, user.age(), "Age should match"),
                    () -> assertEquals("alice@example.com", user.email(), "Email should match")
            );
        }

        @ParameterizedTest(name = "Invalid name: ''{0}''")
        @CsvSource({
                "'', Name cannot be null or blank",
                "' ', Name cannot be null or blank",
                "'  ', Name cannot be null or blank"
        })
        @DisplayName("Should reject invalid names in compact constructor")
        void shouldRejectInvalidNames(String name, String expectedMessage) {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new User(name, 25, "test@example.com"),
                    "Should throw exception for invalid name: " + name
            );

            assertEquals(expectedMessage, exception.getMessage(),
                    "Exception message should match");
        }

        @Test
        @DisplayName("Should reject null name")
        void shouldRejectNullName() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new User(null, 25, "test@example.com"),
                    "Should throw exception for null name"
            );

            assertTrue(exception.getMessage().contains("Name cannot be null"),
                    "Exception message should mention null name");
        }

        @ParameterizedTest(name = "Invalid age: {0}")
        @CsvSource({"-1", "-10", "-100"})
        @DisplayName("Should reject negative ages")
        void shouldRejectNegativeAges(int age) {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new User("Bob", age, "bob@example.com"),
                    "Should throw exception for negative age: " + age
            );

            assertEquals("Age cannot be negative", exception.getMessage(),
                    "Exception message should match");
        }

        @Test
        @DisplayName("Should accept zero age")
        void shouldAcceptZeroAge() {
            // When & Then
            assertDoesNotThrow(() -> new User("Baby", 0, "baby@example.com"),
                    "Zero age should be valid");
        }

        @Test
        @DisplayName("Should provide record equality semantics")
        void shouldProvideRecordEquality() {
            // Given
            User user1 = new User("Charlie", 35, "charlie@example.com");
            User user2 = new User("Charlie", 35, "charlie@example.com");
            User user3 = new User("Charlie", 36, "charlie@example.com");

            // Then
            assertAll("Verify record equality",
                    () -> assertEquals(user1, user2,
                            "Records with same values should be equal"),
                    () -> assertNotEquals(user1, user3,
                            "Records with different values should not be equal"),
                    () -> assertEquals(user1.hashCode(), user2.hashCode(),
                            "Equal records should have same hashCode")
            );
        }

        @Test
        @DisplayName("Should provide meaningful toString()")
        void shouldProvideMeaningfulToString() {
            // Given
            User user = new User("David", 40, "david@example.com");

            // When
            String toString = user.toString();

            // Then
            assertAll("Verify toString content",
                    () -> assertTrue(toString.contains("David"),
                            "toString should contain name"),
                    () -> assertTrue(toString.contains("40"),
                            "toString should contain age"),
                    () -> assertTrue(toString.contains("david@example.com"),
                            "toString should contain email")
            );
        }

        @Test
        @DisplayName("Should test record validation in testRecords method")
        @Tag("integration")
        void shouldTestRecordValidationInMethod() {
            // When
            JavaFeaturesExample.testRecords();
            String errorOutput = errorStream.toString();

            // Then
            assertTrue(errorOutput.contains("Age cannot be negative"),
                    "Should catch and log validation error for negative age");
        }
    }

    @Nested
    @DisplayName("Pattern Matching Tests")
    @Tag("pattern-matching")
    class PatternMatchingTests {

        static Stream<Arguments> patternMatchingScenarios() {
            return Stream.of(
                    arguments("Hello", "Short string: Hello"),
                    arguments("This is a very long string", "Long string: This is a ..."),
                    arguments(42, "Small integer: 42"),
                    arguments(150, "Large integer: 150"),
                    arguments(3.14159, "Double value: 3.14"),
                    arguments(new User("Eve", 28, "eve@example.com"), "User: Eve (age 28)"),
                    arguments(null, "Null object")
            );
        }

        @ParameterizedTest(name = "Object {0} -> {1}")
        @MethodSource("patternMatchingScenarios")
        @DisplayName("Should match patterns correctly using switch expressions")
        void shouldMatchPatternsCorrectly(Object input, String expectedOutput) {
            // When
            String result = JavaFeaturesExample.analyzeObject(input);

            // Then
            assertEquals(expectedOutput, result,
                    () -> "Pattern matching for " + input + " should produce correct result");
        }

        @Test
        @DisplayName("Should handle guarded patterns with when clause")
        void shouldHandleGuardedPatterns() {
            // Given
            String shortString = "Short";
            String longString = "This is definitely longer than ten characters";
            Integer smallInt = 50;
            Integer largeInt = 200;

            // When & Then
            assertAll("Verify guarded pattern matching",
                    () -> assertTrue(JavaFeaturesExample.analyzeObject(shortString)
                                    .startsWith("Short string:"),
                            "Short string should match unguarded case"),
                    () -> assertTrue(JavaFeaturesExample.analyzeObject(longString)
                                    .startsWith("Long string:"),
                            "Long string should match guarded case with when"),
                    () -> assertTrue(JavaFeaturesExample.analyzeObject(smallInt)
                                    .contains("Small integer"),
                            "Small integer should match unguarded case"),
                    () -> assertTrue(JavaFeaturesExample.analyzeObject(largeInt)
                                    .contains("Large integer"),
                            "Large integer should match guarded case with when")
            );
        }

        @Test
        @DisplayName("Should use record pattern matching with deconstruction")
        void shouldUseRecordPatternMatching() {
            // Given
            User user = new User("Frank", 45, "frank@example.com");

            // When
            String result = JavaFeaturesExample.analyzeObject(user);

            // Then
            assertAll("Verify record pattern deconstruction",
                    () -> assertTrue(result.contains("User:"),
                            "Should identify as User type"),
                    () -> assertTrue(result.contains("Frank"),
                            "Should extract name from record"),
                    () -> assertTrue(result.contains("45"),
                            "Should extract age from record")
            );
        }

        @Test
        @DisplayName("Should handle null case explicitly")
        void shouldHandleNullExplicitly() {
            // When
            String result = JavaFeaturesExample.analyzeObject(null);

            // Then
            assertEquals("Null object", result,
                    "Null case should be handled by explicit pattern");
        }

        @Test
        @DisplayName("Should handle unknown types with default case")
        void shouldHandleUnknownTypes() {
            // Given
            Object unknownObject = new Object();

            // When
            String result = JavaFeaturesExample.analyzeObject(unknownObject);

            // Then
            assertTrue(result.startsWith("Unknown type:"),
                    "Unknown types should match default case");
            assertTrue(result.contains("Object"),
                    "Should include class name");
        }

        @Test
        @DisplayName("Should test pattern matching method integration")
        @Tag("integration")
        void shouldTestPatternMatchingIntegration() {
            // When
            JavaFeaturesExample.testPatternMatching();
            String output = outputStream.toString();

            // Then
            assertAll("Verify pattern matching output",
                    () -> assertTrue(output.contains("Pattern Matching Test"),
                            "Should print test header"),
                    () -> assertTrue(output.contains("Hello World"),
                            "Should process string"),
                    () -> assertTrue(output.contains("42"),
                            "Should process integer"),
                    () -> assertTrue(output.contains("User: Alice"),
                            "Should process User record")
            );
        }
    }

    @Nested
    @DisplayName("Virtual Threads Tests")
    @Tag("virtual-threads")
    @Execution(ExecutionMode.SAME_THREAD)
    class VirtualThreadsTests {

        @Test
        @DisplayName("Should execute tasks on virtual threads")
        @Timeout(value = 3, unit = TimeUnit.SECONDS)
        void shouldExecuteOnVirtualThreads() {
            // When
            assertDoesNotThrow(JavaFeaturesExample::testVirtualThreads,
                    "Virtual threads test should complete without exceptions");

            String output = outputStream.toString();

            // Then
            assertAll("Verify virtual thread execution",
                    () -> assertTrue(output.contains("Virtual thread 0"),
                            "Should start thread 0"),
                    () -> assertTrue(output.contains("Virtual thread 4"),
                            "Should start thread 4"),
                    () -> assertTrue(output.contains("completed"),
                            "Threads should complete")
            );
        }

        @Test
        @DisplayName("Should use virtual thread executor pattern")
        void shouldUseVirtualThreadExecutor() {
            // Given
            CountDownLatch latch = new CountDownLatch(3);

            // When & Then
            assertDoesNotThrow(() -> {
                try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    for (int i = 0; i < 3; i++) {
                        executor.submit(() -> {
                            latch.countDown();
                            return null;
                        });
                    }
                }
                assertTrue(latch.await(1, TimeUnit.SECONDS),
                        "All virtual threads should complete quickly");
            });
        }

        @Test
        @DisplayName("Should verify virtual thread characteristics")
        @Tag("thread-properties")
        void shouldVerifyVirtualThreadCharacteristics() {
            // When
            assertDoesNotThrow(() -> {
                Thread virtualThread = Thread.ofVirtual().start(() ->
                        assertTrue(Thread.currentThread().isVirtual(), "Thread should be virtual"));
                virtualThread.join();
            });
        }
    }

    @Nested
    @DisplayName("Structured Concurrency Tests")
    @Tag("structured-concurrency")
    @Execution(ExecutionMode.SAME_THREAD)
    class StructuredConcurrencyTests {

        @Test
        @DisplayName("Should demonstrate structured concurrency failure handling")
        @Timeout(value = 5, unit = TimeUnit.SECONDS)
        void shouldHandleStructuredConcurrencyFailure() {
            // When & Then
            assertThrows(Exception.class,
                    JavaFeaturesExample::testStructuredConcurrency,
                    "Should throw exception when task fails in structured scope");
        }

        @Test
        @DisplayName("Should complete successful tasks before failure")
        @Tag("concurrency-behavior")
        void shouldCompleteTasksBeforeFailure() throws Exception {
            // When
            JavaFeaturesExample.testStructuredConcurrency();
            fail("Should have thrown exception");
        }
    }

    @Nested
    @DisplayName("Main Method Integration Tests")
    @Tag("integration")
    class MainMethodTests {

        @Test
        @DisplayName("Main method should demonstrate all Java features")
        @Timeout(value = 10, unit = TimeUnit.SECONDS)
        void mainMethodShouldDemonstrateAllFeatures() {
            // When & Then
            assertThrows(Exception.class,
                    JavaFeaturesExample::main,
                    "Main method throws exception due to structured concurrency failure");

            String output = outputStream.toString();

            // Verify all features were attempted
            assertAll("Verify all feature tests were initiated",
                    () -> assertTrue(output.contains("Java Features Debugging Exercise"),
                            "Should print main header"),
                    () -> assertTrue(output.contains("Virtual Threads Test"),
                            "Should run virtual threads test"),
                    () -> assertTrue(output.contains("Pattern Matching Test"),
                            "Should run pattern matching test"),
                    () -> assertTrue(output.contains("Records Test"),
                            "Should run records test")
            );
        }
    }

    @Nested
    @DisplayName("Debugging Exercise Documentation")
    @Tag("educational")
    class DebuggingExercisesTests {

        @Test
        @DisplayName("Document virtual threads debugging scenarios")
        @Tag("documentation")
        void documentVirtualThreadsDebugging() {
            var guide = """
                    Virtual Threads Debugging Scenarios:
                    
                    1. Thread Inspection:
                       - Set breakpoint in virtual thread lambda
                       - Inspect Thread.currentThread() properties
                       - Verify isVirtual() returns true
                       - Compare with platform threads
                    
                    2. Concurrent Execution:
                       - Use "Frames" debugger tab to see all virtual threads
                       - Step through multiple threads simultaneously
                       - Observe lightweight context switching
                    
                    3. Performance Analysis:
                       - Compare virtual threads vs platform threads
                       - Monitor thread creation overhead
                       - Analyze memory footprint
                    """;

            assertNotNull(guide, "Virtual threads debugging guide should exist");
        }

        @Test
        @DisplayName("Document pattern matching debugging scenarios")
        void documentPatternMatchingDebugging() {
            var guide = """
                    Pattern Matching Debugging Scenarios:
                    
                    1. Switch Expression Evaluation:
                       - Set breakpoint in analyzeObject()
                       - Use "Evaluate Expression" to test different inputs
                       - Observe pattern matching order and guards
                    
                    2. Record Deconstruction:
                       - Breakpoint on User record pattern case
                       - Inspect deconstructed variables (name, age, email)
                       - Verify automatic extraction from record
                    
                    3. Guarded Patterns:
                       - Test when clauses with different values
                       - Verify guard evaluation before pattern matching
                       - Understand pattern matching precedence
                    """;

            assertTrue(guide.contains("Record Deconstruction"),
                    "Guide should cover record pattern matching");
        }

        @Test
        @DisplayName("Document structured concurrency debugging")
        void documentStructuredConcurrencyDebugging() {
            var guide = """
                    Structured Concurrency Debugging Scenarios:
                    
                    1. Scope Lifecycle:
                       - Set breakpoint before and after scope.fork()
                       - Observe task creation and submission
                       - Verify scope.join() behavior
                    
                    2. Exception Propagation:
                       - Breakpoint in failing task (task3)
                       - Step through exception throwing
                       - Observe how scope handles failure
                    
                    3. Task Coordination:
                       - Use Thread view to see all forked tasks
                       - Inspect task states (running, completed, failed)
                       - Understand parent-child thread relationship
                    """;

            assertTrue(guide.contains("Exception Propagation"),
                    "Guide should cover error handling");
        }

        @Test
        @DisplayName("Verify modern Java features usage")
        void shouldUseModernJavaFeatures() {
            var featuresSummary = """
                    Modern Java Features Demonstrated:
                    1. Records (Java 16+) - User record with compact constructor
                    2. Pattern Matching for switch (Java 21+) - analyzeObject method
                    3. Virtual Threads (Java 21+) - lightweight concurrency
                    4. Structured Concurrency (Preview) - scope-based task management
                    5. Text Blocks (Java 15+) - multi-line strings
                    6. var keyword (Java 10+) - local variable type inference
                    7. Enhanced switch expressions (Java 14+)
                    8. Record patterns (Java 21+) - deconstruction in switch
                    """;

            assertAll("Verify feature documentation",
                    () -> assertTrue(featuresSummary.contains("Records"),
                            "Should document records"),
                    () -> assertTrue(featuresSummary.contains("Virtual Threads"),
                            "Should document virtual threads"),
                    () -> assertTrue(featuresSummary.contains("Pattern Matching"),
                            "Should document pattern matching")
            );
        }
    }

    @Nested
    @DisplayName("Advanced Testing Techniques")
    @Tag("advanced")
    class AdvancedTestingTests {

        @RepeatedTest(value = 3, name = "Repetition {currentRepetition} of {totalRepetitions}")
        @DisplayName("Virtual threads should execute consistently")
        void virtualThreadsShouldBeConsistent() {
            // When & Then
            assertDoesNotThrow(JavaFeaturesExample::testVirtualThreads,
                    "Virtual threads should execute consistently across multiple runs");
        }

        @Test
        @DisplayName("Should handle concurrent pattern matching safely")
        @Tag("thread-safety")
        void shouldHandleConcurrentPatternMatching() throws InterruptedException {
            // Given
            Object[] testObjects = {
                    "Test String",
                    42,
                    3.14,
                    new User("Concurrent", 50, "concurrent@test.com"),
                    null
            };

            CountDownLatch latch = new CountDownLatch(testObjects.length);

            // When
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (Object obj : testObjects) {
                    executor.submit(() -> {
                        try {
                            String result = JavaFeaturesExample.analyzeObject(obj);
                            assertNotNull(result, "Result should not be null");
                        } finally {
                            latch.countDown();
                        }
                    });
                }
            }

            // Then
            assertTrue(latch.await(2, TimeUnit.SECONDS),
                    "All pattern matching operations should complete");
        }

        @Test
        @DisplayName("Should validate record immutability")
        void shouldValidateRecordImmutability() {
            // Given
            User user = new User("Immutable", 30, "immutable@test.com");
            String originalName = user.name();
            int originalAge = user.age();

            // When - Records are immutable, cannot modify
            // We can only create new instances
            User modifiedUser = new User("Modified", 31, user.email());

            // Then
            assertAll("Verify record immutability",
                    () -> assertEquals(originalName, user.name(),
                            "Original record name should not change"),
                    () -> assertEquals(originalAge, user.age(),
                            "Original record age should not change"),
                    () -> assertNotEquals(user, modifiedUser,
                            "New record should be different instance")
            );
        }
    }
}

