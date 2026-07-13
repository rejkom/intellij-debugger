package com.debugging.course;

/**
 * Debugging concurrency problems in IntelliJ IDEA.
 *
 * This file has TWO classic concurrency bugs you can demonstrate:
 *
 *   PART A - A DEADLOCK: two threads lock two objects in the opposite order
 *            and wait for each other forever. Use the "Threads" view and
 *            "Dump Threads" (the camera icon) to find the deadlock.
 *
 *   PART B - A RACE CONDITION: many threads increment a shared counter
 *            without synchronization, so the final value is too small.
 *            Use a CONDITIONAL breakpoint and "suspend thread" (not "all")
 *            to inspect the shared state safely.
 *
 * Tip: run runDeadlock() and runRaceCondition() separately.
 *      In main() only one is active at a time so the demo stays clear.
 */
public class ConcurrencyDebuggingExample {

    // Two locks used by the deadlock demo
    private final Object lockA = new Object();
    private final Object lockB = new Object();

    // Shared counter used by the race-condition demo
    private int counter = 0;

    static void main() throws InterruptedException {
        ConcurrencyDebuggingExample demo = new ConcurrencyDebuggingExample();

        System.out.println("=== PART B: Race condition demo ===");
        demo.runRaceCondition();

        // Uncomment the next two lines to demonstrate the DEADLOCK.
        // System.out.println("=== PART A: Deadlock demo (program will hang) ===");
        // demo.runDeadlock();
    }

    // ---------- PART A: DEADLOCK ----------

    void runDeadlock() throws InterruptedException {
        Thread t1 = new Thread(this::lockAThenB, "Thread-AB");
        Thread t2 = new Thread(this::lockBThenA, "Thread-BA");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    void lockAThenB() {
        synchronized (lockA) {                      // <-- breakpoint here
            sleepQuietly(50);
            synchronized (lockB) {                  // waits for Thread-BA forever
                System.out.println("Thread-AB got both locks");
            }
        }
    }

    void lockBThenA() {
        synchronized (lockB) {                      // <-- breakpoint here
            sleepQuietly(50);
            synchronized (lockA) {                  // waits for Thread-AB forever
                System.out.println("Thread-BA got both locks");
            }
        }
    }

    // ---------- PART B: RACE CONDITION ----------

    void runRaceCondition() throws InterruptedException {
        int threadCount = 10;
        int incrementsPerThread = 1000;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    increment();                    // BUG: not synchronized
                }
            }, "Worker-" + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        int expected = threadCount * incrementsPerThread; // 10000
        System.out.println("Expected counter: " + expected);
        System.out.println("Actual counter:   " + counter + "  (usually smaller - that is the bug)");
    }

    // BUG: counter++ is read-modify-write, not atomic.
    // Fix idea: make this 'synchronized', or use AtomicInteger.
    void increment() {
        counter++;                                  // <-- conditional breakpoint: counter > 9990
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

