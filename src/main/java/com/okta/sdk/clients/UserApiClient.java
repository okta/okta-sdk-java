package com.okta.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.framework.ApiResponse;
import com.okta.sdk.framework.FilterBuilder;
import com.okta.sdk.framework.JsonApiClient;
import com.okta.sdk.framework.PagedResults;
import com.okta.sdk.framework.Utils;
import com.okta.sdk.models.users.ChangePasswordRequest;
import com.okta.sdk.models.users.ChangeRecoveryQuestionRequest;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.Password;
import com.okta.sdk.models.users.RecoveryQuestion;
import com.okta.sdk.models.users.ResetPasswordToken;
import com.okta.sdk.models.users.TempPassword;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;
import mx4j.tools.config.DefaultConfigurationBuilder;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApiClient extends JsonApiClient {

    public UserApiClient(ApiClientConfiguration config) {
        super(config);
    }

    @Override
    protected String getFullPath(String relativePath) {
        return String.format("/api/v%d/users%s", this.apiVersion, relativePath);
    }

    ////////////////////////////////////////////
    // COMMON METHODS
    ////////////////////////////////////////////

    public List<User> getUsers() throws IOException {
        return getUsersWithLimit(Utils.getDefaultResultsLimit());
    }

    public List<User> getUsersWithQuery(String query) throws IOException {
        return get(getEncodedPath("?" + SEARCH_QUERY + "=%s", query), new TypeReference<List<User>>() { });
    }

    public List<User> getUsersWithLimit(int limit) throws IOException {
        return get(getEncodedPath("?" + LIMIT + "=%s", Integer.toString(limit)), new TypeReference<List<User>>() { });
    }

    public List<User> getUsersWithFilter(FilterBuilder filterBuilder) throws IOException {
        return get(getEncodedPath("?" + FILTER + "=%s", filterBuilder.toString()), new TypeReference<List<User>>() { });
    }

    public List<User> getUsersByUrl(String url) throws IOException {
        return get(url, new TypeReference<List<User>>() { });
    }

    // CRUD

    public User createUser(User user) throws IOException {
        return post(getEncodedPath("/"), user, new TypeReference<User>() { });
    }

    public User createUser(User user, boolean activate) throws IOException {
        return post(getEncodedPath("?activate=%s", String.valueOf(activate)), user, new TypeReference<User>() { });
    }

    public User createUser(String firstName, String lastName, String login, String email) throws IOException {
        return createUser(firstName, lastName, login, email, null);
    }

    public User createUser(String firstName, String lastName, String login, String email, String secondEmail) throws IOException {
        return createUser(firstName, lastName, login, email, secondEmail, null);
    }

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

    public User createUser(String firstName, String lastName, String login, String email, boolean activate) throws IOException {
        return createUser(firstName, lastName, login, email, null, null, activate);
    }

    public User createUser(String firstName, String lastName, String login, String email, String secondEmail, boolean activate) throws IOException {
        return createUser(firstName, lastName, login, email, secondEmail, null, activate);
    }

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

    public User getMyUser() throws IOException {
        return get(getEncodedPath("/me"), new TypeReference<User>() { });
    }

    public User getUser(String userId) throws IOException {
        return get(getEncodedPath("/%s", userId), new TypeReference<User>() { });
    }

    public User updateUser(User params) throws IOException {
        return put(getEncodedPath("/%s", params.getId()), params, new TypeReference<User>() { });
    }

    public User updateUser(String userId, User params) throws IOException {
        return put(getEncodedPath("/%s", userId), params, new TypeReference<User>() { });
    }

    public void deleteUser(String userId) throws IOException {
        delete(getEncodedPath("/%s", userId));
    }

    // LIFECYCLE MANAGEMENT

    public Map activateUser(String userId) throws IOException {
        return activateUser(userId, null);
    }

    public Map activateUser(String userId, Boolean sendEmail) throws IOException {
        if (sendEmail != null && sendEmail) {
            return post(getEncodedPath("/%s/lifecycle/activate", userId), null, new TypeReference<Map>() { });
        }
        else {
            return post(getEncodedPath("/%s/lifecycle/activate?sendEmail=false", userId), null, new TypeReference<Map>() { });
        }
    }

    public Map deactivateUser(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/deactivate", userId), null, new TypeReference<Map>() { });
    }

    public Map unlockUser(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/unlock", userId), null, new TypeReference<Map>() { });
    }

    public User expirePassword(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/expire_password", userId), null,
                new TypeReference<User>() { });
    }

    public TempPassword expirePassword(String userId, boolean tempPassword) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("tempPassword", String.valueOf(tempPassword));
        return post(getEncodedPathWithQueryParams("/%s/lifecycle/expire_password", params, userId), null,
                new TypeReference<TempPassword>() { });
    }

    public ResetPasswordToken forgotPassword(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/forgot_password", userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    public ResetPasswordToken forgotPassword(String userId, boolean sendEmail) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sendEmail", String.valueOf(sendEmail));
        return post(getEncodedPathWithQueryParams("/%s/lifecycle/forgot_password", params, userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    public ResetPasswordToken resetPassword(String userId) throws IOException {
        return post(getEncodedPath("/%s/lifecycle/reset_password", userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    public ResetPasswordToken resetPassword(String userId, boolean sendEmail) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sendEmail", String.valueOf(sendEmail));
        return post(getEncodedPathWithQueryParams("/%s/lifecycle/reset_password", params, userId), null,
                new TypeReference<ResetPasswordToken>() { });
    }

    public Map resetFactors(String userId) throws Exception {
        return post(getEncodedPath("/%s/lifecycle/reset_factors", userId), null, new TypeReference<Map>() { });
    }

    // CREDENTIAL MANAGEMENT

    public User setCredentials(String userId, LoginCredentials loginCredentials) throws IOException {
        User dummyUser = new User();
        dummyUser.setCredentials(loginCredentials);
        return put(getEncodedPath("/%s", userId), dummyUser, new TypeReference<User>() { });
    }

    public User setPassword(String userId, String userPassword) throws IOException {
        LoginCredentials loginCredentials = new LoginCredentials();
        Password password = new Password();
        password.setValue(userPassword);
        loginCredentials.setPassword(password);
        return setCredentials(userId, loginCredentials);
    }

    public User setRecoveryQuestion(String userId, String question, String answer) throws IOException {
        RecoveryQuestion recoveryQuestion = new RecoveryQuestion();
        recoveryQuestion.setQuestion(question);
        recoveryQuestion.setAnswer(answer);

        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setRecoveryQuestion(recoveryQuestion);

        return setCredentials(userId, loginCredentials);
    }

    public LoginCredentials changePassword(String userId, String oldPassword, String newPassword) throws IOException {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        Password oldPasswordObj = new Password();
        oldPasswordObj.setValue(oldPassword);
        changePasswordRequest.setOldPassword(oldPasswordObj);
        Password newPasswordObj = new Password();
        newPasswordObj.setValue(newPassword);
        changePasswordRequest.setNewPassword(newPasswordObj);

        return post(getEncodedPath("/%s/credentials/change_password", userId), changePasswordRequest,
                new TypeReference<LoginCredentials>() { });
    }

    public LoginCredentials changeRecoveryQuestion(String userId, String currentPassword, RecoveryQuestion recoveryQuestion) throws IOException {
        ChangeRecoveryQuestionRequest changeRecoveryQuestionRequest = new ChangeRecoveryQuestionRequest();
        Password currentPasswordObj = new Password();
        currentPasswordObj.setValue(currentPassword);
        changeRecoveryQuestionRequest.setPassword(currentPasswordObj);
        changeRecoveryQuestionRequest.setRecoveryQuestion(recoveryQuestion);

        return post(getEncodedPath("/%s/credentials/change_password", userId), changeRecoveryQuestionRequest,
                new TypeReference<LoginCredentials>() { });
    }

    public LoginCredentials changeRecoveryQuestion(String userId, String currentPassword, String question, String answer) throws IOException {
        RecoveryQuestion recoveryQuestion = new RecoveryQuestion();
        recoveryQuestion.setQuestion(question);
        recoveryQuestion.setAnswer(answer);
        return changeRecoveryQuestion(userId, currentPassword, recoveryQuestion);
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

    protected ApiResponse<List<User>> getUsersApiResponseByUrl(String url) throws IOException {
        HttpResponse resp = getHttpResponse(url);
        List<User> users = unmarshallResponse(new TypeReference<List<User>>() { }, resp);
        return new ApiResponse<List<User>>(resp, users);
    }

    ////////////////////////////////////////////
    // PAGED RESULTS METHODS
    ////////////////////////////////////////////

    public PagedResults<User> getUsersPagedResults() throws IOException {
        return new PagedResults<User>(getUsersApiResponse());
    }

    public PagedResults<User> getUsersPagedResultsWithLimit(int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithLimit(limit));
    }

    public PagedResults<User> getUsersPagedResultsWithFilter(FilterBuilder filterBuilder) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithFilter(filterBuilder));
    }

    public PagedResults<User> getUsersPagedResultsWithFilterAndLimit(FilterBuilder filterBuilder, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseWithFilterAndLimit(filterBuilder, limit));
    }

    public PagedResults<User> getUsersPagedResultsAfterCursorWithLimit(String after, int limit) throws IOException {
        return new PagedResults<User>(getUsersApiResponseAfterCursorWithLimit(after, limit));
    }

    public PagedResults<User> getUsersPagedResultsByUrl(String url) throws IOException {
        return new PagedResults<User>(getUsersApiResponseByUrl(url));
    }
}