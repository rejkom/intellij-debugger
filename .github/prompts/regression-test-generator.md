I am working on fixing a bug in my project. In our chat conversation, I will provide you with two things:

1. A `git diff` showing exactly what I modified to fix the bug.
2. The full context of the new, updated class for reference.

Your task is to analyze ONLY the provided `git diff` changes and the class context to create a robust JUnit 5 test
class.

Your generated test suite MUST include test cases for:

- Normal behavior (the happy path).
- Important edge cases.
- A specific regression test strictly targeting the bug I just fixed.

The regression test must be designed specifically so that it would have FAILED before my fix (on the old code), but
PASSES now (on the new code). Use descriptive test names indicating what is being tested.