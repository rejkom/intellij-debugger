package com.debugging.course;

import com.debugging.course.utils.Customer;
import com.debugging.course.utils.Order;
import com.debugging.course.utils.OrderStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Advanced Debugging Example Test Suite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AdvancedDebuggingExampleTest {

    private AdvancedDebuggingExample debuggingExample;
    private ByteArrayOutputStream outputStream;
    private static final PrintStream ORIGINAL_OUT = System.out;

    @BeforeEach
    void setUp() {
        debuggingExample = new AdvancedDebuggingExample();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(ORIGINAL_OUT);
    }

    @Nested
    @DisplayName("Discount Calculation Tests")
    @Tag("business-logic")
    class DiscountCalculationTests {

        @ParameterizedTest(name = "Order total {0} with {1} loyalty years -> {2}% discount")
        @CsvSource({
                "100.00, 1, 10.0",
                "100.00, 3, 10.0",
                "100.00, 6, 15.0",
                "100.00, 10, 15.0",
                "250.00, 8, 37.5",
                "300.00, 15, 45.0"
        })
        @DisplayName("Should calculate correct discounts based on loyalty years")
        void shouldCalculateCorrectDiscount(double total, int loyaltyYears, double expectedDiscount) {
            // Given
            Customer customer = new Customer("C001", "Test Customer",
                    "test@example.com", true, loyaltyYears);
            Order order = new Order("ORD-TEST", customer);
            order.setTotal(BigDecimal.valueOf(total));

            // When
            double actualDiscount = debuggingExample.calculateDiscount(order);

            // Then
            assertEquals(expectedDiscount, actualDiscount, 0.01,
                    () -> String.format("Discount for %d loyalty years should be %.2f",
                            loyaltyYears, expectedDiscount));
        }

        @Test
        @DisplayName("Should apply 10% base discount for premium customers")
        void shouldApplyBaseDiscountForPremiumCustomers() {
            // Given
            Customer customer = new Customer("C001", "New Premium",
                    "new@example.com", true, 2);
            Order order = new Order("ORD-001", customer);
            order.setTotal(BigDecimal.valueOf(200.0));

            // When
            double discount = debuggingExample.calculateDiscount(order);

            // Then
            assertEquals(20.0, discount, 0.01,
                    "Base discount should be 10% of 200.00 = 20.00");
        }

        @Test
        @DisplayName("Should apply 50% bonus for customers with more than 5 loyalty years")
        void shouldApplyLoyaltyBonus() {
            // Given
            Customer loyalCustomer = new Customer("C002", "Loyal Customer",
                    "loyal@example.com", true, 6);
            Order order = new Order("ORD-002", loyalCustomer);
            order.setTotal(BigDecimal.valueOf(100.0));

            // When
            double discount = debuggingExample.calculateDiscount(order);

            // Then
            double expectedDiscount = 10.0 * 1.5; // Base 10.0 * 1.5 bonus
            assertEquals(expectedDiscount, discount, 0.01,
                    "Loyalty bonus should multiply base discount by 1.5");
        }

        @Test
        @Tag("edge-cases")
        @DisplayName("Should never return negative discount")
        void shouldNeverReturnNegativeDiscount() {
            // Given - Edge case with minimal values
            Customer customer = new Customer("C003", "Edge Case",
                    "edge@example.com", true, 0);
            Order order = new Order("ORD-003", customer);
            order.setTotal(BigDecimal.valueOf(0.01));

            // When
            double discount = debuggingExample.calculateDiscount(order);

            // Then
            assertTrue(discount >= 0.0,
                    "Discount should never be negative due to Math.max(0.0, baseDiscount)");
        }

        static Stream<Arguments> discountScenarios() {
            return Stream.of(
                    arguments("Low value, low loyalty", 50.0, 1, 5.0),
                    arguments("Low value, high loyalty", 50.0, 10, 7.5),
                    arguments("High value, low loyalty", 500.0, 3, 50.0),
                    arguments("High value, high loyalty", 500.0, 20, 75.0),
                    arguments("Medium value, threshold loyalty", 200.0, 5, 20.0),
                    arguments("Medium value, above threshold", 200.0, 6, 30.0)
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("discountScenarios")
        @DisplayName("Should handle various discount scenarios correctly")
        void shouldHandleVariousScenarios(String scenario, double total,
                                          int loyaltyYears, double expectedDiscount) {
            // Given
            Customer customer = new Customer("C-SCENARIO", "Scenario Test",
                    "scenario@example.com", true, loyaltyYears);
            Order order = new Order("ORD-SCENARIO", customer);
            order.setTotal(BigDecimal.valueOf(total));

            // When
            double actualDiscount = debuggingExample.calculateDiscount(order);

            // Then
            assertEquals(expectedDiscount, actualDiscount, 0.01,
                    () -> String.format("%s: Expected discount %.2f but got %.2f",
                            scenario, expectedDiscount, actualDiscount));
        }
    }

    @Nested
    @DisplayName("Order Processing Tests")
    @Tag("integration")
    class OrderProcessingTests {

        @Test
        @DisplayName("Should process premium orders with discount")
        void shouldProcessPremiumOrdersWithDiscount() {
            // Given
            Customer premiumCustomer = new Customer("C001", "Premium User",
                    "premium@example.com", true, 8);
            Order order = new Order("ORD-001", premiumCustomer);
            order.setTotal(BigDecimal.valueOf(100.0));
            List<Order> orders = List.of(order);

            // When
            debuggingExample.processOrders(orders);

            // Then
            assertAll("Verify premium order processing",
                    () -> assertTrue(order.getDiscount().doubleValue() > 0,
                            "Premium customer should receive discount"),
                    () -> assertEquals(OrderStatus.CONFIRMED, order.getStatus(),
                            "Order status should be CONFIRMED after processing"),
                    () -> assertTrue(order.getFinalTotal().compareTo(order.getTotal()) < 0,
                            "Final total should be less than original total due to discount")
            );
        }

        @Test
        @DisplayName("Should process non-premium orders without discount")
        void shouldProcessNonPremiumOrdersWithoutDiscount() {
            // Given
            Customer regularCustomer = new Customer("C002", "Regular User",
                    "regular@example.com", false, 0);
            Order order = new Order("ORD-002", regularCustomer);
            order.setTotal(BigDecimal.valueOf(150.0));
            List<Order> orders = List.of(order);

            // When
            debuggingExample.processOrders(orders);

            // Then
            assertAll("Verify non-premium order processing",
                    () -> assertEquals(BigDecimal.ZERO, order.getDiscount(),
                            "Regular customer should not receive discount"),
                    () -> assertEquals(OrderStatus.CONFIRMED, order.getStatus(),
                            "Order status should be CONFIRMED"),
                    () -> assertEquals(order.getTotal(), order.getFinalTotal(),
                            "Final total should equal original total without discount")
            );
        }

        @Test
        @DisplayName("Should process multiple orders correctly")
        void shouldProcessMultipleOrders() {
            // Given
            List<Order> orders = debuggingExample.createTestOrders();
            long premiumOrdersCount = orders.stream()
                    .filter(o -> o.getCustomer().isPremium())
                    .count();

            // When
            debuggingExample.processOrders(orders);

            // Then
            assertAll("Verify multiple order processing",
                    () -> assertEquals(5, orders.size(),
                            "Should process all 5 test orders"),
                    () -> assertTrue(orders.stream()
                                    .allMatch(o -> o.getStatus() == OrderStatus.CONFIRMED),
                            "All orders should have CONFIRMED status"),
                    () -> assertEquals(4, premiumOrdersCount,
                            "Should have 4 premium customers in test data"),
                    () -> assertTrue(orders.stream()
                                    .filter(o -> o.getCustomer().isPremium())
                                    .allMatch(o -> o.getDiscount().doubleValue() > 0),
                            "All premium orders should have discount applied")
            );
        }

        @Test
        @DisplayName("Should produce correct output format")
        @Tag("output-verification")
        void shouldProduceCorrectOutputFormat() {
            // Given
            Customer customer = new Customer("C999", "Output Test",
                    "output@example.com", true, 10);
            Order order = new Order("ORD-999", customer);
            order.setTotal(BigDecimal.valueOf(100.0));

            debuggingExample.processOrders(List.of(order));

            // When
            String output = outputStream.toString();
            System.out.println("DEBUG OUTPUT:\n" + output);

            // Then
            assertAll("Verify output format",
                    () -> assertTrue(output.contains("ORD-999"),
                            "Output should contain order ID"),
                    () -> assertTrue(output.contains("Output Test"),
                            "Output should contain customer name"),
                    () -> assertTrue(output.contains("Total:"),
                            "Output should contain 'Total:'"),
                    () -> assertTrue(output.contains("Discount:"),
                            "Output should contain 'Discount:'"),
                    () -> assertTrue(output.contains("Final:"),
                            "Output should contain 'Final:'")
            );
        }


        @Nested
        @DisplayName("Test Data Creation Tests")
        @Tag("data-factory")
        class TestDataCreationTests {

            @Test
            @DisplayName("Should create exactly 5 test orders")
            void shouldCreateFiveTestOrders() {
                // When
                List<Order> orders = debuggingExample.createTestOrders();

                // Then
                assertEquals(5, orders.size(), "Should create exactly 5 test orders");
            }

            @Test
            @DisplayName("Test orders should have correct properties")
            void testOrdersShouldHaveCorrectProperties() {
                // When
                List<Order> orders = debuggingExample.createTestOrders();

                // Then
                assertAll("Verify test order properties",
                        () -> assertTrue(orders.stream()
                                        .allMatch(o -> o.getOrderId() != null && !o.getOrderId().isBlank()),
                                "All orders should have valid order IDs"),
                        () -> assertTrue(orders.stream()
                                        .allMatch(o -> o.getCustomer() != null),
                                "All orders should have customers"),
                        () -> assertTrue(orders.stream()
                                        .allMatch(o -> o.getTotal().compareTo(BigDecimal.ZERO) > 0),
                                "All orders should have positive totals"),
                        () -> assertTrue(orders.stream()
                                        .noneMatch(o -> o.getItems().isEmpty()),
                                "All orders should have items")
                );
            }

            @Test
            @DisplayName("Should create diverse loyalty year distribution")
            void shouldCreateDiverseLoyaltyDistribution() {
                // When
                List<Order> orders = debuggingExample.createTestOrders();

                // Then
                var loyaltyYears = orders.stream()
                        .map(o -> o.getCustomer().getLoyaltyYears())
                        .toList();

                assertAll("Verify loyalty year diversity",
                        () -> assertTrue(loyaltyYears.stream().anyMatch(y -> y <= 5),
                                "Should have customers with <= 5 loyalty years"),
                        () -> assertTrue(loyaltyYears.stream().anyMatch(y -> y > 5),
                                "Should have customers with > 5 loyalty years"),
                        () -> assertTrue(loyaltyYears.stream().distinct().count() > 1,
                                "Should have varied loyalty years for testing")
                );
            }
        }

        @Nested
        @DisplayName("Main Method Integration Tests")
        @Tag("integration")
        class MainMethodTests {

            @Test
            void mainMethodShouldExecuteSuccessfully() {
                // When & Then
                assertDoesNotThrow(AdvancedDebuggingExample::main,
                        "Main method should execute without throwing exceptions");
            }

            @Test
            @DisplayName("Main method should process all test orders")
            void mainMethodShouldProcessAllOrders() {
                // When
                AdvancedDebuggingExample.main();
                String output = outputStream.toString();

                // Then
                assertAll("Verify main method execution",
                        () -> assertTrue(output.contains("ORD-001"), "Should process order 1"),
                        () -> assertTrue(output.contains("ORD-002"), "Should process order 2"),
                        () -> assertTrue(output.contains("ORD-003"), "Should process order 3"),
                        () -> assertTrue(output.contains("ORD-004"), "Should process order 4"),
                        () -> assertTrue(output.contains("ORD-005"), "Should process order 5"));
            }
        }

        @Nested
        @DisplayName("Debugging Exercise Scenarios")
        @Tag("educational")
        class DebuggingExercisesTests {

            @Test
            @DisplayName("Document Evaluate Expression debugging scenarios")
            @Tag("documentation")
            void documentEvaluateExpressionScenarios() {
                var guide = """
                        Evaluate Expression Debugging Scenarios:
                        1. Set breakpoint in calculateDiscount()
                           - Evaluate: baseDiscount * 2.0 (test different multipliers)
                           - Evaluate: Math.min(50.0, baseDiscount) (test limits)
                        
                        2. Set breakpoint in processOrders()
                           - Evaluate: order.getCustomer().getLoyaltyYears() + 5
                           - Evaluate: order.getTotal().multiply(BigDecimal.valueOf(0.5))
                        
                        3. Set breakpoint in processOrder()
                           - Evaluate: String.format("Test: %.2f", order.getFinalTotal())
                        """;

                assertNotNull(guide, "Evaluation scenarios should be documented");
            }

            @Test
            @DisplayName("Document Force Return debugging scenarios")
            @Tag("documentation")
            void documentForceReturnScenarios() {
                var guide = """
                        Force Return Debugging Scenarios:
                        1. Force return in calculateDiscount():
                           - Return 50.0 to test maximum discount
                           - Return 0.0 to test no discount scenario
                        
                        2. Force return in processOrder():
                           - Return early to skip status update
                        """;

                assertNotNull(guide, "Force return scenarios should be documented");
            }
        }
    }
}
