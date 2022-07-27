package com.okta.sdk.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.okta.sdk.resource.common.PagedList;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.ApplicationApi;
import org.openapitools.client.api.IdentityProviderApi;
import org.openapitools.client.api.UserApi;
import org.openapitools.client.model.Application;
import org.openapitools.client.model.ApplicationSignOnMode;
import org.openapitools.client.model.BookmarkApplication;
import org.openapitools.client.model.BookmarkApplicationSettings;
import org.openapitools.client.model.BookmarkApplicationSettingsApplication;
import org.openapitools.client.model.CreateUserRequest;
import org.openapitools.client.model.Csr;
import org.openapitools.client.model.CsrMetadata;
import org.openapitools.client.model.CsrMetadataSubject;
import org.openapitools.client.model.CsrMetadataSubjectAltNames;
import org.openapitools.client.model.IdentityProvider;
import org.openapitools.client.model.IdentityProviderCredentials;
import org.openapitools.client.model.IdentityProviderCredentialsClient;
import org.openapitools.client.model.IdentityProviderPolicy;
import org.openapitools.client.model.IdentityProviderType;
import org.openapitools.client.model.IssuerMode;
import org.openapitools.client.model.PolicyAccountLink;
import org.openapitools.client.model.PolicyAccountLinkAction;
import org.openapitools.client.model.PolicySubject;
import org.openapitools.client.model.PolicySubjectMatchType;
import org.openapitools.client.model.PolicyUserNameTemplate;
import org.openapitools.client.model.Protocol;
import org.openapitools.client.model.ProtocolAlgorithmType;
import org.openapitools.client.model.ProtocolAlgorithmTypeSignature;
import org.openapitools.client.model.ProtocolAlgorithmTypeSignatureScope;
import org.openapitools.client.model.ProtocolAlgorithms;
import org.openapitools.client.model.ProtocolEndpoint;
import org.openapitools.client.model.ProtocolEndpointBinding;
import org.openapitools.client.model.ProtocolEndpointType;
import org.openapitools.client.model.ProtocolEndpoints;
import org.openapitools.client.model.ProtocolType;
import org.openapitools.client.model.Provisioning;
import org.openapitools.client.model.ProvisioningAction;
import org.openapitools.client.model.ProvisioningConditions;
import org.openapitools.client.model.ProvisioningDeprovisionedAction;
import org.openapitools.client.model.ProvisioningDeprovisionedCondition;
import org.openapitools.client.model.ProvisioningGroups;
import org.openapitools.client.model.ProvisioningGroupsAction;
import org.openapitools.client.model.ProvisioningSuspendedAction;
import org.openapitools.client.model.ProvisioningSuspendedCondition;
import org.openapitools.client.model.User;
import org.openapitools.client.model.UserProfile;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AppMain {

    public static void main(String... args) {

        ApiClient apiClient = buildApiClient(args[0], args[1]);

        testDriveAppsApi(apiClient);
        //testDriveUsersApi(apiClient);
        //testDriveIdpApi(apiClient);

//        BookmarkApplication bookmarkApplication = new BookmarkApplication();
//        bookmarkApplication.setName("bookmark");
//        bookmarkApplication.setLabel("Sample Bookmark App");
//        bookmarkApplication.setSignOnMode(ApplicationSignOnMode.BOOKMARK);
//        BookmarkApplicationSettings bookmarkApplicationSettings = new BookmarkApplicationSettings();
//        BookmarkApplicationSettingsApplication bookmarkApplicationSettingsApplication =
//            new BookmarkApplicationSettingsApplication();
//        bookmarkApplicationSettingsApplication.setUrl("https://example.com/bookmark.htm");
//        bookmarkApplicationSettingsApplication.setRequestIntegration(false);
//        bookmarkApplicationSettings.setApp(bookmarkApplicationSettingsApplication);
//        bookmarkApplication.setSettings(bookmarkApplicationSettings);
//
//        ResponseEntity<BookmarkApplication> responseEntity = apiClient.invokeAPI("/api/v1/apps",
//            HttpMethod.POST,
//            Collections.emptyMap(),
//            null,
//            bookmarkApplication,
//            new HttpHeaders(),
//            new LinkedMultiValueMap<>(),
//            null,
//            Collections.singletonList(MediaType.APPLICATION_JSON),
//            MediaType.APPLICATION_JSON,
//            new String[]{"API Token"},
//            new ParameterizedTypeReference<BookmarkApplication>() {});
//
//        responseEntity.getBody();
    }

    private static void testDriveAppsApi(ApiClient apiClient) {

        ApplicationApi applicationApi = new ApplicationApi(apiClient);
        BookmarkApplication bookmarkApplication = new BookmarkApplication();
        bookmarkApplication.setName("bookmark");
        bookmarkApplication.setLabel("Sample Bookmark App");
        bookmarkApplication.setSignOnMode(ApplicationSignOnMode.BOOKMARK);
        BookmarkApplicationSettings bookmarkApplicationSettings = new BookmarkApplicationSettings();
        BookmarkApplicationSettingsApplication bookmarkApplicationSettingsApplication =
            new BookmarkApplicationSettingsApplication();
        bookmarkApplicationSettingsApplication.setUrl("https://example.com/bookmark.htm");
        bookmarkApplicationSettingsApplication.setRequestIntegration(false);
        bookmarkApplicationSettings.setApp(bookmarkApplicationSettingsApplication);
        bookmarkApplication.setSettings(bookmarkApplicationSettings);

        BookmarkApplication createdApp = applicationApi.createApplication(BookmarkApplication.class, bookmarkApplication, true, "java-sdk-oasv3");
        assert createdApp != null;
        System.out.println("== CREATED APP ==\n" + createdApp);

        assert createdApp.getId() != null;
        System.out.println("== CREATED APP ID == " + createdApp.getId());

        sleep(Duration.ofSeconds(5));

        Application toBeUpdatedApp = bookmarkApplication.label("updated-" + bookmarkApplication.getLabel());
        System.out.println("== UPDATING APP ID == " + createdApp.getId());
        Application updatedApp = applicationApi.updateApplication(createdApp.getId(), toBeUpdatedApp);

        sleep(Duration.ofSeconds(10));
        assert Objects.equals(updatedApp.getId(), createdApp.getId());

        // generate csr
        CsrMetadata csrMetaData = new CsrMetadata();
        CsrMetadataSubject csrMetadataSubject = new CsrMetadataSubject();
        csrMetadataSubject.setCountryName("US");
        csrMetadataSubject.setStateOrProvinceName("California");
        csrMetadataSubject.setLocalityName("San Francisco");
        csrMetadataSubject.setOrganizationName("Okta, Inc.");
        csrMetadataSubject.setOrganizationalUnitName("Dev");
        csrMetadataSubject.setCommonName("SP Issuer");
        csrMetaData.setSubject(csrMetadataSubject);
        CsrMetadataSubjectAltNames csrMetadataSubjectAltNames = new CsrMetadataSubjectAltNames();
        csrMetadataSubjectAltNames.setDnsNames(Collections.singletonList("dev.okta.com"));
        csrMetaData.setSubjectAltNames(csrMetadataSubjectAltNames);
        Csr csr = applicationApi.generateCsrForApplication(createdApp.getId(), csrMetaData);
        assert csr != null;
        System.out.println("== CREATED CSR ==\n" + csr);

/*
        // publish csr
        JsonWebKey jsonWebKey = applicationApi.publishCsrFromApplication(createdApp.getId(), createdApp.getId(), new File("/Users/arvindkrishnakumar/Downloads/csr.pem"));
        assert jsonWebKey != null;
        System.out.println("== JSON WEB KEY ==\n" + jsonWebKey);
*/

        System.out.println("== DEACTIVATING APP ID == " + createdApp.getId());
        applicationApi.deactivateApplication(createdApp.getId());

        System.out.println("== DELETING APP ID == " + createdApp.getId());
        applicationApi.deleteApplication(createdApp.getId());
    }

    private static void testDriveUsersApi(ApiClient apiClient) {

        UserApi userApi = new UserApi(apiClient);
        List<User> users = userApi.listUsers(null, null, 5, null, null, null, null);
        System.out.println(users);
        System.out.println("\n==> Fetched # " + users.size() + " users!");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("John");
        userProfile.setLastName("OASv3");
        userProfile.setEmail(userProfile.getFirstName() + "-" + userProfile.getLastName() + "@delete.com");
        userProfile.setSecondEmail(userProfile.getFirstName() + "-" + userProfile.getLastName() + "@delete1.com");
        userProfile.setMobilePhone("1234567890");
        userProfile.setLogin(userProfile.getEmail());

        createUserRequest.setProfile(userProfile);
        User createdUser = userApi.createUser(createUserRequest, null, null, null);
        assert createdUser != null;

        System.out.println("== CREATED USER ==\n" + createdUser);
        System.out.println("== CREATED USER ID == " + createdUser.getId());

        System.out.println("== DEACTIVATING USER ID == " + createdUser.getId());
        userApi.deactivateOrDeleteUser(createdUser.getId(), false);

        System.out.println("== DELETING USER ID == " + createdUser.getId());
        userApi.deactivateOrDeleteUser(createdUser.getId(), false);

        PagedList usersPagedList = userApi.listUsersWithPaginationInfo(null, null, 52, null, null, null, null);
        System.out.println("== PAGINATED USERS LIST == " + usersPagedList);
    }

    private static void testDriveIdpApi(ApiClient apiClient) {

        IdentityProviderApi identityProviderApi = new IdentityProviderApi(apiClient);
        IdentityProvider identityProvider = new IdentityProvider();
        String name = "java-sdk-oasv3-" + UUID.randomUUID();
        identityProvider.setName(name);
        identityProvider.setType(IdentityProviderType.OIDC);
        identityProvider.setIssuerMode(IssuerMode.ORG_URL);
        Protocol protocol = new Protocol();

        ProtocolAlgorithmType protocolAlgorithmTypeReq = new ProtocolAlgorithmType();
        ProtocolAlgorithmType protocolAlgorithmTypeRes = new ProtocolAlgorithmType();
        ProtocolAlgorithmTypeSignature protocolAlgorithmTypeSignature_Req = new ProtocolAlgorithmTypeSignature();
        protocolAlgorithmTypeSignature_Req.setScope(ProtocolAlgorithmTypeSignatureScope.REQUEST);
        protocolAlgorithmTypeSignature_Req.setAlgorithm("SHA-256");
        protocolAlgorithmTypeReq.setSignature(protocolAlgorithmTypeSignature_Req);
        ProtocolAlgorithmTypeSignature protocolAlgorithmTypeSignature_Res = new ProtocolAlgorithmTypeSignature();
        protocolAlgorithmTypeSignature_Res.setScope(ProtocolAlgorithmTypeSignatureScope.RESPONSE);
        protocolAlgorithmTypeSignature_Res.setAlgorithm("SHA-256");
        protocolAlgorithmTypeRes.setSignature(protocolAlgorithmTypeSignature_Res);

        ProtocolAlgorithms protocolAlgorithms = new ProtocolAlgorithms();
        protocolAlgorithms.setRequest(protocolAlgorithmTypeReq);
        protocolAlgorithms.setResponse(protocolAlgorithmTypeRes);
        protocol.setAlgorithms(protocolAlgorithms);
        List<String> scopes = new ArrayList<>();
        scopes.add("openid");
        scopes.add("email");
        protocol.setScopes(scopes);
        protocol.setType(ProtocolType.OIDC);

        ProtocolEndpoint protocolEndpointIssuer = new ProtocolEndpoint();
        protocolEndpointIssuer.setUrl("issuer url");
        protocol.setIssuer(protocolEndpointIssuer);
        IdentityProviderCredentials identityProviderCredentials = new IdentityProviderCredentials();
        IdentityProviderCredentialsClient identityProviderCredentialsClient = new IdentityProviderCredentialsClient();
        identityProviderCredentialsClient.setClientId("clientId");
        identityProviderCredentialsClient.setClientSecret("clientSecret");
        identityProviderCredentials.setClient(identityProviderCredentialsClient);
        protocol.setCredentials(identityProviderCredentials);

        ProtocolEndpoints protocolEndpoints = new ProtocolEndpoints();
        ProtocolEndpoint protocolEndpointAcs = new ProtocolEndpoint();
        protocolEndpointAcs.setBinding(ProtocolEndpointBinding.POST);
        protocolEndpointAcs.setType(ProtocolEndpointType.INSTANCE);

        ProtocolEndpoint protocolEndpointAuthorization = new ProtocolEndpoint();
        protocolEndpointAuthorization.setBinding(ProtocolEndpointBinding.POST);
        protocolEndpointAuthorization.setUrl("https://idp.example.com/authorize");

        ProtocolEndpoint protocolEndpointToken = new ProtocolEndpoint();
        protocolEndpointToken.setBinding(ProtocolEndpointBinding.POST);
        protocolEndpointToken.setUrl("https://idp.example.com/token");

        ProtocolEndpoint protocolEndpointUserInfo = new ProtocolEndpoint();
        protocolEndpointUserInfo.setBinding(ProtocolEndpointBinding.REDIRECT);
        protocolEndpointUserInfo.setUrl("https://idp.example.com/userinfo");

        ProtocolEndpoint protocolEndpointJwks = new ProtocolEndpoint();
        protocolEndpointJwks.setBinding(ProtocolEndpointBinding.REDIRECT);
        protocolEndpointJwks.setUrl("https://idp.example.com/keys");

        protocolEndpoints.setAcs(protocolEndpointAcs);
        protocolEndpoints.setAuthorization(protocolEndpointAuthorization);
        protocolEndpoints.setToken(protocolEndpointToken);
        protocolEndpoints.setUserInfo(protocolEndpointUserInfo);
        protocolEndpoints.setJwks(protocolEndpointJwks);
        protocol.setEndpoints(protocolEndpoints);
        identityProvider.setProtocol(protocol);

        IdentityProviderPolicy identityProviderPolicy = new IdentityProviderPolicy();
        PolicyAccountLink policyAccountLink = new PolicyAccountLink();
        policyAccountLink.setAction(PolicyAccountLinkAction.AUTO);
        identityProviderPolicy.setAccountLink(policyAccountLink);
        Provisioning provisioning = new Provisioning();
        provisioning.setAction(ProvisioningAction.AUTO);
        ProvisioningConditions provisioningConditions = new ProvisioningConditions();
        ProvisioningDeprovisionedCondition provisioningDeprovisionedConditionDeprov = new ProvisioningDeprovisionedCondition();
        provisioningDeprovisionedConditionDeprov.setAction(ProvisioningDeprovisionedAction.NONE);
        provisioningConditions.setDeprovisioned(provisioningDeprovisionedConditionDeprov);
        ProvisioningSuspendedCondition provisioningDeprovisionedConditionSusp = new ProvisioningSuspendedCondition();
        provisioningDeprovisionedConditionSusp.setAction(ProvisioningSuspendedAction.NONE);
        provisioningConditions.setSuspended(provisioningDeprovisionedConditionSusp);
        provisioning.setConditions(provisioningConditions);
        identityProviderPolicy.setProvisioning(provisioning);
        identityProviderPolicy.setMaxClockSkew(1000);
        ProvisioningGroups provisioningGroups = new ProvisioningGroups();
        provisioningGroups.setAction(ProvisioningGroupsAction.NONE);
        provisioning.setGroups(provisioningGroups);
        identityProviderPolicy.setProvisioning(provisioning);
        PolicySubject policySubject = new PolicySubject();
        PolicyUserNameTemplate policyUserNameTemplate = new PolicyUserNameTemplate();
        policyUserNameTemplate.setTemplate("idpuser.email");
        policySubject.setUserNameTemplate(policyUserNameTemplate);
        policySubject.setMatchType(PolicySubjectMatchType.USERNAME);
        identityProviderPolicy.setSubject(policySubject);
        identityProvider.setPolicy(identityProviderPolicy);

        IdentityProvider createdIdp = identityProviderApi.createIdentityProvider(identityProvider);
        System.out.println("== CREATED IDENTITY PROVIDER ==\n" + createdIdp);

        assert createdIdp != null;
        assert createdIdp.getId() != null;
        System.out.println("== CREATED IDENTITY PROVIDER ID == " + createdIdp.getId());

        System.out.println("== DEACTIVATING IDENTITY PROVIDER ID == " + createdIdp.getId());
        identityProviderApi.deactivateIdentityProvider(createdIdp.getId());

        System.out.println("== DELETING IDENTITY PROVIDER ID == " + createdIdp.getId());
        identityProviderApi.deleteIdentityProvider(createdIdp.getId());
    }

    private static ApiClient buildApiClient(String orgBaseUrl, String apiKey) {

        ApiClient apiClient = new ApiClient(buildRestTemplate());
        apiClient.setBasePath(orgBaseUrl);
        apiClient.setApiKey(apiKey);
        apiClient.setApiKeyPrefix("SSWS");
        return apiClient;
    }

    private static RestTemplate buildRestTemplate() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        ObjectMapper mapper = messageConverter.getObjectMapper();
        messageConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.parseMediaType("application/x-pem-file"),
            MediaType.parseMediaType("application/x-x509-ca-cert"),
            MediaType.parseMediaType("application/pkix-cert")));
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new JsonNullableModule());

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(messageConverter);

        RestTemplate restTemplate = new RestTemplate(messageConverters);

        // This allows us to read the response more than once - Necessary for debugging.
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
        return restTemplate;
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            System.err.println("Sleep interrupted: " + e);
        }
    }
}
