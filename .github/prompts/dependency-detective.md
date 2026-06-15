I am troubleshooting a runtime issue in my Maven-based Java project (such as ClassNotFoundException,
NoClassDefFoundError, NoSuchMethodError, or unexpected behavior).

Please analyze the data provided at the very bottom of this message and deliver a structured response covering the
following points:

1. Root Cause Analysis: Identify the most likely cause of the problem and explain it in the context of the Java
   ClassLoader and Maven's dependency resolution mechanics.
2. Suspicious Dependencies: Clearly point out which specific libraries, versions, or transitive dependencies are
   clashing.
3. Verification Commands: Show how to further isolate or confirm this conflict using Maven tools (e.g., 'mvn dependency:
   tree' with useful flags like -Dverbose or -Dincludes) if more analysis is needed.
4. Concrete Fix: Provide a precise, copy-pasteable solution for my pom.xml (e.g.,
   using <dependencyManagement>, <exclusions>, or explicit version overrides).
5. Next Steps: Suggest follow-up verification checks (like specific Maven phases or runtime checks) to ensure the
   classpath is permanently clean.