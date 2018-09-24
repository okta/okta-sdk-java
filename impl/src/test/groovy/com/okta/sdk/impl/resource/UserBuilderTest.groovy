package com.okta.sdk.impl.resource

import com.okta.sdk.client.Client
import com.okta.sdk.resource.user.PasswordCredential
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserCredentials
import com.okta.sdk.resource.user.UserProfile
import org.mockito.ArgumentCaptor
import org.testng.annotations.Test

import static org.mockito.Mockito.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*


/**
 * Tests for {@link com.okta.sdk.resource.user.UserBuilder}.  NOTE: this class is heavily tested through ITs but it
 * would be nice to get more coverage with UTs.
 */
class UserBuilderTest {

    @Test
    void builderWithCredentialsTest() {

        def client = mock(Client)
        def userCapture = ArgumentCaptor.forClass(User)
        def profile = mock(UserProfile)
        def user = mock(User)
        def credentials = mock(UserCredentials)

        when(client.instantiate(User)).thenReturn(user)
        when(client.instantiate(UserProfile)).thenReturn(profile)
        when(user.getProfile()).thenReturn(profile)
        when(client.createUser(userCapture.capture(), nullable(Boolean), nullable(Boolean))).thenReturn(user)

        def userResult = new DefaultUserBuilder()
            .setFirstName("first")
            .setLastName("last")
            .setEmail("jcoder@example.com")
            .setCredentials(credentials)
            .buildAndCreate(client)

        assertThat userResult, sameInstance(user)
        def credentialsCapture = ArgumentCaptor.forClass(UserCredentials)
        verify(user).setCredentials(credentialsCapture.capture())
    }

    @Test
    void builderWithCredentialsAndPasswordTest() {

        def password = "aPassword".chars
        def client = mock(Client)
        def userCapture = ArgumentCaptor.forClass(User)
        def profile = mock(UserProfile)
        def user = mock(User)
        def credentials = mock(UserCredentials)
        def passwordCredential = mock(PasswordCredential)

        when(client.instantiate(User)).thenReturn(user)
        when(client.instantiate(UserProfile)).thenReturn(profile)
        when(client.instantiate(PasswordCredential)).thenReturn(passwordCredential)
        when(user.getProfile()).thenReturn(profile)
        when(client.createUser(userCapture.capture(), nullable(Boolean), nullable(Boolean))).thenReturn(user)
        when(user.getCredentials()).thenReturn(credentials)
        when(passwordCredential.setValue(password)).thenReturn(passwordCredential)

        def userResult = new DefaultUserBuilder()
            .setFirstName("first")
            .setLastName("last")
            .setEmail("jcoder@example.com")
            .setPassword(password)
            .setCredentials(credentials)
            .buildAndCreate(client)

        assertThat userResult, sameInstance(user)
        def credentialsCapture = ArgumentCaptor.forClass(UserCredentials)
        verify(user).setCredentials(credentialsCapture.capture())
        verify(credentials).setPassword(passwordCredential)
    }
}
