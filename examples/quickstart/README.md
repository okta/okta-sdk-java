Basic CLI Example
=================

Run this example locally with the following Maven command:

``` bash
$ mvn exec:java
```

## Recent Changes

This example has been updated to demonstrate proper user lookup patterns:

1. **User lookup by email**: Now uses `listUsers()` with a filter instead of `getUser(email)`
2. **Correct API signatures**: All API calls now use the correct number of parameters

See [CHANGELOG.md](CHANGELOG.md) for detailed information about the changes and best practices for using the Okta SDK.

## Key Patterns Demonstrated

- Creating users with UserBuilder
- Creating groups with GroupBuilder
- Looking up users by ID (direct lookup)
- Searching users by email (using filters)
- Listing users by status (using filters)
- Updating user profiles
- Adding users to groups
- Deleting users and groups

## API Best Practices

When searching for users:
- Use `getUser(userId, ...)` for direct lookup by user ID
- Use `listUsers(..., filter, ...)` with Okta filter syntax for searching by email, status, or other attributes

Example filters:
```java
"profile.email eq \"user@example.com\""  // Search by email
"status eq \"ACTIVE\""                   // Filter by status
```
