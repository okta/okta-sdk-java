package com.okta.sdk.resource.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.*;
import com.okta.sdk.resource.model.EmailCustomization;
import com.okta.sdk.resource.model.EmailDefaultContent;
import com.okta.sdk.resource.model.EmailPreview;
import com.okta.sdk.resource.model.EmailSettings;
import com.okta.sdk.resource.model.EmailSettingsResponse;
import com.okta.sdk.resource.model.EmailTemplateResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class CustomTemplatesApiTest {

    private ApiClient apiClient;
    private CustomTemplatesApi api;

    @Before
    public void setup() {
        apiClient = mock(ApiClient.class);
        api = new CustomTemplatesApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(i -> i.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");

        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            Object v = inv.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(inv.getArgument(0), String.valueOf(v)));
        });
        when(apiClient.parameterToPairs(anyString(), anyString(), any())).thenAnswer(inv -> {
            Object v = inv.getArgument(2);
            if (v == null) return Collections.emptyList();
            if (v instanceof Collection) {
                List<Pair> list = new ArrayList<>();
                for (Object o : (Collection<?>) v) {
                    list.add(new Pair(inv.getArgument(1), String.valueOf(o)));
                }
                return list;
            }
            return Collections.singletonList(new Pair(inv.getArgument(1), String.valueOf(v)));
        });
    }

    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class)
        )).thenReturn(value);
    }

    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull()
        )).thenReturn(null);
    }

    @Test
    public void testCreateEmailCustomization_Success() throws Exception {
        EmailCustomization expected = new EmailCustomization();
        stubInvoke(expected);
        EmailCustomization body = new EmailCustomization();

        EmailCustomization actual = api.createEmailCustomization("b1","welcome", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b1/templates/email/welcome/customizations"), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testCreateEmailCustomization_WithHeaders() throws Exception {
        stubInvoke(new EmailCustomization());
        api.createEmailCustomization("b2","welcome", new EmailCustomization(), Collections.singletonMap("X-C","1"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdr.getValue().get("X-C"));
    }

    @Test(expected = ApiException.class)
    public void testCreateEmailCustomization_MissingBrand() throws Exception {
        api.createEmailCustomization(null,"t", new EmailCustomization());
    }

    @Test(expected = ApiException.class)
    public void testCreateEmailCustomization_MissingTemplate() throws Exception {
        api.createEmailCustomization("b",null, new EmailCustomization());
    }

    @Test
    public void testDeleteAllCustomizations_Success() throws Exception {
        stubVoidInvoke();
        api.deleteAllCustomizations("b3","reset");

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b3/templates/email/reset/customizations"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test
    public void testDeleteAllCustomizations_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteAllCustomizations("b4","reset", Collections.singletonMap("X-D","v"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdr.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteAllCustomizations_MissingBrand() throws Exception {
        api.deleteAllCustomizations(null,"t");
    }

    @Test
    public void testDeleteEmailCustomization_Success() throws Exception {
        stubVoidInvoke();
        api.deleteEmailCustomization("b5","welcome","c1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b5/templates/email/welcome/customizations/c1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
    }

    @Test(expected = ApiException.class)
    public void testDeleteEmailCustomization_MissingCustomizationId() throws Exception {
        api.deleteEmailCustomization("b","t", null);
    }

    @Test
    public void testGetCustomizationPreview_Success() throws Exception {
        EmailPreview expected = new EmailPreview();
        stubInvoke(expected);
        EmailPreview actual = api.getCustomizationPreview("b6","welcome","c2");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b6/templates/email/welcome/customizations/c2/preview"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetEmailCustomization_Success() throws Exception {
        EmailCustomization expected = new EmailCustomization();
        stubInvoke(expected);
        assertSame(expected, api.getEmailCustomization("b7","welcome","c3"));

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b7/templates/email/welcome/customizations/c3"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetEmailDefaultContent_Success() throws Exception {
        EmailDefaultContent expected = new EmailDefaultContent();
        stubInvoke(expected);
        assertSame(expected, api.getEmailDefaultContent("b8","welcome","en"));

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b8/templates/email/welcome/default-content"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }


    @Test
    public void testGetEmailSettings_Success() throws Exception {
        EmailSettingsResponse expected = new EmailSettingsResponse();
        stubInvoke(expected);
        assertSame(expected, api.getEmailSettings("b10","welcome"));

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b10/templates/email/welcome/settings"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testGetEmailTemplate_WithExpand() throws Exception {
        EmailTemplateResponse expected = new EmailTemplateResponse();
        stubInvoke(expected);
        List<String> expand = Arrays.asList("customizations","settings");

        EmailTemplateResponse actual = api.getEmailTemplate("b11","welcome", expand);
        assertSame(expected, actual);

        ArgumentCaptor<List> collCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b11/templates/email/welcome"), eq("GET"),
            anyList(), collCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        List<Pair> pairs = (List<Pair>) collCap.getValue();
        assertTrue(pairs.stream().anyMatch(p -> p.getName().equals("expand")));
    }

    @Test
    public void testListEmailCustomizations_WithParams() throws Exception {
        List<EmailCustomization> expected = Arrays.asList(new EmailCustomization(), new EmailCustomization());
        stubInvoke(expected);

        // Last param is headers (per current API), not a search query
        Map<String,String> headers = Collections.singletonMap("X-H","v");
        List<EmailCustomization> actual = api.listEmailCustomizations("b12","welcome","after1",25, headers);
        assertSame(expected, actual);

        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b12/templates/email/welcome/customizations"), eq("GET"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        List<Pair> qp = (List<Pair>) qpCap.getValue();
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("after") && p.getValue().equals("after1")));
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("limit") && p.getValue().equals("25")));
    }

    @Test
    public void testReplaceEmailCustomization_Success() throws Exception {
        EmailCustomization expected = new EmailCustomization();
        stubInvoke(expected);
        EmailCustomization body = new EmailCustomization();

        EmailCustomization actual = api.replaceEmailCustomization("b14","welcome","c9", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b14/templates/email/welcome/customizations/c9"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testReplaceEmailCustomization_WithHeaders() throws Exception {
        stubInvoke(new EmailCustomization());
        api.replaceEmailCustomization("b15","welcome","c10", new EmailCustomization(), Collections.singletonMap("X-R","x"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", hdr.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceEmailCustomization_MissingCustomization() throws Exception {
        api.replaceEmailCustomization("b","t", null, new EmailCustomization());
    }

    @Test
    public void testReplaceEmailSettings_Success() throws Exception {
        EmailSettings expected = new EmailSettings();
        stubInvoke(expected);
        EmailSettings body = new EmailSettings();

        EmailSettings actual = api.replaceEmailSettings("b16","welcome", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b16/templates/email/welcome/settings"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
    }

    @Test
    public void testSendTestEmail_Success() throws Exception {
        stubVoidInvoke();
        api.sendTestEmail("b17","welcome","es");

        ArgumentCaptor<List> qpCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b17/templates/email/welcome/test"), eq("POST"),
            qpCap.capture(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());

        List<Pair> qp = (List<Pair>) qpCap.getValue();
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("language") && p.getValue().equals("es")));
    }

    @Test
    public void testSendTestEmail_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.sendTestEmail("b18","welcome","de", Collections.singletonMap("X-T","1"));

        ArgumentCaptor<Map> hdr = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdr.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("1", hdr.getValue().get("X-T"));
    }

    @Test(expected = ApiException.class)
    public void testSendTestEmail_MissingTemplate() throws Exception {
        api.sendTestEmail("b19", null, null);
    }
}
