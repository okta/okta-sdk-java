package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.usergroups.UserGroup;
import com.okta.sdk.models.users.ChangePasswordRequest;
import com.okta.sdk.models.users.ChangeRecoveryQuestionRequest;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.Password;
import com.okta.sdk.models.users.RecoveryQuestion;
import com.okta.sdk.models.users.ResetPasswordToken;
import com.okta.sdk.models.users.TempPassword;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApiClient extends JsonApiClient {

    /**
     * Class constructor specifying which configuration to use.
     *
     * @param config  The Api client configuration to be used.
     */
    public UserApiClient(ApiClientConfiguration config) {
        super(config);
    }

    // List users.

    /**
     * Get all users in Okta.
     *
     * @return List<User>   Users containing matching results.
     * @throws IOException  If an input or output exception occurred.
     */
    public List<User> getUsers() throws IOException {
        return getUsersWithLimit(Utils.getDefaultResultsLimit());
    }

    /**
     * Search for Okta users that have firstName, lastName or email starting with {@code query}.
     *
     * @param  query        The to be searched for
     * @return List<Users>  Users containing matching results.
     * @throws IOException  If an input or output exception occurred.
     */
    public List<User> getUsersWithQuery(String query) throws IOException {
        return get(getEncodedPath("?" + SEARCH_QUERY + "=%s", query), new TypeReference<List<User>>() {
        });
    }

    /**
     * Return all users in Okta with an upper limit of the number of results.
     *
     * @return List<Users>  Users containing matching results.
     * @throws IOException  If an input or output exception occurred.
     */
    public List<User> getUsersWithLimit(int limit) throws IOException {
        return get(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)), new TypeReference<List<User>>() {
        });
    }

    /**
     * Return all users in Okta that match the given SCIM filter. It is possible to filter on
     * id, status, activated, lastUpdated, profile.firstName, profile.lastName, profile.login and profile.email.
     *
     * @param  filterBuilder  The filter that's being used to match users.
     * @return List<User>    Users that match the given {@code filter}.
     * @throws IOException   If an input or output exception occurred.
     * @see                  <a href="http://developer.okta.com/docs/api/resources/users.html#filters">Filters</a>
     */
    public List<User> getUsersWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<User>>() {
        });
    }

    /**
     * Return all users in Okta that match the given SCIM filter. It is possible to filter on all user profile attributes,
     * all custom user profile attributes as well as the following attributes: id, status, created, activated,
     * statusChanged and lastUpdated. Note that the results might not yet be up to date, as the most up to date data
     * can be delayed up to a few seconds, so use for convenience.
     *
     * @param  filterBuilder  The filter that's being used to match users.
     * @return List<User>     Users that match the given {@code filter}.
     * @throws IOException    If an input or output exception occurred.
     * @see                   <a href="http://developer.okta.com/docs/api/resources/users.html#filters">Filters</a>
     */
    public List<User> getUsersWithAdvancedSearch(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + SEARCH + "=%s", filterBuilder.toString()), new TypeReference<List<User>>() {
        });
    }

    /**
     * Get users by URL.
     *
     * @param  url          The URL to get users from.
     * @return List<User>   Users that is fetched from the {@code url}.
     * @throws IOException  If an input or output exception occurred.
     */
    public List<User> getUsersByUrl(String url) throws IOException {
        return get(url, new TypeReference<List<User>>() {
        });
    }

    /**
     * Get the current user.
     *
     * @return User         My user.
     * @throws IOException  If an input or output exception occurred.
     */
    public User getMyUser() throws IOException {
        return get(getEncodedPath("/me"), new TypeReference<User>() {
        });
    }

    /**
     * Get a user based on the {@code userId}.
     *
     * @param  userId       The id of the returned user.
     * @return User         The user with id equal to {@code userId}.
     * @throws IOException  If an input or output exception occurred.
     */
    public User getUser(String userId) throws IOException {
        return get(getEncodedPath("/%s", userId), new TypeReference<User>() {
        });
    }

    // Create users.

    /**
     * Create user based on the {@code user}.
     *
     * @param  user         The user to be created.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(User user) throws IOException {
        return post(getEncodedPath("/"), user, new TypeReference<User>() {
        });
    }

    /**
     * Create and activate user based on the {@code user} and {@code activate}.
     *
     * @param  user         The user to be created.
     * @param  activate     Determines whether to activate the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(User user, boolean activate) throws IOException {
        return post(getEncodedPath("?activate=%s", String.valueOf(activate)), user, new TypeReference<User>() {
        });
    }

    /**
     * Create a user based on {@code firstName}, {@code lastName}, {@code login}, and {@code email}.
     *
     * @param  firstName    The first name of the user.
     * @param  lastName     The last name of the user.
     * @param  login        The login of the user.
     * @param  email        The email of the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(String firstName, String lastName, String login, String email) throws IOException {
        return createUser(firstName, lastName, login, email, null);
    }

    /**
     * Create a user based on {@code firstName}, {@code lastName}, {@code login}, {@code email} and {@code secondEmail}.
     *
     * @param  firstName    The first name of the user.
     * @param  lastName     The last name of the user.
     * @param  login        The login of the user.
     * @param  email        The email of the user.
     * @param  secondEmail  The secondary email of the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(String firstName, String lastName, String login, String email, String secondEmail) throws IOException {
        return createUser(firstName, lastName, login, email, secondEmail, null);
    }

    /**
     * Create a user based on {@code firstName}, {@code lastName}, {@code login}, {@code email}, {@code secondEmail}
     * and {@code mobilePhone}.
     *
     * @param  firstName    The first name of the user.
     * @param  lastName     The last name of the user.
     * @param  login        The login of the user.
     * @param  email        The email of the user.
     * @param  secondEmail  The secondary email of the user.
     * @param  mobilePhone  The mobile phone number of the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(String firstName, String lastName, String login, String email, String secondEmail, String mobilePhone) throws IOException {
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setLogin(login);
        userProfile.setEmail(email);
        userProfile.setSecondEmail(secondEmail);
        userProfile.setMobilePhone(mobilePhone);

        User user = new User();
        user.setProfile(userProfile);

        return createUser(user);
    }

    /**
     * Create and activate a user based on {@code firstName}, {@code lastName}, {@code login}, {@code email}
     * and {@code activate}.
     *
     * @param  firstName    The first name of the user.
     * @param  lastName     The last name of the user.
     * @param  login        The login of the user.
     * @param  email        The email of the user.
     * @param  activate     Determines whether to activate the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(String firstName, String lastName, String login, String email, boolean activate) throws IOException {
        return createUser(firstName, lastName, login, email, null, null, activate);
    }

    /**
     * Create and activate a user based on {@code firstName}, {@code lastName}, {@code login}, {@code email},
     * {@code secondEmail} and {@code activate}.
     *
     * @param  firstName    The first name of the user.
     * @param  lastName     The last name of the user.
     * @param  login        The login of the user.
     * @param  email        The email of the user.
     * @param  secondEmail  The secondary email of the user.
     * @param  activate     Determines whether to activate the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(String firstName, String lastName, String login, String email, String secondEmail, boolean activate) throws IOException {
        return createUser(firstName, lastName, login, email, secondEmail, null, activate);
    }

    /**
     * Create and activate a user based on {@code firstName}, {@code lastName}, {@code login}, {@code email},
     * {@code secondEmail}, {@code mobilePhone} and {@code activate}.
     *
     * @param  firstName    The first name of the user.
     * @param  lastName     The last name of the user.
     * @param  login        The login of the user.
     * @param  email        The email of the user.
     * @param  secondEmail  The secondary email of the user.
     * @param  mobilePhone  The mobile phone number of the user.
     * @param  activate     Determines whether to activate the user.
     * @return User         The user that's created.
     * @throws IOException  If an input or output exception occurred.
     */
    public User createUser(String firstName, String lastName, String login, String email, String secondEmail, String mobilePhone, boolean activate) throws IOException {
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setLogin(login);
        userProfile.setEmail(email);
        userProfile.setMobilePhone(mobilePhone);
        userProfile.setSecondEmail(secondEmail);

        User user = new User();
        user.setProfile(userProfile);

        return createUser(user, activate);
    }

    // Get user groups.

    /**
     * Get all groups for a user.
     *
     * @param  userId           The id of the user to get groups for.
     * @return List<UserGroup>  The groups the user with id equal to {@code userId} is member of.
     * @throws IOException      If an input or output exception occurred.
     */
    public List<UserGroup> getUserGroups(String userId) throws IOException  {
        return get(getEncodedPath("/%s/groups", userId), new TypeReference<List<UserGroup>>() {
        });
    }

    // Update users.

    /**
     * Update a user with parameters.
     *
     * @param  params       The user parameters to be updated.
     * @return User         The updated user.
     * @throws IOException  If an input or output exception occurred.
     */
    public User updateUser(User params) throws IOException {
        return put(getEncodedPath("/%s", params.getId()), params, new TypeReference<User>() { });
    }

    /**
     * Update a user with parameters.
     *
     * @param  userId       The id of the user to be updated.
     * @param  params       The user parameters to be updated.
     * @return User         The updated user.
     * @throws IOException  If an input or output exception occurred.
     */
    public User updateUser(String userId, User params) throws IOException {
        return put(getEncodedPath("/%s", userId), params, new TypeReference<User>() { });
    }

    // Delete users.

    /**
     * Delete a user.
     *
     * @param  userId       The id of the user to be deleted.
     * @throws IOException  If an input or output exception occurred.
     */
    public void deleteUser(String userId) throws IOException {
        delete(getEncodedPath("/%s", userId));
    }

    // Activate users.

    /**
     * Activate user.
     *
     * @param  userId       The id of the user to be activated.
     * @return User         The activated user.
     * @throws IOException  If an input or output exception occurred.
     */
    public Map activateUser(String userId) throws IOException {
        return activateUser(userId, null);
    }

    /**
     * Activate user.
     *
     * @param  userId       The id of the user to be activated.
     * @param  sendEmail    Determines whether to send an activate email.
     * @return User         The activated user.
     * @throws IOException  If an input or output exception occurred.
     */
    public Map activateUser(String userId, Boolean sendEmail) throws IOException {
        if (sendEmail != null && sendEmail) {
            return post(getEncodedPath("/%s/lifecycle/activate", userId), null, new TypeReference<Map>() { });
        }
        else {
            return post(getEncodedPath("/%s/lifecycle/activate?sendEmail=false", userId), null, new TypeReference<Map>() { });
        }
    }

    // Users lifecycle.

    /**
     * Deactivate user.
     *
     * @param  userId       The id of the user to be deactivated.
     * @return User         The deactivated user.
     * @throws IOException  If an input or output exception occurred.
     */
    public Map deactivateUser(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/deactivate", userId), null, new TypeReference<Map>() { });
    }

    /**
     * Unlock user.
     *
     * @param  userId       The id of the user to be unlocked.
     * @return User         The unlocked user.
     * @throws IOException  If an input or output exception occurred.
     */
    public Map unlockUser(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/unlock", userId), null, new TypeReference<Map>() {
        });
    }

    /**
     * Expire password for a user.
     *
     * @param  userId       The id of the user to expire password for.
     * @return User         The user that has gotten it's password expired.
     * @throws IOException  If an input or output exception occurred.
     */
    public User expirePassword(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/expire_password", userId), null,
                new TypeReference<User>() { });
    }

    /**
     * Expire password for a user.
     *
     * @param  userId       The id of the user to expire password for.
     * @param  tempPassword The new temporary password for the user.
     * @return User         The user that has gotten it's password expired.
     * @throws IOException  If an input or output exception occurred.
     */
    public TempPassword expirePassword(String userId, boolean tempPassword) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tempPassword", String.valueOf(tempPassword));
        return post(getEncodedPathWithQueryParams("/%s/lifecycle/expire_password", params, userId), null,
                new TypeReference<TempPassword>() { });
    }

    /**
     * Generates a reset password link for a user.
     *
     * @param  userId       The id of the user to get a reset password link for.
     * @return User         The user that has gotten a password reset request.
     * @throws IOException  If an input or output exception occurred.
     */
    public ResetPasswordToken forgotPassword(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/forgot_password", userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    /**
     * Generates a reset password link for a user.
     *
     * @param  userId       The id of the user to get a reset password link for.
     * @param  sendEmail    Determines whether to notify user with an email.
     * @return User         The user that has gotten a password reset request.
     * @throws IOException  If an input or output exception occurred.
     */
    public ResetPasswordToken forgotPassword(String userId, boolean sendEmail) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sendEmail", String.valueOf(sendEmail));
        return post(getEncodedPathWithQueryParams("/%s/lifecycle/forgot_password", params, userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    /**
     * Reset password for a user, the user can no longer log in with the old password.
     *
     * @param  userId       The id of the user to reset password for.
     * @return User         The user that has gotten its password reset.
     * @throws IOException  If an input or output exception occurred.
     */
    public ResetPasswordToken resetPassword(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/reset_password", userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    /**
     * Reset password for a user, the user can no longer log in with the old password.
     *
     * @param  userId       The id of the user to reset password for.
     * @param  sendEmail    Determines whether to notify user with an email.
     * @return User         The user that has gotten its password reset.
     * @throws IOException  If an input or output exception occurred.
     */
    public ResetPasswordToken resetPassword(String userId, boolean sendEmail) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sendEmail", String.valueOf(sendEmail));
        return post(getEncodedPathWithQueryParams("/%s/lifecycle/reset_password", params, userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    /**
     * Reset factors for a user.
     *
     * @param  userId       The id of the user to reset factors for.
     * @return User         The user that has gotten its factors reset.
     * @throws IOException  If an input or output exception occurred.
     */
    public Map resetFactors(String userId) throws Exception {
        return post(getEncodedPath("/%s/lifecycle/reset_factors", userId), null, new TypeReference<Map>() { });
    }

    // User credentials

    /**
     * Set credentials for a user.
     *
     * @param  userId            The id of the user to set credentials for.
     * @param  loginCredentials  The login credentials for the user.
     * @return User              The user that has gotten its credentials set.
     * @throws IOException       If an input or output exception occurred.
     */
    public User setCredentials(String userId, LoginCredentials loginCredentials) throws IOException {
        User dummyUser = new User();
        dummyUser.setCredentials(loginCredentials);
        return put(getEncodedPath("/%s", userId), dummyUser, new TypeReference<User>() {
        });
    }

    /**
     * Set password for a user.
     *
     * @param  userId            The id of the user to set password for.
     * @param  userPassword      The password for the user.
     * @return User              The user that has gotten its password set.
     * @throws IOException       If an input or output exception occurred.
     */
    public User setPassword(String userId, String userPassword) throws IOException {
        LoginCredentials loginCredentials = new LoginCredentials();
        Password password = new Password();
        password.setValue(userPassword);
        loginCredentials.setPassword(password);
        return setCredentials(userId, loginCredentials);
    }

    /**
     * Set recovery question for a user.
     *
     * @param  userId            The id of the user to set recovery question for.
     * @param  question          The recovery question for the user.
     * @param  answer            The recovery answer for the user.
     * @return User              The user that has gotten its recovery question set.
     * @throws IOException       If an input or output exception occurred.
     */
    public User setRecoveryQuestion(String userId, String question, String answer) throws IOException {
        RecoveryQuestion recoveryQuestion = new RecoveryQuestion();
        recoveryQuestion.setQuestion(question);
        recoveryQuestion.setAnswer(answer);

        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setRecoveryQuestion(recoveryQuestion);

        return setCredentials(userId, loginCredentials);
    }

    /**
     * Change password for a user.
     *
     * @param  userId            The id of the user to change password for.
     * @param  oldPassword       The old password for the user.
     * @param  newPassword       The new password for the user.
     * @return LoginCredentials  The login credentials for the user that has gotten its password changed.
     * @throws IOException       If an input or output exception occurred.
     */
    public LoginCredentials changePassword(String userId, String oldPassword, String newPassword) throws IOException {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        Password oldPasswordObj = new Password();
        oldPasswordObj.setValue(oldPassword);
        changePasswordRequest.setOldPassword(oldPasswordObj);
        Password newPasswordObj = new Password();
        newPasswordObj.setValue(newPassword);
        changePasswordRequest.setNewPassword(newPasswordObj);

        return post(getEncodedPath("/%s/credentials/change_password", userId), changePasswordRequest,
                new TypeReference<LoginCredentials>() {
                });
    }

    /**
     * Change recovery question for a user.
     *
     * @param  userId            The id of the user to change recovery question for.
     * @param  currentPassword   The current password for the user.
     * @param  recoveryQuestion  The recovery question for the user.
     * @return LoginCredentials  The login credentials for the user that has gotten its recovery question changed.
     * @throws IOException       If an input or output exception occurred.
     */
    public LoginCredentials changeRecoveryQuestion(String userId, String currentPassword, RecoveryQuestion recoveryQuestion) throws IOException {
        ChangeRecoveryQuestionRequest changeRecoveryQuestionRequest = new ChangeRecoveryQuestionRequest();
        Password currentPasswordObj = new Password();
        currentPasswordObj.setValue(currentPassword);
        changeRecoveryQuestionRequest.setPassword(currentPasswordObj);
        changeRecoveryQuestionRequest.setRecoveryQuestion(recoveryQuestion);

        return post(getEncodedPath("/%s/credentials/change_password", userId), changeRecoveryQuestionRequest,
                new TypeReference<LoginCredentials>() {
                });
    }

    /**
     * Change recovery question for a user.
     *
     * @param  userId            The id of the user to change recovery question for.
     * @param  currentPassword   The current password for the user.
     * @param  question          The recovery question for the user.
     * @param  answer            The recovery answer for the user.
     * @return LoginCredentials  The login credentials for the user that has gotten its recovery question changed.
     * @throws IOException       If an input or output exception occurred.
     */
    public LoginCredentials changeRecoveryQuestion(String userId, String currentPassword, String question, String answer) throws IOException {
        RecoveryQuestion recoveryQuestion = new RecoveryQuestion();
        recoveryQuestion.setQuestion(question);
        recoveryQuestion.setAnswer(answer);
        return changeRecoveryQuestion(userId, currentPassword, recoveryQuestion);
    }

    /**
     * Get all users with paging info.
     *
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResults() throws IOException {
        return new PagedResults<User>(getUsersApiResponse());
    }

    /**
     * Get all users up to a limit with paging info.
     *
     * @param  limit               The max number of results returned.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithLimit(limit));
    }

    /**
     * Get all users with paging info that matches filter.
     *
     * @param  filterBuilder       The filter that's being used to match users.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithFilter(filterBuilder));
    }

    /**
     * Get all users up to a limit with paging info that matches filter.
     *
     * @param  filterBuilder       The filter that's being used to match users.
     * @param  limit               The max number of results returned.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithFilterAndLimit(filterBuilder, limit));
    }

    /**
     * Get all users up to a limit using cursor with paging info.
     *
     * @param  after               The cursor that determines which users to return after.
     * @param  limit               The max number of results returned.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsAfterCursorWithLimit(String after, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseAfterCursorWithLimit(after, limit));
    }

    /**
     * Get all users from url with paging info.
     *
     * @param  url                 The URL to get users from.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsByUrl(String url) throws IOException {
        return new PagedResults<User>(getUsersApiResponseByUrl(url));
    }

    /**
     * Get all users using advanced search with paging info.
     *
     * @param  filterBuilder       The advanced search filter thats being used to match users.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithAdvancedSearch(FilterBuilder filterBuilder) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithAdvancedSearch(filterBuilder));
    }

    /**
     * Get all users up to a limit using advanced search with paging info.
     *
     * @param  filterBuilder       The advanced search filter thats being used to match users.
     * @param  limit               The max number of results returned.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithAdvancedSearchAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithAdvancedSearchAndLimit(filterBuilder, limit));
    }

    /**
     * Get all users up to a limit using advanced search and cursor with paging info.
     *
     * @param  filterBuilder       The advanced search filter thats being used to match users.
     * @param  limit               The max number of results returned.
     * @param  after               The cursor that determines which users to return after.
     * @return PagedResults<User>  The users with paging info.
     * @throws IOException         If an input or output exception occurred.
     */
    public PagedResults<User> getUsersPagedResultsWithAdvancedSearchAndLimitAndAfterCursor(FilterBuilder filterBuilder, int limit, String after) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithAdvancedSearchAndLimitAndAfterCursor(filterBuilder, limit, after));
    }

    ////////////////////////////////////////////
    // API RESPONSE METHODS
    ////////////////////////////////////////////

    protected ApiResponse<List<User>> getUsersApiResponse() throws IOException {
        return getUsersApiResponseWithLimit(Utils.getDefaultResultsLimit());
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithLimit(int limit) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithFilter(FilterBuilder filterBuilder) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(FILTER, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseAfterCursorWithLimit(String after, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithAdvancedSearch(FilterBuilder filterBuilder) throws IOException {
        HttpResponse resp = getHttpResponse(getEncodedPath("?" + SEARCH + "=%s", filterBuilder.toString()));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithAdvancedSearchAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SEARCH, filterBuilder.toString());
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseWithAdvancedSearchAndLimitAndAfterCursor(FilterBuilder filterBuilder, int limit, String after) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SEARCH, filterBuilder.toString());
        params.put(AFTER_CURSOR, after);
        params.put(LIMIT, Integer.toString(limit));
        HttpResponse resp = getHttpResponse(getEncodedPathWithQueryParams("/", params));
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    protected ApiResponse<List<User>> getUsersApiResponseByUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/users%s", this.apiVersion, relativePath);
    }

}
