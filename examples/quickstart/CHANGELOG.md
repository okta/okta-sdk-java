# Quickstart Example - Changes Log

## Changes Made

### User Lookup by Email - Fixed

**Issue**: The original code attempted to look up a user directly by email using:
```java
userApi.getUser(email, null, "false")
```

This approach doesn't work because `getUser()` expects a user ID, not an email address.

**Solution**: Changed to use `listUsers()` with a filter to search by email:
```java
List<User> usersByEmail = userApi.listUsers(null, null, "profile.email eq \"" + email + "\"", null, null, null, null, null, null);
if (!usersByEmail.isEmpty()) {
    println("User lookup by Email: " + Objects.requireNonNull(usersByEmail.get(0).getProfile()).getLogin());
}
```

**Why This Works**:
- The `listUsers()` method accepts a `filter` parameter (3rd parameter)
- The filter uses Okta's filter syntax: `profile.email eq "email@example.com"`
- This returns a list of users matching the filter
- We check if the list is not empty before accessing the first result

### User Listing by Status - Fixed

**Issue**: The original code used incorrect parameter positioning:
```java
List<User> users = userApi.listUsers(null, null, null, null, "status eq \"ACTIVE\"", null, null, null);
```

**Solution**: Corrected the filter parameter position (3rd position) and all 9 parameters:
```java
List<User> users = userApi.listUsers(null, null, "status eq \"ACTIVE\"", null, null, null, null, null, null);
```

## API Method Signature

The `listUsers()` method signature is:
```java
public List<User> listUsers(
    String contentType,     // 1st param: Content-Type header (usually null)
    String search,          // 2nd param: Search string
    String filter,          // 3rd param: Filter expression (used for email/status search)
    String q,               // 4th param: Query string
    String after,           // 5th param: Pagination cursor
    Integer limit,          // 6th param: Number of results per page
    String sortBy,          // 7th param: Sort field
    String sortOrder,       // 8th param: Sort direction (asc/desc)
    String expand           // 9th param: Expansion parameter
) throws ApiException
```

## Best Practices

1. **Always use filters for searching**: Use the `filter` parameter with proper Okta filter syntax
2. **Check for empty results**: Always verify the list is not empty before accessing elements
3. **Use proper null safety**: Use `Objects.requireNonNull()` or null checks when accessing nested properties
4. **Match method signatures**: Ensure all required parameters are provided, even if they are null

## Filter Syntax Examples

```java
// Search by email
"profile.email eq \"user@example.com\""

// Search by status
"status eq \"ACTIVE\""

// Search by first name
"profile.firstName eq \"John\""

// Combined filters (AND)
"status eq \"ACTIVE\" and profile.email eq \"user@example.com\""

// Combined filters (OR)
"profile.firstName eq \"John\" or profile.firstName eq \"Jane\""
```

## Running the Example

```bash
# Compile the example
mvn compile -pl examples/quickstart

# Run the example (ensure you have valid Okta credentials configured)
mvn exec:java -pl examples/quickstart -Dexec.mainClass="quickstart.Quickstart"
```

## Dependencies

This example requires:
- Okta SDK for Java v24.0.1 or later
- Java 8 or later
- Valid Okta API credentials (configured via environment variables or okta.yaml)
