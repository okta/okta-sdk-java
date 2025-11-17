# Multi-Threading Guide for Okta Java SDK

## Table of Contents
- [Overview](#overview)
- [The Memory Leak Problem](#the-memory-leak-problem)
- [The Solution](#the-solution)
- [Thread Safety Warnings](#thread-safety-warnings)
- [Recommended Usage Patterns](#recommended-usage-patterns)
- [Monitoring Thread Usage](#monitoring-thread-usage)
- [Migration Guide](#migration-guide)

## Overview

The Okta Java SDK uses thread-local state to support pagination and response tracking. While this design works well for single-threaded applications, it has important implications for multi-threaded environments.

**Key Points:**
- ✅ **Fixed in v14.0.0+**: Memory leak caused by thread churn
- ⚠️ **Important**: SDK is not designed for concurrent multi-threaded usage with shared `ApiClient` instances
- 📊 **New Feature**: Automatic detection and warnings for multi-threaded usage

## The Memory Leak Problem

### Original Implementation (Pre-v14.0.0)

The SDK originally used `ConcurrentHashMap` to store thread state:

```java
private Map<Long, Integer> lastStatusCodeByThread = new ConcurrentHashMap<>();
private Map<Long, Map<String, List<String>>> lastResponseHeadersByThread = new ConcurrentHashMap<>();
```

**The Issue:**
- Maps were keyed by `Thread.getId()`
- Entries were **never removed**, even when threads died
- In long-running applications (especially web servers), this caused unbounded memory growth

**Example Scenario:**
```
Web Server with Thread Pool:
- Request 1: Thread-1 makes API call → Entry added to map
- Request 2: Thread-2 makes API call → Entry added to map
- Request 3: Thread-3 makes API call → Entry added to map
- ... 10,000 requests later ...
- Map has 10,000+ entries
- None are ever removed
- Memory leak! 💥
```

### Why Pagination Depends on Thread State

The SDK's pagination implementation relies on the `Link` header from HTTP responses:

```java
// Simplified pagination loop
List<User> users = new ArrayList<>();
PagedList<User> page = api.listUsers();
users.addAll(page);

while (page.hasNext()) {
    // The SDK implicitly uses the Link header from the PREVIOUS response
    // stored in lastResponseHeadersByThread to make the next request
    page = api.listUsers(); 
    users.addAll(page);
}
```

This "magic" behavior creates a tight coupling between:
1. Thread state storage (the maps)
2. Pagination functionality

A previous attempt to "clean up" thread state reportedly broke pagination, proving this fragile dependency.

## The Solution

### ThreadLocal Implementation (v14.0.0+)

We replaced `ConcurrentHashMap` with `ThreadLocal`:

```java
private final ThreadLocal<Integer> lastStatusCode = new ThreadLocal<>();
private final ThreadLocal<Map<String, List<String>>> lastResponseHeaders = new ThreadLocal<>();
```

**Benefits:**
- ✅ Automatic cleanup when threads die (in spawn-and-kill scenarios)
- ✅ No explicit map management needed
- ✅ Thread-safe by design
- ✅ Maintains backward compatibility with pagination

**Thread Pool Caveat:**

In thread pool environments (the most common scenario for web apps):
- Thread pool threads live for the application's lifetime
- `ThreadLocal` values persist as long as the thread lives
- State from Request A can leak into Request B if both use the same pooled thread

**This is why we added multi-threading warnings!**

## Thread Safety Warnings

### Automatic Detection

The SDK now includes `MultiThreadingWarningUtil` that automatically detects and warns about:

1. **Multiple threads using the same `ApiClient`** (threshold: 3+ threads)
2. **Thread pool usage patterns** (detected by thread names)
3. **Excessive thread churn** (over 1,000 unique threads)

### Example Warning Output

```
================================================================================
OKTA SDK MULTI-THREADING WARNING
================================================================================
The Okta SDK has detected that 5 unique threads are accessing the same
ApiClient instance. The SDK uses thread-local state for pagination and is
NOT designed for concurrent, multi-threaded usage patterns.

POTENTIAL ISSUES:
  - Pagination may behave unexpectedly when multiple threads make requests
  - Thread state may interfere between concurrent operations
  - In thread pool environments, ThreadLocal state persists across requests

RECOMMENDATIONS:
  1. Use a separate ApiClient instance per thread (thread-local pattern)
  2. Synchronize access to a shared ApiClient instance (not recommended for
     high-concurrency scenarios due to performance impact)
  3. Avoid using the SDK's collection/pagination methods in multi-threaded
     contexts where threads share an ApiClient instance

For more information, see: https://github.com/okta/okta-sdk-java/issues/1637
================================================================================
```

## Recommended Usage Patterns

### 1. Thread-Local ApiClient (Best for Thread Pools)

**Use this pattern for:**
- Web applications (Spring Boot, Tomcat, Jetty)
- Async frameworks
- Any application using thread pools

```java
public class UserService {
    private static final ThreadLocal<ApiClient> CLIENT_HOLDER = 
        ThreadLocal.withInitial(() -> 
            Clients.builder()
                .setOrgUrl("https://dev-123456.okta.com")
                .setClientCredentials(new TokenClientCredentials("token"))
                .build()
        );
    
    public User getUser(String userId) {
        ApiClient client = CLIENT_HOLDER.get();
        return client.getUserApi().getUser(userId);
    }
}
```

**Pros:**
- ✅ Each thread gets its own `ApiClient` instance
- ✅ No state leakage between threads
- ✅ Minimal overhead (client created once per thread)

**Cons:**
- ⚠️ One HTTP client per thread (uses more resources)
- ⚠️ Must manage `ThreadLocal` cleanup if needed

### 2. Per-Request ApiClient (Safest)

**Use this pattern for:**
- Low-traffic applications
- Applications where simplicity > performance
- Testing

```java
public class UserService {
    public User getUser(String userId) {
        ApiClient client = Clients.builder()
            .setOrgUrl("https://dev-123456.okta.com")
            .setClientCredentials(new TokenClientCredentials("token"))
            .build();
        
        return client.getUserApi().getUser(userId);
    }
}
```

**Pros:**
- ✅ Completely thread-safe
- ✅ No state leakage possible
- ✅ Simplest to understand

**Cons:**
- ❌ Creates new client for every request (overhead)
- ❌ More resource usage (HTTP clients, connections)

### 3. Spring Request-Scoped Bean

**Use this pattern for:**
- Spring/Spring Boot applications
- Dependency injection preference

```java
@Configuration
public class OktaConfig {
    
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ApiClient oktaClient() {
        return Clients.builder()
            .setOrgUrl("https://dev-123456.okta.com")
            .setClientCredentials(new TokenClientCredentials("token"))
            .build();
    }
}

@Service
public class UserService {
    
    @Autowired
    private ApiClient oktaClient; // New instance per HTTP request
    
    public User getUser(String userId) {
        return oktaClient.getUserApi().getUser(userId);
    }
}
```

**Pros:**
- ✅ Automatic lifecycle management
- ✅ One instance per HTTP request
- ✅ Clean dependency injection

**Cons:**
- ⚠️ Only works in Spring
- ⚠️ Requires understanding of Spring scopes

### 4. Synchronized Access (Not Recommended)

**Only use this if:**
- You have very low concurrency
- You cannot change the architecture

```java
public class UserService {
    private final ApiClient client = Clients.builder()
        .setOrgUrl("https://dev-123456.okta.com")
        .setClientCredentials(new TokenClientCredentials("token"))
        .build();
    
    public synchronized User getUser(String userId) {
        return client.getUserApi().getUser(userId);
    }
}
```

**Pros:**
- ✅ Simple to implement
- ✅ One shared client instance

**Cons:**
- ❌ Severe performance bottleneck
- ❌ All requests serialize through one lock
- ❌ Not suitable for high-concurrency scenarios

## Monitoring Thread Usage

### Checking Thread Count Programmatically

```java
ApiClient client = Clients.builder()...build();

// ... use the client ...

int threadCount = client.getUniqueThreadCount();
if (threadCount > 1) {
    logger.warn("Multiple threads detected: {}", threadCount);
}
```

### Logging Configuration

The SDK logs warnings at `WARN` level via SLF4J. To see detailed thread tracking:

```properties
# logback.xml or similar
<logger name="com.okta.sdk.impl.util.MultiThreadingWarningUtil" level="DEBUG"/>
<logger name="com.okta.sdk.resource.client.ApiClient" level="DEBUG"/>
```

## Migration Guide

### If You're Using a Shared ApiClient

**Before (Risky):**
```java
public class Application {
    // ❌ Shared across threads
    private static final ApiClient CLIENT = Clients.builder()...build();
    
    public void handleRequest() {
        CLIENT.getUserApi().listUsers(); // Used by multiple threads
    }
}
```

**After (Safe - Thread-Local):**
```java
public class Application {
    // ✅ One per thread
    private static final ThreadLocal<ApiClient> CLIENT = 
        ThreadLocal.withInitial(() -> Clients.builder()...build());
    
    public void handleRequest() {
        CLIENT.get().getUserApi().listUsers(); // Each thread gets its own
    }
}
```

### If You're Creating Clients Frequently

You're already safe! Just be aware that v14.0.0+ will log warnings if the same client instance is used by multiple threads.

### Testing Your Application

1. **Run your application normally**
2. **Look for warnings in logs**:
   ```
   OKTA SDK MULTI-THREADING WARNING
   ```
3. **Check programmatically**:
   ```java
   if (client.getUniqueThreadCount() > 1) {
       // Multiple threads detected
   }
   ```
4. **Apply the appropriate pattern** from the [Recommended Usage Patterns](#recommended-usage-patterns) section

## FAQ

### Q: Will the SDK throw exceptions if I use it incorrectly?

**A:** No. The SDK will log warnings but will not prevent multi-threaded usage. However, you may experience unexpected pagination behavior.

### Q: Is there any performance impact from the thread tracking?

**A:** Minimal. The `MultiThreadingWarningUtil` uses concurrent data structures and atomic operations. The overhead is negligible compared to actual API calls.

### Q: Can I disable the warnings?

**A:** Yes, configure your logger:
```xml
<logger name="com.okta.sdk.impl.util.MultiThreadingWarningUtil" level="ERROR"/>
```

However, we strongly recommend investigating and fixing the underlying multi-threading issue instead.

### Q: What if I'm using virtual threads (Java 21+)?

**A:** Virtual threads are still threads! The same concerns apply:
- Each virtual thread gets its own `ThreadLocal` storage
- Use the thread-local pattern or per-request pattern
- Be cautious with shared `ApiClient` instances

### Q: Does this affect the async SDK?

**A:** This guide is for the synchronous SDK. The async/reactive SDK has different considerations.

## Related Issues

- [GitHub Issue #1637 - Memory Leak in Multi-threaded Environments](https://github.com/okta/okta-sdk-java/issues/1637)

## Further Reading

- [Java ThreadLocal Best Practices](https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html)
- [Thread Pool Memory Leaks](https://blogs.oracle.com/javamagazine/post/java-thread-pool-memory-leaks)
- [Spring Bean Scopes](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes)
