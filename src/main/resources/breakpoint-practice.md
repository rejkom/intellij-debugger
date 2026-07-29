# Breakpoint Practice — Hands-On Challenges

**Companion file:** `BreakpointStrategiesExercise.java`
**Goal:** practice IntelliJ breakpoint features on real code. The program already
works — you are training the debugger, not fixing a bug.

> How to use: open `BreakpointStrategiesExercise.java`, run it once in Debug mode,
> then work through the challenges. Answers are at the bottom — try first, peek
> later.

---

## Quick facts about the data (so you can predict values)

- Orders are created as `new Order(i, 10.0 + i, flagged)` for `i = 0..999`.
- `flagged` is `true` when `i % 250 == 0`.
- So the flagged order ids are: **0, 250, 500, 750** (4 flagged orders).
- `processedCount` is incremented once per order in `processOne(...)`.

---

## Challenge 1 — Conditional breakpoint

**Task:** Stop the program only for the order with `id == 750`.

1. Put a breakpoint on the `total += processOne(order);` line in `processAll`.
2. Right-click it and add the condition: `order.id() == 750`.
3. Debug. It should stop exactly once.

**Question:** What is `order.amount()` when it stops?

## Challenge 2 — Hit count

**Task:** Make a breakpoint stop only every 100th time.

1. On the same line, open the breakpoint settings.
2. Set **Pass count / Hit count** to `100`.
3. Debug and note the `order.id()` each time it pauses.

**Question:** What are the first three `order.id()` values where it stops?

## Challenge 3 — Logging breakpoint (no stopping, no println)

**Task:** Log every order id + amount WITHOUT pausing and WITHOUT editing code.

1. Breakpoint on `return order.amount();` in `processOne`.
2. Uncheck **Suspend**.
3. Check **Evaluate and log** and enter:
   `"Order " + order.id() + " amount " + order.amount()`
4. Debug and watch the Console.

**Question:** How many log lines appear in total?

## Challenge 4 — Dependent breakpoint

**Task:** Activate a breakpoint only AFTER a flagged order is handled.

1. Breakpoint A: first line inside `handleFlaggedOrder`.
2. Breakpoint B: `return order.amount();` in `processOne`.
3. In B's settings, set **"Disabled until selected breakpoint is hit"** → choose A.

**Question:** For which order id does B fire for the FIRST time? (Hint: which is
the first flagged order that reaches `handleFlaggedOrder`?)

## Challenge 5 — Exception breakpoint

**Task:** Stop the moment an `IllegalStateException` is thrown.

The current data never throws, because every amount is positive. To see it fire:

1. Temporarily change one order to a negative amount, e.g. in `createOrders`
   add inside the `for` loop: `if (i == 500) { orders.add(new Order(i, -5.0, true)); continue; }`.
2. Add a **Java Exception Breakpoint** for `IllegalStateException`.
3. Debug.

**Question:** At which line does the debugger stop, and what is the exception
message?

## Challenge 6 — Field watchpoint

**Task:** Stop whenever `processedCount` changes.

1. Click the gutter next to the `private int processedCount = 0;` field.
2. Debug. It will pause on every change.
3. Optional: give the watchpoint a condition like `processedCount == 500`.

**Question:** With the condition `processedCount == 500`, what is the current
`order.id()` when it stops?

---

## Answer key (try before you peek!)

1. **`order.amount()` = 760.0** → `10.0 + 750`.
2. **id = 99, 199, 299** → it stops on the 100th, 200th, 300th processed order
   (ids are zero-based, so the 100th order has id 99).
3. **1000 log lines** → one per order, `i = 0..999`.
4. **id = 750** → flagged orders are 0, 250, 500, 750; the first one that passes
   the whole flagged branch and reaches `return` after A is hit is id 0 if A
   fires there first. NOTE: A fires for id 0 (first flagged), so B becomes active
   and fires on the NEXT hit of B, which is the same call's `return` for id 0.
   In practice: A is hit inside `handleFlaggedOrder(0)`, then B fires for id 0 on
   its `return`. So the FIRST fire of B is **id = 0**. (Great talking point: the
   dependency arms B during the same order.)
5. **Stops inside `handleFlaggedOrder`**, at the `throw new IllegalStateException`
   line, message: **"Negative amount for order 500"**.
6. **`order.id()` = 500** → the debugger pauses before modifying the field again, which happens exactly
   when the 501st order (id = 500, zero-based) enters the method.

> The full run processes all 1000 orders, so
> `processedCount` ends at **1000** and the printed total amount is **509500.0**
> (`sum of 10.0 + i for i = 0..999`). Good sanity-check :)

