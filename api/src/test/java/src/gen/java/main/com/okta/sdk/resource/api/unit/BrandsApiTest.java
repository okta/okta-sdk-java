package src.gen.java.main.com.okta.sdk.resource.api.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.client.ApiException;
import com.okta.sdk.resource.client.Pair;
import com.okta.sdk.resource.model.Brand;
import com.okta.sdk.resource.model.BrandRequest;
import com.okta.sdk.resource.model.BrandWithEmbedded;
import com.okta.sdk.resource.model.CreateBrandRequest;
import com.okta.sdk.resource.model.DomainResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BrandsApiTest {

    private ApiClient apiClient;
    private com.okta.sdk.resource.api.BrandsApi api;

    @Before
    public void setUp() {
        apiClient = mock(ApiClient.class);
        api = new com.okta.sdk.resource.api.BrandsApi(apiClient);

        when(apiClient.escapeString(anyString())).thenAnswer(inv -> inv.getArgument(0));
        when(apiClient.selectHeaderAccept(any(String[].class), anyString())).thenReturn("application/json");
        when(apiClient.selectHeaderContentType(any(String[].class))).thenReturn("application/json");

        when(apiClient.parameterToPair(anyString(), any())).thenAnswer(inv -> {
            String name = inv.getArgument(0);
            Object v = inv.getArgument(1);
            if (v == null) return Collections.emptyList();
            return Collections.singletonList(new Pair(name, String.valueOf(v)));
        });
        when(apiClient.parameterToPairs(anyString(), anyString(), any())).thenAnswer(inv -> {
            Object vals = inv.getArgument(2);
            if (vals == null) return Collections.emptyList();
            if (vals instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) vals;
                if (list.isEmpty()) return Collections.emptyList();
                String joined = list.stream().map(String::valueOf).collect(Collectors.joining(","));
                return Collections.singletonList(new Pair(inv.getArgument(1), joined));
            }
            return Collections.singletonList(new Pair(inv.getArgument(1), String.valueOf(vals)));
        });
    }

    // Helpers
    private <T> void stubInvoke(T value) throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class))
        ).thenReturn(value);
    }
    private void stubVoidInvoke() throws ApiException {
        when(apiClient.invokeAPI(
            anyString(), anyString(),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull())
        ).thenReturn(null);
    }

    // createBrand
    @Test
    public void testCreateBrand_Success() throws Exception {
        Brand expected = new Brand();
        stubInvoke(expected);

        Brand actual = api.createBrand(new CreateBrandRequest());
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands"), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
    }

    @Test
    public void testCreateBrand_NullBodyAllowed() throws Exception {
        Brand expected = new Brand();
        stubInvoke(expected);

        api.createBrand(null);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertNull(bodyCap.getValue());
    }

    @Test
    public void testCreateBrand_WithHeaders() throws Exception {
        stubInvoke(new Brand());
        api.createBrand(new CreateBrandRequest(), Collections.singletonMap("X-C","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("POST"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-C"));
    }

    // deleteBrand
    @Test
    public void testDeleteBrand_Success() throws Exception {
        stubVoidInvoke();
        api.deleteBrand("b1");

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b1"), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        verify(apiClient).escapeString("b1");
    }

    @Test
    public void testDeleteBrand_WithHeaders() throws Exception {
        stubVoidInvoke();
        api.deleteBrand("b2", Collections.singletonMap("X-D","v"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("DELETE"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), isNull());
        assertEquals("v", hdrCap.getValue().get("X-D"));
    }

    @Test(expected = ApiException.class)
    public void testDeleteBrand_MissingId() throws Exception {
        api.deleteBrand(null);
    }

    // getBrand
    @Test
    public void testGetBrand_SuccessWithExpand() throws Exception {
        BrandWithEmbedded expected = new BrandWithEmbedded();
        stubInvoke(expected);

        List<String> expand = Arrays.asList("themes","email");
        BrandWithEmbedded actual = api.getBrand("b3", expand);
        assertSame(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> collCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b3"), eq("GET"),
            anyList(), collCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        @SuppressWarnings("unchecked")
        List<Pair> colPairs = collCap.getValue();
        assertTrue(colPairs.stream().anyMatch(p -> p.getName().equals("expand") && p.getValue().equals("themes,email")));
        verify(apiClient).escapeString("b3");
    }

    @Test
    public void testGetBrand_NullExpand() throws Exception {
        stubInvoke(new BrandWithEmbedded());
        api.getBrand("b4", null);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> collCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), collCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertTrue(((List<?>) collCap.getValue()).isEmpty());
    }

    @Test
    public void testGetBrand_WithHeaders() throws Exception {
        stubInvoke(new BrandWithEmbedded());
        api.getBrand("b5", Collections.singletonList("themes"), Collections.singletonMap("X-G","g"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("g", hdrCap.getValue().get("X-G"));
    }

    @Test(expected = ApiException.class)
    public void testGetBrand_MissingId() throws Exception {
        api.getBrand(null, null);
    }

    // listBrandDomains
    @Test
    public void testListBrandDomains_Success() throws Exception {
        List<DomainResponse> expected = Arrays.asList(new DomainResponse(), new DomainResponse());
        stubInvoke(expected);

        List<DomainResponse> actual = api.listBrandDomains("b6");
        assertSame(expected, actual);

        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b6/domains"), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        verify(apiClient).escapeString("b6");
    }

    @Test
    public void testListBrandDomains_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listBrandDomains("b7", Collections.singletonMap("X-LD","1"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("1", hdrCap.getValue().get("X-LD"));
    }

    @Test(expected = ApiException.class)
    public void testListBrandDomains_MissingId() throws Exception {
        api.listBrandDomains(null);
    }

    // listBrands
    @Test
    public void testListBrands_SuccessWithParams() throws Exception {
        List<BrandWithEmbedded> expected = Arrays.asList(new BrandWithEmbedded(), new BrandWithEmbedded());
        stubInvoke(expected);

        List<BrandWithEmbedded> actual = api.listBrands(Arrays.asList("themes"), "cursor1", 42, "query");
        assertSame(expected, actual);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> queryCap = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> collCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands"), eq("GET"),
            queryCap.capture(), collCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));

        @SuppressWarnings("unchecked")
        List<Pair> qp = queryCap.getValue();
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("after") && p.getValue().equals("cursor1")));
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("limit") && p.getValue().equals("42")));
        assertTrue(qp.stream().anyMatch(p -> p.getName().equals("q") && p.getValue().equals("query")));

        @SuppressWarnings("unchecked")
        List<Pair> cp = collCap.getValue();
        assertTrue(cp.stream().anyMatch(p -> p.getName().equals("expand") && p.getValue().equals("themes")));
    }

    @Test
    public void testListBrands_AllNullOptionals() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listBrands(null, null, null, null);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> queryCap = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<List> collCap = ArgumentCaptor.forClass(List.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            queryCap.capture(), collCap.capture(),
            anyString(), isNull(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertTrue(((List<?>) queryCap.getValue()).isEmpty());
        assertTrue(((List<?>) collCap.getValue()).isEmpty());
    }

    @Test
    public void testListBrands_WithHeaders() throws Exception {
        stubInvoke(Collections.emptyList());
        api.listBrands(null, null, null, null, Collections.singletonMap("X-LB","x"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("GET"),
            anyList(), anyList(),
            anyString(), isNull(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("x", hdrCap.getValue().get("X-LB"));
    }

    // replaceBrand
    @Test
    public void testReplaceBrand_Success() throws Exception {
        Brand expected = new Brand();
        stubInvoke(expected);
        BrandRequest body = new BrandRequest();

        Brand actual = api.replaceBrand("b8", body);
        assertSame(expected, actual);

        ArgumentCaptor<Object> bodyCap = ArgumentCaptor.forClass(Object.class);
        verify(apiClient).invokeAPI(
            eq("/api/v1/brands/b8"), eq("PUT"),
            anyList(), anyList(),
            anyString(), bodyCap.capture(),
            anyMap(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertSame(body, bodyCap.getValue());
        verify(apiClient).escapeString("b8");
    }

    @Test
    public void testReplaceBrand_WithHeaders() throws Exception {
        stubInvoke(new Brand());
        api.replaceBrand("b9", new BrandRequest(), Collections.singletonMap("X-R","r"));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> hdrCap = ArgumentCaptor.forClass(Map.class);
        verify(apiClient).invokeAPI(
            anyString(), eq("PUT"),
            anyList(), anyList(),
            anyString(), any(),
            hdrCap.capture(), anyMap(), anyMap(),
            anyString(), anyString(),
            any(String[].class), any(TypeReference.class));
        assertEquals("r", hdrCap.getValue().get("X-R"));
    }

    @Test(expected = ApiException.class)
    public void testReplaceBrand_MissingId() throws Exception {
        api.replaceBrand(null, new BrandRequest());
    }

    @Test(expected = ApiException.class)
    public void testReplaceBrand_MissingBrand() throws Exception {
        api.replaceBrand("b10", null);
    }
}
