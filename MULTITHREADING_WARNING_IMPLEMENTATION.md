# Multi-Threading Warning Implementation

## Overview
This document describes the implementation of multi-threading warnings in the Okta Java SDK to help developers identify when they are incorrectly creating multiple `ApiClient` instances in multi-threaded applications.

## Problem Statement
The Okta Java SDK uses an internal cache manager that is shared across all `ApiClient` instances within the same JVM. Creating multiple instances in a multi-threaded application is **NOT SUPPORTED** and may lead to:
- Cache inconsistencies
- Memory leaks
- Sub-optimal caching behavior
- Unpredictable behavior in multi-threaded environments

## Solution Components

### 1. **MultiThreadingWarningUtil** (New Utility Class)
**Location:** `impl/src/main/java/com/okta/sdk/impl/util/MultiThreadingWarningUtil.java`

A utility class that tracks and warns about multiple `ApiClient` instance creation:
- Uses `AtomicInteger` to safely count instances across threads
- Logs informational message for the first instance
- Logs detailed warning banner for the second instance
- Logs simplified warnings for subsequent instances
- Provides methods for testing (`getInstanceCount()`, `resetInstanceCounter()`)

**Key Methods:**
- `recordInstanceCreation()` - Called when a new ApiClient is created
- `getInstanceCount()` - Returns the current count of instances
- `resetInstanceCounter()` - Resets the counter (for testing)

### 2. **Updated DefaultClientBuilder**
**Location:** `impl/src/main/java/com/okta/sdk/impl/client/DefaultClientBuilder.java`

Modified the `build()` method to call `MultiThreadingWarningUtil.recordInstanceCreation()` whenever a new `ApiClient` is built.

### 3. **Updated ApiClient Template**
**Location:** `api/src/main/resources/custom_templates/ApiClient.mustache`

Added a warning log in the constructor that alerts developers about the multi-threading limitation:
```java
log.warn("Creating ApiClient instance. Note: Multiple ApiClient instances in multi-threaded " +
         "applications are not supported. Use a single instance throughout your application lifecycle.");
```

### 4. **Enhanced Documentation**

#### **README.md**
Updated the "Thread Safety" section with:
- Prominent warning banner with visual indicators (⚠️, ❌, ✅)
- Clear bullet points explaining the issue
- Version information (warnings available as of 11.1.0)

#### **Clients.java**
Added comprehensive JavaDoc warnings in:
- Class-level documentation
- `builder()` method documentation

#### **ClientBuilder.java**
Added detailed JavaDoc to the `build()` method explaining the multi-threading restriction.

#### **DPoPInterceptor.java**
Added JavaDoc warning noting that while `ThreadLocal` is used for thread-safety, the SDK as a whole does not support multiple instances.

### 5. **Test Coverage**
**Location:** `impl/src/test/groovy/com/okta/sdk/impl/util/MultiThreadingWarningUtilTest.groovy`

Comprehensive test suite covering:
- Single instance creation
- Multiple instance creation
- Counter reset functionality
- Concurrent instance creation (10 threads)

All tests pass successfully.

## Warning Output Examples

### First Instance (INFO level):
```
ApiClient instance created. Remember: Use a single ApiClient instance throughout your application.
```

### Second Instance (WARN level with banner):
```
================================================================================
WARNING: Multiple ApiClient instances detected (2 instances created)
================================================================================
Creating multiple ApiClient instances in a multi-threaded application is NOT
SUPPORTED and may lead to:
  - Cache inconsistencies
  - Memory leaks
  - Sub-optimal performance

The SDK uses an internal cache manager that is shared across all ApiClient
instances within the same JVM. You MUST use a single ApiClient instance
throughout the entire lifecycle of your application.

For more information, see:
https://github.com/okta/okta-sdk-java/blob/master/README.md#thread-safety
================================================================================
```

### Subsequent Instances (WARN level):
```
Multiple ApiClient instances detected (3 total). This is NOT SUPPORTED in multi-threaded applications. See: https://github.com/okta/okta-sdk-java/blob/master/README.md#thread-safety
```

## Files Modified

1. **impl/src/main/java/com/okta/sdk/impl/util/MultiThreadingWarningUtil.java** (NEW)
2. **impl/src/test/groovy/com/okta/sdk/impl/util/MultiThreadingWarningUtilTest.groovy** (NEW)
3. **impl/src/main/java/com/okta/sdk/impl/client/DefaultClientBuilder.java**
4. **api/src/main/resources/custom_templates/ApiClient.mustache**
5. **api/src/main/java/com/okta/sdk/client/ClientBuilder.java**
6. **api/src/main/java/com/okta/sdk/client/Clients.java**
7. **impl/src/main/java/com/okta/sdk/impl/oauth2/DPoPInterceptor.java**
8. **README.md**

## Benefits

1. **Early Detection**: Developers are immediately alerted when they create multiple instances
2. **Clear Guidance**: Warnings include specific consequences and link to documentation
3. **Non-Breaking**: Warnings don't prevent the SDK from working, maintaining backward compatibility
4. **Testing Support**: Counter can be reset for testing purposes
5. **Thread-Safe**: Implementation uses `AtomicInteger` for accurate counting in concurrent environments

## Usage Recommendations

### ✅ CORRECT Usage:
```java
// Create ONE instance at application startup
ApiClient client = Clients.builder()
    .setOrgUrl("https://{yourOktaDomain}")
    .build();

// Reuse this instance throughout your application
UserApi userApi = new UserApi(client);
GroupApi groupApi = new GroupApi(client);
```

### ❌ INCORRECT Usage:
```java
// DON'T create multiple instances
ApiClient client1 = Clients.builder().build(); // First instance - OK
ApiClient client2 = Clients.builder().build(); // Second instance - WARNING!
ApiClient client3 = Clients.builder().build(); // Third instance - WARNING!
```

## Future Enhancements

Potential future improvements could include:
1. Optional strict mode that throws an exception on multiple instance creation
2. JMX/metrics integration for monitoring instance count
3. More detailed stack trace information to help identify where instances are being created
4. Configuration option to disable warnings for specific use cases

## Version Information

- **Introduced in:** Version 11.1.0
- **Status:** Active and recommended for all users
